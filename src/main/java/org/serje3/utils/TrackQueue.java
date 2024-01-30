package org.serje3.utils;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.protocol.Track;
import org.serje3.domain.TrackContext;
import org.serje3.utils.exceptions.NoTracksInQueueException;

import java.util.*;

public class TrackQueue {
    private static final HashMap<Long, Deque<TrackContext>> tracksQueue = new HashMap<>();

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

    public static void skip(LavalinkClient client, Long guildId) throws NoTracksInQueueException {
        init(guildId);

        TrackContext trackContext = TrackQueue.pop(guildId);
        if (trackContext == null) {
            throw new NoTracksInQueueException();
        }
        Track track = trackContext.getTrack();
        System.out.println("BLYAT   "  + TrackQueue.tracksQueue.get(guildId));
        Link link = client.getLink(guildId);
        System.out.println("Next track is " + track.getInfo().getTitle() + " in guild " + guildId);
        VoiceHelper.play(link, track, 35);
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
