package org.serje3;

import dev.arbjerg.lavalink.client.Helpers;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.serje3.adapters.MusicAdapter;


public class BotApplication {

    public static JDA Bot;

    public static void main(String[] args) throws Exception {
        String token = System.getenv("BOT_TOKEN");
        if (token == null) {
            throw new Exception("No token provided");
        }
        LavalinkClient client = new LavalinkClient(Helpers.getUserIdFromToken(token));

        ListenerAdapter musicAdapter = new MusicAdapter(client);

        Bot = JDABuilder.createDefault(token)
                .setVoiceDispatchInterceptor(new JDAVoiceUpdateListener(client))
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .enableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(musicAdapter)
                .build();
        Bot.awaitReady();
    }
}
