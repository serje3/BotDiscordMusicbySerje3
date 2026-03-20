package org.serje3.utils;

import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.LinkState;
import dev.arbjerg.lavalink.client.player.LavalinkPlayer;
import dev.arbjerg.lavalink.client.player.Track;
import io.sentry.Sentry;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.serje3.utils.exceptions.NoTracksInQueueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

public class VoiceHelper {
    private static final Logger logger = LoggerFactory.getLogger(VoiceHelper.class);


    public static void joinHelper(GenericInteractionCreateEvent event) {
        final Member member = event.getMember();
        final Guild guild = event.getGuild();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        guild.getAudioManager().setSelfDeafened(true);
        if (memberVoiceState.inAudioChannel()) {
            event.getJDA().getDirectAudioController().connect(memberVoiceState.getChannel());
        }
    }

    public static void play(Link link, Track track, Integer volume) {
        try {
            link.createOrUpdatePlayer()
                    .setTrack(track)
                    .setVolume(volume)
                    .setNoReplace(false)
                    .setEndTime(track.getInfo().getLength())
                    .block(Duration.ofSeconds(30));
        } catch (Exception e) {
            Sentry.captureException(e);
        }
    }


    public static MessageEmbed wrapTrackEmbed(Track track, Member member, String description) {
        if (track == null) return null;
        String title = track.getInfo().getTitle();
        String author = track.getInfo().getAuthor();
        String url = track.getInfo().getUri();
        String artUrl = track.getInfo().getArtworkUrl();
        String source = track.getInfo().getSourceName();
        try {
            URI uri = new URI("https", "cataas.com", "/cat/says/" + title, "fontSize=30&fontColor=red", null);
            String thumbnailUrl = uri.toString();

            System.out.println(thumbnailUrl);

            return getMessageEmbed(url, title, description, source, thumbnailUrl, member, author, artUrl);
        } catch (URISyntaxException e) {
            Sentry.captureException(e);
            throw new RuntimeException(e);
        }
    }

    public static MessageEmbed getMessageEmbed(String url,
                                               String title,
                                               String description,
                                               String source,
                                               String thumbnailUrl,
                                               Member member,
                                               String author,
                                               String artUrl
    ) {
        return new MessageEmbed(
                url,
                title,
                description + "\nИсточник: " + source,
                EmbedType.AUTO_MODERATION,
                null,
                member.getColorRaw(),
                new MessageEmbed.Thumbnail(thumbnailUrl, thumbnailUrl, 100, 100),
                null,
                new MessageEmbed.AuthorInfo(author, url, null, null),
                null,
                new MessageEmbed.Footer(member.getEffectiveName(), member.getEffectiveAvatarUrl(), member.getEffectiveAvatarUrl()),
                new MessageEmbed.ImageInfo(artUrl, artUrl, 100, 100),
                null
        );
    }

    public static void queue(Link link, Long guildId) {
        synchronized (TrackQueue.playbackLock(guildId)) {
            try {
                LavalinkPlayer player = link.getPlayer().block(Duration.ofSeconds(15));
                if (player == null) {
                    return;
                }
                logger.info("Queue. Player state - {}", player.getState());
                if (!shouldStartNextFromQueue(link, player)) {
                    return;
                }
                logger.info("START QUEUE");
                TrackQueue.skip(guildId, false);
            } catch (NoTracksInQueueException e) {
                // гонка: очередь опустели между add и skip
            } catch (Exception e) {
                Sentry.captureException(e);
            }
        }
    }

    /**
     * True, если плеер не воспроизводит трек сейчас и нужно взять следующий из очереди.
     * Учитывает link/voice, паузу, конец трека и стримы (длина ~∞).
     */
    static boolean shouldStartNextFromQueue(Link link, LavalinkPlayer player) {
        if (link.getState() != LinkState.CONNECTED) {
            return true;
        }
        if (!player.getState().getConnected()) {
            return true;
        }
        if (player.getTrack() == null) {
            return true;
        }
        if (player.getPaused()) {
            return false;
        }
        Track track = player.getTrack();
        if (track.getInfo().isStream()) {
            return false;
        }
        long length = track.getInfo().getLength();
        if (length <= 0) {
            return false;
        }
        return player.getPosition() >= length;
    }
}
