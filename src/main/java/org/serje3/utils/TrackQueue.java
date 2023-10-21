package org.serje3.utils;

import dev.arbjerg.lavalink.protocol.v4.Track;

import java.util.*;

public class TrackQueue {
    private static final HashMap<Long, Deque<Track>> tracksQueue = new HashMap<>();

    static void add(Long guildId, Track track) {
        Set<Long> keySet = tracksQueue.keySet();
        if (!keySet.contains(guildId)) {
            tracksQueue.put(guildId, new ArrayDeque<>());
        }

        tracksQueue.get(guildId).addLast(track);
    }


    static Track pop(Long guildId) {
        Set<Long> keySet = tracksQueue.keySet();
        if (!keySet.contains(guildId)) {
            return null;
        }

        return tracksQueue.get(guildId).pollFirst();
    }
}
