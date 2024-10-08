package org.serje3.components.buttons.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.player.LoadFailed;
import dev.arbjerg.lavalink.client.player.NoMatches;
import dev.arbjerg.lavalink.client.player.Track;
import dev.arbjerg.lavalink.client.player.TrackLoaded;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.serje3.meta.abs.Button;
import org.serje3.rest.handlers.YoutubeRestHandler;
import org.serje3.services.LavalinkService;
import org.serje3.services.MusicService;
import org.serje3.utils.VoiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class AddToQueueButton extends Button {
    private final Logger logger = LoggerFactory.getLogger(AddToQueueButton.class);
    private final MusicService musicService = new MusicService();
    private final YoutubeRestHandler youtubeRestHandler = new YoutubeRestHandler();

    public AddToQueueButton() {
    }

    @Override
    public String getComponentId() {
        return "add-to-queue";
    }

    @Override
    public ButtonType getButtonType() {
        return ButtonType.SECONDARY;
    }

    @Override
    public void handle(ButtonInteractionEvent event) {
        // логика сильно связана с URL на видео в title
        event.deferReply().queue();

        joinChannel(event);

        Long guildId = Objects.requireNonNull(event.getGuild()).getIdLong();
        Member member = event.getMember();
        TextChannel textChannel = event.getChannel().asTextChannel();
        MessageEmbed embed = event.getMessage().getEmbeds().get(0);
        String identifier = embed.getUrl();
        if (identifier == null) {
            String authorPrefix = embed.getAuthor() != null ? embed.getAuthor().getName() + " - " : "";
            identifier = youtubeRestHandler.trySearchCachedUrl( authorPrefix + embed.getTitle());
            if (identifier == null) {
                event.getHook().sendMessage("Ничего не найдено по вашему запросу!").queue();
                return;
            }
        }

        LavalinkService.getInstance().getLink(guildId)
                .loadItem(identifier)
                .subscribe((item) -> {
                    if (item instanceof TrackLoaded trackLoaded) {
                        Track track = trackLoaded.getTrack();
                        track = musicService.cockinizeTrackIfNowIsTheTime(guildId, track);
                        musicService.queue(track, guildId, member, textChannel);
                        event.getHook().sendMessage("Успешно добавлено - " + track.getInfo().getTitle() + "!").queue();
                    } else if (item instanceof NoMatches) {
                        event.getHook().sendMessage("Ничего не найдено по вашему запросу!").queue();
                    } else if (item instanceof LoadFailed fail) {
                        event.getHook().sendMessage("ЕБАТЬ НЕ ПОЛУЧИЛОСЬ ЗАГРУЗИТЬ АУДИО! Ашибка: " + fail.getException().getMessage()).queue();
                    } else {
                        logger.error("Thats not supposed to be happend {}", item);
                        event.getHook().sendMessage("Какая-то хуйня. О-о-обратитесь к адддддддминистратору пжжжжжж~~~ пук среньк").queue();
                    }
                });
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public Emoji getLabelEmoji() {
        return Emoji.fromFormatted("<:Plus48ThemeFilled:1248968099387019274>");
    }


    private void joinChannel(ButtonInteractionEvent event) {
        final Guild guild = event.getGuild();
        // We are already connected, go ahead and play
        if (!guild.getSelfMember().getVoiceState().inAudioChannel()) {
            // Connect to VC first
            VoiceHelper.joinHelper(event);
        }
    }


}
