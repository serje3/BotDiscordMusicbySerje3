package org.serje3.rest.domain.gachi;

import lombok.Data;

@Data
public class NowPlaying {
    private final int sh_id;
    private final int played_at;
    private final int duration;
    private final String playlist;
    private final String streamer;
    private final boolean is_request;
    private final Song song;
    private final int elapsed;
    private final int remaining;
}
