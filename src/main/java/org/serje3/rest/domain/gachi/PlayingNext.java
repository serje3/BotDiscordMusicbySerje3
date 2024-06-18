package org.serje3.rest.domain.gachi;

import lombok.Data;

@Data
public class PlayingNext {
    private final int cued_at;
    private final int played_at;
    private final int duration;
    private final String playlist;
    private final boolean is_request;
    private final Song song;
}
