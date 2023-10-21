package org.serje3.meta.enums;

import lombok.Getter;

@Getter
public enum PlaySourceType {
    YOUTUBE("youtube"),
    SOUNDCLOUD("soundcloud"),
    YANDEXMUSIC("yandexmusic"),
    TEXT_TO_SPEECH("tts");


    private final String title;

    PlaySourceType(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

}
