package org.serje3.rest.domain.gachi;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import lombok.Data;

import java.util.List;

@Data
public class Station {
    private final int id;
    private final String name;
    private final String shortcode;
    private final String description;
    private final String frontend;
    private final String backend;
    private final String timezone;
    private final String listen_url;
    private final String url;
    private final String public_player_url;
    private final String playlist_pls_url;
    private final String playlist_m3u_url;
    private final boolean is_public;
    private final List<Object> mounts;
    private final List<Object> remotes;
    private final boolean hls_enabled;
    private final boolean hls_is_default;
    private final String hls_url;
    private final int hls_listeners;
}
