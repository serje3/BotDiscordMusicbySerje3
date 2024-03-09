package org.serje3.rest.base;

import org.serje3.config.BotConfig;

import java.io.IOException;

public class BaseRestClient extends AbstractBaseClient {

    private final String base_url;

    {
        try {
            base_url = BotConfig.getProperty("backend_url");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getBaseUrl() {
        return base_url;
    }
}
