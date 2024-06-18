package org.serje3.rest.domain.gachi;

import lombok.Data;

import java.util.List;

@Data
public class GachiResponse {
    private final Station station;
    private final Listeners listeners;
    private final Live live;
    private final NowPlaying now_playing;
    private final PlayingNext playing_next;
    private final List<SongHistory> song_history;
    private final boolean is_online;
    private final Object cache;
    private final Integer status;

    public boolean isSuccessful() {
        return status == null || status >= 200 && status < 300;
    }
}
