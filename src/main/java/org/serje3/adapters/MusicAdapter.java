package org.serje3.adapters;

import dev.arbjerg.lavalink.client.*;
import dev.arbjerg.lavalink.client.loadbalancing.RegionGroup;
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import dev.arbjerg.lavalink.protocol.v4.Message;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.serje3.commands.music.*;
import org.serje3.meta.abs.Command;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

public class MusicAdapter extends ListenerAdapter {

    private final LavalinkClient client;

    private final HashMap<String, Command> commands;

    public MusicAdapter() throws Exception {
        String token = System.getenv("BOT_TOKEN");
        if (token == null) {
            throw new Exception("No token provided");
        }
        this.client = new LavalinkClient(Helpers.getUserIdFromToken(token));

        this.client.getLoadBalancer().addPenaltyProvider(new VoiceRegionPenaltyProvider());

        this.registerLavalinkNodes();
        this.registerLavalinkListeners();

        this.commands = new HashMap<>();
        this.registerCommands();

        JDABuilder.createDefault(token)
                .setVoiceDispatchInterceptor(new JDAVoiceUpdateListener(this.client))
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .enableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(this)
                .build()
                .awaitReady();
    }

    private void registerCommands() {
        this.commands.put("play", new PlayCommand());
        this.commands.put("gachi", new GachiCommand());
        this.commands.put("pause", new PauseCommand());
        this.commands.put("join", new JoinCommand());
        this.commands.put("leave", new LeaveCommand());
    }

    private void registerLavalinkNodes() {
        List.of(
                client.addNode(
                        "Testnode",
                        URI.create("ws://localhost:2333"),
                        "testing",
                        RegionGroup.EUROPE
                )
        ).forEach((node) -> {
            node.on(TrackStartEvent.class).subscribe((data) -> {
                final LavalinkNode node1 = data.getNode();
                final var event = data.getEvent();

                System.out.printf(
                        "%s: track started: %s%n",
                        node1.getName(),
                        event.getTrack().getInfo()
                );
            });
        });
    }

    private void registerLavalinkListeners() {
        this.client.on(dev.arbjerg.lavalink.client.ReadyEvent.class).subscribe((data) -> {
            final LavalinkNode node = data.getNode();
            final Message.ReadyEvent event = data.getEvent();
            System.out.printf(
                    "Node '%s' is ready, session id is '%s'!%n",
                    node.getName(),
                    event.getSessionId()
            );
        });

        this.client.on(StatsEvent.class).subscribe((data) -> {
            final LavalinkNode node = data.getNode();
            final Message.StatsEvent event = data.getEvent();

            System.out.printf(
                    "Node '%s' has stats, current players: %d/%d%n",
                    node.getName(),
                    event.getPlayingPlayers(),
                    event.getPlayers()
            );
        });
    }

    @Override
    public void onReady(@NotNull net.dv8tion.jda.api.events.session.ReadyEvent event) {
        System.out.println(event.getJDA().getSelfUser().getAsTag() + " is ready!");
        initializeSlashCommands(event.getJDA());
    }


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getFullCommandName()) {
            case "join" -> this.commands.get("join").execute(event, this.client);
            case "leave" -> this.commands.get("leave").execute(event, this.client);
            case "pause" -> this.commands.get("pause").execute(event, this.client);
            case "play" -> this.commands.get("play").execute(event, this.client);
            case "gachi" -> this.commands.get("gachi").execute(event, this.client);
            default -> event.reply("Unknown command???").queue();
        }
    }

    private void initializeSlashCommands(JDA jda) {
        jda.updateCommands()
                .addCommands(
                        Commands.slash("join", "Присоединиться к каналу."),
                        Commands.slash("leave", "Выйти из голосового канала"),
                        Commands.slash("pause", "Пауза трека"),
                        Commands.slash("play", "Играть музыку")
                                .addOption(
                                        OptionType.STRING,
                                        "identifier",
                                        "Ссылка или id трека, который вы хотите включить",
                                        true
                                ),
                        Commands.slash("gachi", "НЕ НАЖИМАТЬ")
                )
                .queue();
    }
}
