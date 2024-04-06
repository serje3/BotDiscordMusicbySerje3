package org.serje3.utils;
import io.sentry.Sentry;
import org.serje3.config.BotConfig;

import java.io.IOException;

public class SentryUtil {
    private static boolean initialized = false;
    public static void initSentry(){
        if(initialized) return;

        String dsn;
        try {
            dsn = BotConfig.getProperty("sentry.dsn");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Sentry.init(sentryOptions -> {
            sentryOptions.setDsn(dsn);

            sentryOptions.setTracesSampleRate(1.0);
            sentryOptions.setDebug(true);
            initialized = true;
        });
    }
}
