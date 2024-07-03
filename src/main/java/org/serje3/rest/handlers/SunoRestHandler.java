package org.serje3.rest.handlers;

import org.serje3.rest.base.BaseRestClient;
import org.serje3.rest.domain.SunoClip;
import org.serje3.rest.requests.SunoGenerateRequest;
import org.serje3.rest.requests.SunoLoginRequest;
import org.serje3.rest.responses.SunoCredits;
import org.serje3.rest.responses.SunoGenerateResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SunoRestHandler extends BaseRestClient {

    public CompletableFuture<?> login(Long userId, String cookie, String session) {
        return this.post("/suno/login", new SunoLoginRequest(userId, cookie, session), Object.class);
    }

    public CompletableFuture<List<SunoClip>> feed(Long userId) {
        return feed(userId, null);
    }

    public CompletableFuture<List<SunoClip>> feed(Long userId, Integer page) {
        String feedUrl = "/suno/feed/" + userId;
        if (page != null) {
            feedUrl += "?page=" + page;
        }
        return this.getList(feedUrl, SunoClip.class);
    }

    public CompletableFuture<SunoGenerateResponse> generate(Long userId, SunoGenerateRequest request) {
        return this.post("/suno/generate?userId=" + userId, request, SunoGenerateResponse.class);
    }

    public CompletableFuture<SunoCredits> credits(Long userId) {
        return this.get("/credits/"+userId, SunoCredits.class);
    }


}
