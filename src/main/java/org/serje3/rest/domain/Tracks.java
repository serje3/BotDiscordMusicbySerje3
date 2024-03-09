package org.serje3.rest.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class Tracks {
    private final List<CachedTrack> items;

    public record CachedTrack(String etag, String title, String channelId, String channelTitle, String videoId,
                                  String thumbnailUrl, LocalDateTime publishTime) {

        public String getYoutubeURL() {
            if (videoId == null || videoId.isEmpty() || videoId.isBlank()) return null;
            return "https://www.youtube.com/watch?v=" + videoId;
        }
    }
}
