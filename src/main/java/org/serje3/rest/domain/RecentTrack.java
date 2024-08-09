package org.serje3.rest.domain;

import lombok.Builder;
import lombok.Data;

@Data
public class RecentTrack {
    private Long id;
    private String name;
    private String url;
}
