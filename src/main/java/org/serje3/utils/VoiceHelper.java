package org.serje3.utils;

import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.Track;
import io.sentry.Sentry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.serje3.domain.TrackContext;
import org.serje3.services.LavalinkService;
import org.serje3.utils.exceptions.NoTracksInQueueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


public class VoiceHelper {
    private static final Logger logger = LoggerFactory.getLogger(VoiceHelper.class);


    public static void joinMemberVoiceChannel(GenericInteractionCreateEvent event) {
        final Member member = event.getMember();
        final Guild guild = event.getGuild();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        guild.getAudioManager().setSelfDeafened(true);
        if (memberVoiceState.inAudioChannel()) {
            event.getJDA().getDirectAudioController().connect(memberVoiceState.getChannel());
        }
    }

    public static void play(Link link, TrackContext context, Integer volume) {
        Track track = context.getTrack();
        if (track == null && context.getIdentifier() != null) {
            context.retrieveTrackByIdentifier(_track -> {
                turnOnTrack(link, _track, volume);
            }, (res) -> {});
            return;
        }
        if (track == null) {
            throw new RuntimeException();
        }
        turnOnTrack(link, track, volume);
    }


    private static void turnOnTrack(Link link, Track track, Integer volume) {
        link.createOrUpdatePlayer()
                .setTrack(track)
                .setVolume(volume)
                .setNoReplace(true)
                .setEndTime(track.getInfo().getLength())
                .subscribe((ignored) -> {
                }, Sentry::captureException);
    }

    public static void queue(Link link, Long guildId) {
        link.getPlayer().subscribe((player) -> {

            logger.info("Queue. Player state - {}", player.getState());
            boolean isStopped = !player.getState().getConnected() || player.getTrack() == null
                    || player.getPosition() >= player.getTrack().getInfo().getLength();

            if (isStopped) {
                try {
                    logger.info("START QUEUE");
                    TrackQueue.skip(guildId, true);
                } catch (NoTracksInQueueException e) {
                    // Такое может произойти в очень редких случаях
                    // с учётом того что перед тем как запустить queue,
                    // мы добавляем трек в TrackQueue
                    // В случае если это все-таки произошло - удалите system32,
                    // а если вы на linux или macos, то напишите rm -rf /. И проблема исчезнет
                }
            }
        }, Sentry::captureException);
    }


    public static void disconnect(JDA jda, Guild guild) {
        jda.getDirectAudioController().disconnect(Objects.requireNonNull(guild));
        clearPlayerForGuild(guild);
    }

    public static void clearPlayerForGuild(Guild guild) {
        TrackQueue.clear(guild.getIdLong());
        LavalinkService.getInstance().destroyLink(guild.getIdLong());
    }
}
