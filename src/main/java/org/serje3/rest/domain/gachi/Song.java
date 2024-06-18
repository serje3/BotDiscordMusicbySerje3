package org.serje3.rest.domain.gachi;

import lombok.Data;

import java.util.List;

@Data
public class Song {
    private final String id;
    private final String text;
    private final String artist;
    private final String title;
    private final String album;
    private final String genre;
    private final String isrc;
    private final String lyrics;
    private final String art;
    private final List<Object> custom_fields;
}
