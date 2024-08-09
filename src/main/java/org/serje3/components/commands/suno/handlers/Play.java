package org.serje3.components.commands.suno.handlers;

import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.components.buttons.music.AddToQueueButton;
import org.serje3.components.commands.music.queue.QueueCommand;
import org.serje3.meta.interfaces.CommandExecutable;
import org.serje3.rest.domain.SunoClip;
import org.serje3.rest.handlers.SunoRestHandler;
import org.serje3.rest.requests.SaveRecentTrackRequest;
import org.serje3.services.MusicService;
import org.serje3.services.EmbedService;
import org.serje3.utils.VoiceHelper;

public class Play implements CommandExecutable {
    private final SunoRestHandler sunoRestHandler = new SunoRestHandler();
    private final MusicService musicService = new MusicService();

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        int playIndex = event.getOption("index").getAsInt();
        if (playIndex < 0) {
            event.reply("Неверный индекс").setEphemeral(true).queue();
            return;
        }


        int page = playIndex / 20;

        int localPlayIndex = playIndex % 20;

        sunoRestHandler.feed(event.getUser().getIdLong(), page).thenAccept((clips) -> {
            try{
                SunoClip clip = clips.get(localPlayIndex);
                if (clip.getAudioUrl() == null){
                    event.getHook().sendMessage("Аудио ещё не готово").queue();
                    return;
                }
                VoiceHelper.joinMemberVoiceChannel(event);
                new QueueCommand().play(event, event.getGuild().getIdLong(), clip.getAudioUrl(), 35, ignored -> {
                    event.getHook().sendMessageEmbeds(
                            EmbedService.getInstance().getMessageTrackEmbed(
                                    clip.getVideoUrl(),
                                    clip.getTitle(),
                                    "Жанр: " + clip.getMetadata().get("tags"),
                                    "suno.com",
                                    clip.getImageUrl(),
                                    event.getMember(),
                                    event.getMember().getEffectiveName(),
                                    clip.getImageLargeUrl()
                            )
                    ).addActionRow(new AddToQueueButton().asJDAButton()).queue();
                });
                musicService.saveRecentTrack(SaveRecentTrackRequest.builder()
                                .trackName(clip.getTitle())
                                .url(clip.getAudioUrl())
                                .guildId(event.getGuild().getIdLong())
                        .build() );
            } catch (IndexOutOfBoundsException e) {
                event.getHook().sendMessage("Индекс неверный: " + e.getLocalizedMessage()).queue();
            }
        }).exceptionally((e) -> {
            event.getHook().sendMessage("Пиздец баля: " + e.getLocalizedMessage()).queue();
            return null;
        });
    }
}
