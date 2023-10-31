package org.serje3.utils;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.protocol.v4.Track;
import org.serje3.utils.exceptions.NoTracksInQueueException;

import java.util.*;

public class TrackQueue {
    private static final HashMap<Long, Deque<Track>> tracksQueue = new HashMap<>();
    private static final HashMap<Long, Boolean> busyPlayers = new HashMap<>();

    public static void add(Long guildId, Track track) {
        Set<Long> keySet = tracksQueue.keySet();
        if (!keySet.contains(guildId)) {
            tracksQueue.put(guildId, new ArrayDeque<>());
        }

        tracksQueue.get(guildId).addLast(track);
        if (TrackQueue.size(guildId) == 1){

        }
    }

    public static void skip(LavalinkClient client, Long guildId) throws NoTracksInQueueException {
        Track track = TrackQueue.pop(guildId);
        Link link = client.getLink(guildId);
        if (track == null){
            throw new NoTracksInQueueException();
        }
        System.out.println("Next track is " + track.getInfo().getTitle() + " in guild " + guildId);
        VoiceHelper.play(link, track, 35);
    }

    public static Track pop(Long guildId) {
        Set<Long> keySet = tracksQueue.keySet();
        if (!keySet.contains(guildId)) {
            return null;
        }

        return tracksQueue.get(guildId).pollFirst();
    }

    public static Integer size(Long guildId) {
        Deque<Track> tracks = tracksQueue.get(guildId);
        if (tracks != null) {
            return tracks.size();
        }
        return 0;
    }

    public static void clear(Long guildId) {
        Deque<Track> tracks = tracksQueue.get(guildId);
        if (tracks != null) {
            tracks.clear();
        }
    }

    public static List<Track> listQueue(Long guildId){
        Deque<Track> tracks = tracksQueue.get(guildId);
        if (tracks == null){
            return new ArrayList<>();
        }
        return tracks.stream().toList();

    }
}
