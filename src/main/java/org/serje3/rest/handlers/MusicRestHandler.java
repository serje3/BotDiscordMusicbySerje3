package org.serje3.rest.handlers;

import org.serje3.rest.base.BaseRestClient;
import org.serje3.rest.domain.RecentTrack;
import org.serje3.rest.requests.SaveRecentTrackRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MusicRestHandler extends BaseRestClient {
    private final Logger logger = LoggerFactory.getLogger(MusicRestHandler.class);

    public CompletableFuture<List<RecentTrack>> getRecentTracks(Long guildId) {
        return this.getList("/music/recent/" + guildId, RecentTrack.class);
    }

    public void saveRecentTrack(SaveRecentTrackRequest request) {
        this.post("/music/recent/", request, Object.class).thenAccept(ignored -> {
            logger.info("Successfully saved recent track");
        });
    }
}
