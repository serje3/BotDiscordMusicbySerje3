package org.serje3;

import dev.arbjerg.lavalink.client.Helpers;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.serje3.adapters.DefaultAdapter;
import org.serje3.adapters.MusicAdapter;

import java.util.ArrayList;
import java.util.List;


public class BotApplication {

    public static JDA Bot;

    public static void main(String[] args) throws Exception {
        String token = System.getenv("BOT_TOKEN");
        if (token == null) {
            throw new Exception("No token provided");
        }
        LavalinkClient client = new LavalinkClient(Helpers.getUserIdFromToken(token));


        Bot = JDABuilder.createDefault(token)
                .setVoiceDispatchInterceptor(new JDAVoiceUpdateListener(client))
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .enableCache(CacheFlag.VOICE_STATE)
                .setActivity(Activity.customStatus("иди нахуй"))
                .build();
        Bot.awaitReady();
        // Bot is ready
        System.out.println("BOT IS READY");

        MusicAdapter musicAdapter = new MusicAdapter(client);
        DefaultAdapter defaultAdapter = new DefaultAdapter();

        // Clear commands
        Bot.updateCommands()
                .addCommands(new ArrayList<>() {
                    {
                        addAll(musicAdapter.getSlashCommands());
                        addAll(defaultAdapter.getSlashCommands());
                    }
                })
                .queue();


        Bot.addEventListener(defaultAdapter);
        Bot.addEventListener(musicAdapter);
    }
}
