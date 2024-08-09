package org.serje3.meta.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlaySourceType {
    YOUTUBE("youtube", "ytsearch:"),
    YOUTUBEMUSIC("youtubemusic", "ytmsearch:"),
    SOUNDCLOUD("soundcloud", "scsearch:"),
    YANDEXMUSIC("yandexmusic", "ymsearch:"),
    SPOTIFY("spotify", "spsearch:"),
    TEXT_TO_SPEECH("tts", null),
    RECENT("recent", null),
    LOCAL("local", "local:");


    private final String title;
    private final String searchMask;

    @Override
    public String toString() {
        return title;
    }

}
