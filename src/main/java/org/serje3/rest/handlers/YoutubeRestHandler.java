package org.serje3.rest.handlers;

import io.sentry.Sentry;
import org.serje3.rest.base.BaseRestClient;
import org.serje3.rest.domain.Tracks;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

public class YoutubeRestHandler extends BaseRestClient {
    public Tracks searchCached(String q) throws ExecutionException, InterruptedException {
        System.out.println("URL ENCODE " + URLEncoder.encode(q, StandardCharsets.UTF_8));
        return this.get("/youtube/search?q=" + URLEncoder.encode(q, StandardCharsets.UTF_8), Tracks.class).get();
    }

    public String trySearchCachedUrl(String identifier) {
        if (identifier.length() > 100) {
            identifier = identifier.substring(0, 100);
        }

        Tracks tracks;
        try {
            tracks = searchCached(identifier);
        } catch (InterruptedException | ExecutionException e) {
            Sentry.captureException(e);
            return null;
        }
        if (tracks.getItems() == null || tracks.getItems().isEmpty()) {
            return null;
        }

        Tracks.CachedTrack track = tracks.getItems().get(0);

        return track.getYoutubeURL();
    }
}
