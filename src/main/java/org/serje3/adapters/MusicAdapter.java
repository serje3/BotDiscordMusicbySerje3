package org.serje3.adapters;

import com.github.topi314.lavasrc.yandexmusic.YandexMusicSourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import dev.arbjerg.lavalink.client.*;
import dev.arbjerg.lavalink.client.loadbalancing.RegionGroup;
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import com.github.topi314.lavasearch.AudioSearchManager;
import dev.arbjerg.lavalink.protocol.v4.Message;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.serje3.commands.music.*;
import org.serje3.meta.abs.Command;
import org.serje3.meta.decorators.MusicCommandDecorator;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private void registerCommands() {
        this.commands.put("play", convertCommand(new PlayCommand()));
        this.commands.put("gachi", convertCommand(new GachiCommand()));
        this.commands.put("pause", convertCommand(new PauseCommand()));
        this.commands.put("join", convertCommand(new JoinCommand()));
        this.commands.put("leave", convertCommand(new LeaveCommand()));
        this.commands.put("tts", convertCommand(new TTSCommand()));
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
        switch (event.getFullCommandName().split(" ")[0]) {
            case "join" -> this.commands.get("join").execute(event, this.client);
            case "leave" -> this.commands.get("leave").execute(event, this.client);
            case "pause" -> this.commands.get("pause").execute(event, this.client);
            case "play" -> this.commands.get("play").execute(event, this.client);
            case "gachi" -> this.commands.get("gachi").execute(event, this.client);
            case "tts" -> this.commands.get("tts").execute(event, this.client);
            default -> event.reply("Такой cumанды нет???").queue();
        }
    }

    private void initializeSlashCommands(JDA jda) {
        jda.updateCommands()
                .addCommands(
                        Commands.slash("join", "Присоединиться к каналу."),
                        Commands.slash("leave", "Выйти из голосового канала"),
                        Commands.slash("pause", "Пауза трека"),
                        Commands.slash("play", "Играть музыку")
                                .addSubcommands(
                                        new SubcommandData("youtube", "Поиск из ютуба")
                                                .addOption(
                                                        OptionType.STRING,
                                                        "текст",
                                                        "Строка поиска youtube",
                                                        true
                                                ),
                                        new SubcommandData("soundcloud", "Поиск из soundclound")
                                                .addOption(
                                                        OptionType.STRING,
                                                        "текст",
                                                        "Строка поиска soundcloud",
                                                        true
                                                ),
                                        new SubcommandData("yandexmusic", "Поиск из Yandex Music")
                                                .addOption(
                                                        OptionType.STRING,
                                                        "текст",
                                                        "Строка поиска Yandex Music",
                                                        true
                                                )
                                ),
                        Commands.slash("gachi", "НЕ НАЖИМАТЬ"),
                        Commands.slash("tts", "TTS бля")
                                .addOption(OptionType.STRING,
                                        "текст",
                                        "текст в голос че не понятного",
                                        true)
                                .addOption(OptionType.STRING,
                                        "голос",
                                        "Выберите нужный голос из списка https://api.flowery.pw/v1/tts/voices",
                                        false)
                )
                .queue();
    }
}
