package org.serje3;

import dev.arbjerg.lavalink.client.Helpers;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.serje3.adapters.DefaultAdapter;
import org.serje3.adapters.LogAdapter;
import org.serje3.adapters.MusicAdapter;
import org.serje3.adapters.SunoAdapter;
import org.serje3.config.BotConfig;
import org.serje3.services.LavalinkService;
import org.serje3.utils.SentryUtil;

import java.util.ArrayList;


public class BotApplication {

    public static JDA Bot;

    public static void main(String[] args) throws Exception {
        String token = System.getenv("BOT_TOKEN");
        if (token == null) {
            throw new Exception("No token provided");
        }
        // creating instance
        LavalinkService clientService = LavalinkService.getInstance();

        SentryUtil.initSentry();

        Sentry.captureMessage("Bot is starting", SentryLevel.DEBUG);

        boolean isTest = Boolean.parseBoolean(BotConfig.getProperty("test"));

        Bot = JDABuilder.createDefault(token)
                .setVoiceDispatchInterceptor(new JDAVoiceUpdateListener(clientService.getClient()))
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .enableCache(CacheFlag.VOICE_STATE)
                .setActivity(Activity.customStatus(isTest ? "я sosu" : "идите нахуй"))
                .build();
        Bot.awaitReady();
        // Bot is ready
        System.out.println("BOT IS READY!!!!");

        LogAdapter logAdapter = new LogAdapter();
        MusicAdapter musicAdapter = new MusicAdapter();
        DefaultAdapter defaultAdapter = new DefaultAdapter();
        SunoAdapter sunoAdapter = new SunoAdapter();
        // Clear context
        Bot.updateCommands()
                .addCommands(new ArrayList<>() {
                    {
                        addAll(logAdapter.getSlashCommands());
                        addAll(musicAdapter.getSlashCommands());
                        addAll(defaultAdapter.getSlashCommands());
                        addAll(sunoAdapter.getSlashCommands());
                    }
                })
                .queue();
        Bot.addEventListener(logAdapter);
        Bot.addEventListener(musicAdapter);
        Bot.addEventListener(defaultAdapter);
        Bot.addEventListener(sunoAdapter);
    }
}
