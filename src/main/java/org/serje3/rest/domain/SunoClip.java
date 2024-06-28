package org.serje3.rest.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@Getter
public class SunoClip {
    private final String id;
    private final String videoUrl;
    private final String audioUrl;
    private final String imageUrl;
    private final String imageLargeUrl;
    private final boolean isVideoPending;
    private final String majorModelVersion;
    private final String modelName;
    private final Map<String, Object> metadata;
    private final boolean isLiked;
    private final String userId;
    private final String displayName;
    private final String handle;
    private final boolean isHandleUpdated;
    private final boolean isTrashed;
    private final Object reaction; // тип данных для reaction может варьироваться в зависимости от содержания
    private final String createdAt;
    private final String status;
    private final String title;
    private final int playCount;
    private final int upvoteCount;
    private final boolean isPublic;

}
