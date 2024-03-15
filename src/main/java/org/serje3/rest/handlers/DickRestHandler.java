package org.serje3.rest.handlers;

import org.serje3.config.BotConfig;
import org.serje3.rest.base.AbstractBaseClient;
import org.serje3.rest.base.BaseRestClient;
import org.serje3.rest.requests.DickRequest;
import org.serje3.rest.responses.DickResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class DickRestHandler extends AbstractBaseClient {

    private final String base_url;

    {
        try {
            base_url = BotConfig.getProperty("dick_backend_url");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getBaseUrl() {
        return base_url;
    }

    public DickResponse generateDickName(String name){
        try {
            return this.post("/generate/dickname", new DickRequest(name), DickResponse.class).get();
        } catch (InterruptedException e) {
            return new DickResponse("Хуй хуёвый");
        } catch (ExecutionException e) {
            return new DickResponse("Хуй хуёвее");
        }
    }
}
