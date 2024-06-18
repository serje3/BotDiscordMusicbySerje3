package org.serje3.rest.handlers;

import org.serje3.rest.base.AbstractBaseClient;
import org.serje3.rest.domain.gachi.GachiResponse;

import java.util.concurrent.ExecutionException;

public class GachiBassRestHandler extends AbstractBaseClient {

    public GachiResponse now() throws ExecutionException, InterruptedException {
        return this.get("/api/nowplaying/gachibass_radio", GachiResponse.class).get();
    }

    @Override
    protected String getBaseUrl() {
        return "https://radio.gachibass.us.to";
    }
}
