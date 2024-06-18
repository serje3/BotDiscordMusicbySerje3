package org.serje3.rest.domain.gachi;

import lombok.Data;

@Data
public class Live {
    private final boolean is_live;
    private final String streamer_name;
    private final Object broadcast_start;
    private final Object art;
}
