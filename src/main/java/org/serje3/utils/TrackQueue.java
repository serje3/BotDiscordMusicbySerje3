package org.serje3.utils;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.Track;
import org.serje3.domain.TrackContext;
import org.serje3.services.LavalinkService;
import org.serje3.utils.exceptions.NoTracksInQueueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TrackQueue {
    private static final ConcurrentHashMap<Long, Deque<TrackContext>> tracksQueue = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, TrackContext> tracksNow = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(TrackQueue.class);

    private static void init(Long guildId) {
        Set<Long> keySet = tracksQueue.keySet();
        if (!keySet.contains(guildId)) {
            tracksQueue.put(guildId, new ArrayDeque<>());
        }
    }

    public static void add(Long guildId, TrackContext track) {
        init(guildId);

        tracksQueue.get(guildId).addLast(track);
    }

    public static void addAll(Long guildId, Collection<TrackContext> tracks) {
        init(guildId);

        tracksQueue.get(guildId).addAll(tracks);
    }

    public static TrackContext skip(Long guildId, boolean emitByEvent) throws NoTracksInQueueException {
        // return: next track
        init(guildId);
        TrackContext trackNow = peekNow(guildId);
        TrackContext trackContext;
        if (emitByEvent && trackNow != null && trackNow.isRepeat()) {
            trackContext = trackNow;
        } else {
            trackContext = TrackQueue.pop(guildId);
        }

        if (trackContext == null) {
            clearNow(guildId);
            throw new NoTracksInQueueException();
        }
        Track track = trackContext.getTrack();
        logger.info("Guild: {}. Current tracks in queue is {}",guildId, TrackQueue.tracksQueue.get(guildId));
        Link link = LavalinkService.getInstance().getLink(guildId);
        logger.info("Guild: {}. Next track is {}", guildId, track.getInfo().getTitle());
        VoiceHelper.play(link, track, 35);
        tracksNow.put(guildId, trackContext);
        return trackContext;
    }

    public static TrackContext repeat(Long guildId, Boolean repeat) {
        return tracksNow.computeIfPresent(guildId, (id, trackContext) -> {
            trackContext.setRepeat(repeat);
            return trackContext;
        });
    }

    public static void pause(Long guildId, boolean paused) {
        tracksNow.computeIfPresent(guildId, (id, trackContext) -> {
            trackContext.setPaused(paused);
            return trackContext;
        });
    }

    public static Boolean toggleRepeat(Long guildId) {
        TrackContext trackContext = tracksNow.get(guildId);
        if (trackContext == null) return false;
        TrackContext updatedTrackContext = repeat(guildId, !trackContext.isRepeat());
        return updatedTrackContext.isRepeat();
    }

    public static TrackContext peekNow(Long guildId) {
        init(guildId);
        return tracksNow.get(guildId);
    }

    public static void clearNow(Long guildId) {
        tracksNow.computeIfPresent(guildId, (key, val) -> null);
    }

    public static TrackContext getFirst(Long guildId) {
        init(guildId);
        return tracksQueue.get(guildId).peekFirst();
    }

    public static TrackContext pop(Long guildId) {
        init(guildId);
        return tracksQueue.get(guildId).poll();
    }

    public static Integer size(Long guildId) {
        init(guildId);
        return tracksQueue.get(guildId).size();
    }

    public static void clear(Long guildId) {
        init(guildId);
        tracksQueue.get(guildId).clear();
    }

    public static List<TrackContext> listQueue(Long guildId) {
        init(guildId);
        Deque<TrackContext> tracks = tracksQueue.get(guildId);
        if (tracks == null) {
            return new ArrayList<>();
        }
        return tracks.stream().toList();

    }
}
