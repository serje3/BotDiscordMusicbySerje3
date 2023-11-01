package org.serje3.adapters;

import com.github.topi314.lavasrc.yandexmusic.YandexMusicSourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import dev.arbjerg.lavalink.client.*;
import dev.arbjerg.lavalink.client.loadbalancing.RegionGroup;
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider;
import dev.arbjerg.lavalink.protocol.v4.Message;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.serje3.meta.abs.Command;
import org.serje3.meta.decorators.MusicCommandDecorator;
import org.serje3.utils.CommandList;
import org.serje3.utils.TrackQueue;
import org.serje3.utils.exceptions.NoTracksInQueueException;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MusicAdapter extends ListenerAdapter {

    private final LavalinkClient client;

    private final HashMap<String, Command> commands;

    public MusicAdapter(LavalinkClient client) throws Exception {
        this.client = client;

        this.client.getLoadBalancer().addPenaltyProvider(new VoiceRegionPenaltyProvider());

        this.registerLavalinkNodes();
        this.registerLavalinkListeners();

        this.commands = new HashMap<>();
        this.registerCommands();
    }

    private void registerCommands(){
        CommandList.forEach(command -> this.commands.put(command.getName(), convertCommand(command)));
    }

    private Command convertCommand(Command command) {
        return new MusicCommandDecorator(command);
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
            node.on(TrackEndEvent.class).subscribe((data) -> {
                System.out.println("TRACK ENDED " + data.getEvent().getType());
                Long guildId = Long.parseLong(data.getEvent().getGuildId());
                try {
                    TrackQueue.skip(client, guildId);
                } catch (NoTracksInQueueException e) {
                    //pass

                }
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

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YandexMusicSourceManager("AQAAAAAQZs1CAAG8XjWJXJC1gUq1nLyhOwI9bUI"));
        System.out.println(playerManager.getSourceManagers());
    }


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        System.out.println("Command Name: " + event.getName());
        String commandName = event.getName();
        Command command = this.commands.get(commandName);
        if (command != null) {
            command.execute(event, this.client);
        } else {
            event.reply("Такой cumанды нет???").queue();
        }
    }

    private void initializeSlashCommands(JDA jda) {

        jda.updateCommands()
                .addCommands(
                        this.commands.values().stream().map(Command::getSlashCommand).collect(Collectors.toList())
                )
                .queue();
    }
}
