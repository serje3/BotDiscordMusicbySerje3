package org.serje3.services;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.LavalinkNode;
import dev.arbjerg.lavalink.client.NodeOptions;
import dev.arbjerg.lavalink.client.event.*;
import dev.arbjerg.lavalink.client.loadbalancing.RegionGroup;
import dev.arbjerg.lavalink.client.player.Track;
import dev.arbjerg.lavalink.protocol.v4.Message;
import io.sentry.Sentry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import org.serje3.config.GuildConfig;
import org.serje3.domain.TrackContext;
import org.serje3.rest.handlers.NodeRestHandler;
import org.serje3.utils.SlashEventHelper;
import org.serje3.utils.TrackQueue;
import org.serje3.utils.exceptions.NoTracksInQueueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

import static org.serje3.BotApplication.Bot;

public class NodeService {
    private final Logger logger = LoggerFactory.getLogger(NodeService.class);
    private final NodeRestHandler nodeRestHandler = new NodeRestHandler();
    private final List<Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason> audioTrackEndReasonsToIgnore = List.of(
            Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason.REPLACED
    );

    private static boolean nodesRegistered = false;

    private final LavalinkService lavalinkService;

    public NodeService(LavalinkService service) {
        this.lavalinkService = service;
        register();
    }

    public NodeService() {
        this.lavalinkService = LavalinkService.getInstance();
        register();
    }

    private void register() {
        if (!nodesRegistered) {
            registerLavalinkListeners();
            registerLavalinkNodes();
            registerLavalinkWebsocketClosed();
            nodesRegistered = true;
        }
    }

    private void registerLavalinkNodes() {
        List<NodeOptions> nodes = null;
        try {
            logger.info("Getting nodes");
            nodes = this.nodeRestHandler.getNodes();
        } catch (Exception e) {
            Sentry.captureException(e);
            logger.error(e.getMessage());
        }

        if (nodes == null || nodes.isEmpty()) {
            nodes = List.of(new NodeOptions.Builder()
                    .setName("base")
                    .setServerUri("wss://amsterdam.serje3.ru:443")
                    .setPassword("DIcsG6lG49wY7rkk")
                    .setRegionFilter(RegionGroup.EUROPE)
                    .build());
        }

        nodes.stream().map(lavalinkService.getClient()::addNode).toList().forEach((node) -> {
            node.on(TrackStartEvent.class).subscribe((data) -> {
                final LavalinkNode chosenNode = data.getNode();
                final var event = data;
                logger.info(
                        "{}: track started: {}\n",
                        chosenNode.getName(),
                        event.getTrack().getInfo()
                );
            });

            node.on(TrackExceptionEvent.class).subscribe((data) -> {
                logger.error("TrackExceptionEvent {} {}", data.getException().getMessage(), data.getException().getCause());
                TrackContext now = TrackQueue.peekNow(data.getGuildId());
                if (now == null) {
                    GuildConfig.Settings settings = GuildConfig.getSettings(data.getGuildId());
                    if (settings.getLastInteractionChannel() == null || settings.getLastInteractedMember() == null) return;
                    TrackQueue.addNextToFirst(data.getGuildId(), SlashEventHelper.createTrackContextFromEvent(data.getTrack(),
                            settings.getLastInteractedMember(),
                            settings.getLastInteractionChannel()));
                    settings.getLastInteractionChannel().sendMessage("Бляздец. Ещё раз").queue();
                    return;
                }
                ;
                TrackContext track = SlashEventHelper.createTrackContextFromEvent(data.getTrack(), now.getMember(), now.getTextChannel());
                if (track.getTextChannel() != null) {
                    track.getTextChannel().sendMessage("Бля кажется пизда треку, попробую перезапустить").queue();
                }

                TrackQueue.addNextToFirst(data.getGuildId(), track);
            });

            node.on(TrackEndEvent.class).subscribe((data) -> {
                Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason endReason = data.getEndReason();
                logger.warn("TRACK ENDED {}", data.getEndReason());


                if (endReason.equals(Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason.LOAD_FAILED)) {
                    Sentry.captureMessage("TRACK ENDED, REASON: " + endReason);
                } else if (audioTrackEndReasonsToIgnore.contains(endReason)) {
                    return;
                }
                Long guildId = data.getGuildId();
                try {
                    TrackContext newTrack = TrackQueue.skip(guildId, true);
                    TextChannel textChannel = newTrack.getTextChannel();
                    textChannel.sendMessage("Включаю следующий трек: " + newTrack.getTrack().getInfo().getAuthor() + " - " + newTrack.getTrack().getInfo().getTitle()).queue();
                } catch (NoTracksInQueueException e) {
                    //pass
                    logger.warn("No Tracks in Queue in guild {}", guildId);
                }
            });

            node.on(TrackStuckEvent.class).subscribe(data -> {
                logger.warn("Track {} is stuck. Threshold is {} on node {}", data.getTrack().getInfo().getTitle(), data.getThresholdMs(), data.getNode().getName());
            });
        });
    }

    private void registerLavalinkListeners() {
        LavalinkClient client = lavalinkService.getClient();
        client.on(ReadyEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();

            logger.info(
                    "Node '{}' is ready, session id is '{}'! is Resumed {}",
                    node.getName(),
                    event.getSessionId(),
                    event.getResumed()
            );
        });

        client.on(StatsEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();

            Duration duration = Duration.ofMillis(event.getUptime());

            // Получение целых дней, часов, минут, секунд и миллисекунд
            long days = duration.toDays();
            long hours = duration.toHours() % 24;
            long minutes = duration.toMinutes() % 60;
            long seconds = duration.getSeconds() % 60;

            String upTimeFormatted = days + " дней " + String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + seconds;

            logger.info(
                    "Node '{}' has stats, current players: {}/{} (link count {}). Lavalink load {}. System load {}. Cores {}. Memory {}/{}. Up time {}",
                    node.getName(),
                    event.getPlayingPlayers(),
                    event.getPlayers(),
                    client.getLinks().size(),
                    Math.round(event.getCpu().getLavalinkLoad() * 100) + "%",
                    Math.round(event.getCpu().getSystemLoad() * 100) + "%",
                    event.getCpu().getCores(),
                    event.getMemory().getUsed(),
                    event.getMemory().getReservable(),
                    upTimeFormatted
            );
        });
    }

    private void registerLavalinkWebsocketClosed() {
        LavalinkClient client = lavalinkService.getClient();

        client.on(WebSocketClosedEvent.class).subscribe((event) -> {
            logger.warn("Websocket closed Code: {}, Reason: {}", event.getCode(), event.getReason());
            if (event.getCode() == 4006) {
                long guildId = event.getGuildId();
                Guild guild = Bot.getGuildById(guildId);
                if (guild == null) {
                    return;
                }

                AudioChannelUnion connectedChannel = guild.getSelfMember().getVoiceState().getChannel();
                if (connectedChannel == null) {
                    return;
                }

                logger.error("CODE 4006");
                Bot.getDirectAudioController().reconnect(connectedChannel);
            }
        });
    }

}
