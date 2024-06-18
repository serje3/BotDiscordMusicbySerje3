package org.serje3.services;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.LavalinkNode;
import dev.arbjerg.lavalink.client.NodeOptions;
import dev.arbjerg.lavalink.client.event.*;
import dev.arbjerg.lavalink.client.loadbalancing.RegionGroup;
import dev.arbjerg.lavalink.protocol.v4.Message;
import io.sentry.Sentry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import org.serje3.domain.TrackContext;
import org.serje3.rest.handlers.NodeRestHandler;
import org.serje3.utils.TrackQueue;
import org.serje3.utils.exceptions.NoTracksInQueueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.serje3.BotApplication.Bot;

public class NodeService {
    private final Logger logger = LoggerFactory.getLogger(NodeService.class);
    private final NodeRestHandler nodeRestHandler = new NodeRestHandler();
    private final List<Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason> audioTrackEndReasonsToIgnore = List.of(
            Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason.REPLACED
    );

    public void register(){
        registerLavalinkNodes();
        registerLavalinkListeners();
        registerLavalinkWebsocketClosed();
    }

    private void registerLavalinkNodes() {
        List<NodeOptions> nodes = null;
        try {
            nodes = this.nodeRestHandler.getNodes();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Sentry.captureException(e);
        }

        if (nodes == null || nodes.isEmpty()) {
            nodes = List.of(new NodeOptions.Builder()
                    .setName("base")
                    .setServerUri("wss://amsterdam.serje3.ru:443")
                    .setPassword("DIcsG6lG49wY7rkk")
                    .setRegionFilter(RegionGroup.EUROPE)
                    .build());
        }

        nodes.stream().map(LavalinkService.getInstance().getClient()::addNode).toList().forEach((node) -> {
            node.on(TrackStartEvent.class).subscribe((data) -> {
                final LavalinkNode chosenNode = data.getNode();
                final var event = data;
                logger.info(
                        "{}: track started: {}\n",
                        chosenNode.getName(),
                        event.getTrack().getInfo()
                );
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
        LavalinkClient client = LavalinkService.getInstance().getClient();
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


            logger.info(
                    "Node '{}' has stats, current players: {}/{} (link count {}). Lavalink load {}. System load {}. Cores {}. Memory {}/{}. Up time {}",
                    node.getName(),
                    event.getPlayingPlayers(),
                    event.getPlayers(),
                    client.getLinks().size(),
                    event.getCpu().getLavalinkLoad(),
                    event.getCpu().getSystemLoad(),
                    event.getCpu().getCores(),
                    event.getMemory().getUsed(),
                    event.getMemory().getReservable(),
                    event.getUptime()
            );
        });
    }

    private void registerLavalinkWebsocketClosed() {
        LavalinkClient client = LavalinkService.getInstance().getClient();

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
