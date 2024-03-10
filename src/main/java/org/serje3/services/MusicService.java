package org.serje3.services;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.LavalinkPlayer;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.PlayerUpdateBuilder;
import dev.arbjerg.lavalink.client.loadbalancing.VoiceRegion;
import dev.arbjerg.lavalink.client.protocol.Track;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.serje3.components.buttons.music.PauseButton;
import org.serje3.components.buttons.music.PausePlayButton;
import org.serje3.components.buttons.music.RepeatButton;
import org.serje3.components.buttons.music.SkipButton;
import org.serje3.domain.TrackContext;
import org.serje3.meta.enums.PlaySourceType;
import org.serje3.utils.SlashEventHelper;
import org.serje3.utils.TrackQueue;
import org.serje3.utils.VoiceHelper;
import org.serje3.utils.exceptions.NoTrackIsPlayingNow;
import org.serje3.utils.exceptions.PoshelNaxyiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class MusicService {
    private final Logger logger = LoggerFactory.getLogger(MusicService.class);

    public void pauseMusic(SlashCommandInteractionEvent event, LavalinkClient client) {
        pauseMusic(event.getGuild().getIdLong(), client)
                .subscribe((player) -> {
                    event.reply("Плеер " + (player.getPaused() ? "на паузе" : "возобновлён") + "!").queue();
                });
    }

    public Mono<LavalinkPlayer> pauseMusic(Long guildId, LavalinkClient client) {
        return client.getLink(guildId)
                .getPlayer()
                .flatMap((player) -> {
                    PlayerUpdateBuilder playerUpdateBuilder = player.setPaused(!player.getPaused());
                    TrackQueue.pause(guildId, player.getPaused());
                    return playerUpdateBuilder;
                });
    }


    public MessageEmbed whatsPlayingNow(Member member, LavalinkPlayer player) throws NoTrackIsPlayingNow {
        Track track = player.getTrack();
        if (track == null || player.getPosition() >= track.getInfo().getLength()) {
            throw new NoTrackIsPlayingNow();
        }
        ;
        return VoiceHelper.wrapTrackEmbed(track, member, "");
    }

    public void skipTrack(SlashCommandInteractionEvent event, LavalinkClient client) {
        event.deferReply().queue();
        long guildId = event.getGuild().getIdLong();
        Link link = client.getLink(event.getGuild().getIdLong());
        TrackQueue.repeat(guildId, false);
        link.getPlayer().subscribe(player -> {
            try {
                event.getHook().sendMessage(skipTrack(event.getMember(), link, player)).queue();
            } catch (NoTrackIsPlayingNow e) {
                event.getHook().sendMessage(e.getMessage()).queue();
            }
        }, (e) -> {
            event.getHook().sendMessage("Что-то пошло не по плану (а возможно и по плану)").queue();
        });
    }

    public void skipTrack(ButtonInteractionEvent event, LavalinkClient client) {
        event.deferReply().queue();
        long guildId = event.getGuild().getIdLong();
        Link link = client.getLink(guildId);
        TrackQueue.repeat(guildId, false);
        link.getPlayer().subscribe(player -> {
            try {
                event.getHook().sendMessage(skipTrack(event.getMember(), link, player)).queue();
            } catch (NoTrackIsPlayingNow e) {
                event.getHook().sendMessage(e.getMessage()).queue();
            }
        }, (e) -> {
            event.getHook().sendMessage("Что-то пошло не по плану (а возможно и по плану)").queue();
        });
    }

    public String getSearchPrefix(String subCommandName, String identifier) {
        final PlaySourceType playType = PlaySourceType.valueOf(subCommandName.toUpperCase());
        String prefix = playType.getSearchMask();

        if (identifier.startsWith("https://")) {
            prefix = "";
        }
        return prefix;
    }


    private String skipTrack(Member member, Link link, LavalinkPlayer player) throws NoTrackIsPlayingNow {

        final Track track = player.getTrack();
        if (track == null || !player.getState().getConnected()) {
            TrackQueue.clearNow(member.getGuild().getIdLong());
            throw new NoTrackIsPlayingNow();
        }

        logger.info("SKIPPING {}", track.getInfo().getTitle());
        logger.info("isStream {}", track.getInfo().isStream());
        logger.info("isSeekable {}", track.getInfo().isSeekable());
        link.createOrUpdatePlayer().stopTrack().subscribe(p -> {
            logger.info("Track {} skipped", track.getInfo().getTitle());
        });
        String mention = (member != null ? member.getAsMention() : "Анон");
        if (track.getInfo().isStream()) {
            return "Стрим выключен, " + mention;
        } else {
            return "Трек пропущен, " + mention;
        }
    }

    public void whatsPlayingNowWithoutInteraction(TextChannel textChannel, TrackContext track) {
        try {
            if (track == null) throw new NoTrackIsPlayingNow();
            textChannel.sendMessageEmbeds(VoiceHelper.wrapTrackEmbed(track.getTrack(), track.getMember(), ""))
                    .addActionRow(
                            track.isRepeat() ? new RepeatButton.On().asJDAButton() : new RepeatButton().asJDAButton(),
                            track.isPaused() ? new PausePlayButton().asJDAButton() : new PauseButton().asJDAButton(),
                            new SkipButton().asJDAButton(),
                            Button.link(track.getTrack().getInfo().getUri(), "Ссылка на трек")
                    ).queue();
        } catch (NoTrackIsPlayingNow e) {
            // ниче не делаем
        }
    }

    public boolean queue(Track track, Long guildId, Member member, TextChannel textChannel, LavalinkClient client){
        if (track == null){
            return false;
        }
        TrackQueue.add(guildId, SlashEventHelper.createTrackContextFromEvent(track, member, textChannel));

        logger.info("Размер очереди - {}", TrackQueue.size(guildId));
        Link link = client.getLink(guildId, VoiceRegion.RUSSIA);
        VoiceHelper.queue(client, link, guildId);
        return true;
    }

    public boolean queue(List<Track> tracks, Long guildId, Member member, TextChannel textChannel, LavalinkClient client){
        if (tracks == null || tracks.isEmpty() || tracks.size() > 10000) {
            return false;
        }
        List<TrackContext> trackContextList = tracks.stream()
                .map(track -> SlashEventHelper.createTrackContextFromEvent(track, member, textChannel)).toList();
        TrackQueue.addAll(guildId, trackContextList);

        logger.info("Размер очереди - {}", TrackQueue.size(guildId));
        Link link = client.getLink(guildId, VoiceRegion.RUSSIA);
        VoiceHelper.queue(client, link, guildId);
        return true;
    }
}
