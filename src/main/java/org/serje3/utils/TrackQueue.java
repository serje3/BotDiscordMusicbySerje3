package org.serje3.utils;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.protocol.v4.Track;
import org.serje3.utils.exceptions.NoTracksInQueueException;

import java.util.*;

public class TrackQueue {
    private static final HashMap<Long, Deque<Track>> tracksQueue = new HashMap<>();
    private static final HashMap<Long, Boolean> busyPlayers = new HashMap<>();

    private static void init(Long guildId) {
        Set<Long> keySet = tracksQueue.keySet();
        if (!keySet.contains(guildId)) {
            tracksQueue.put(guildId, new ArrayDeque<>());
        }
    }

    public static void add(Long guildId, Track track) {
        init(guildId);

        tracksQueue.get(guildId).addLast(track);
    }

    public static void addAll(Long guildId, Collection<Track> tracks) {
        init(guildId);

        tracksQueue.get(guildId).addAll(tracks);
    }

    public static void skip(LavalinkClient client, Long guildId) throws NoTracksInQueueException {
        init(guildId);

        Track track = TrackQueue.pop(guildId);
        System.out.println("BLYAT   "  + TrackQueue.tracksQueue.get(guildId));
        Link link = client.getLink(guildId);
        if (track == null) {
            throw new NoTracksInQueueException();
        }
        System.out.println("Next track is " + track.getInfo().getTitle() + " in guild " + guildId);
        VoiceHelper.play(link, track, 35);
    }

    public static Track pop(Long guildId) {
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

    public static List<Track> listQueue(Long guildId) {
        init(guildId);
        Deque<Track> tracks = tracksQueue.get(guildId);
        if (tracks == null) {
            return new ArrayList<>();
        }
        return tracks.stream().toList();

    }
}
