package org.serje3.adapters;

import dev.arbjerg.lavalink.client.*;

import dev.arbjerg.lavalink.client.event.*;
import dev.arbjerg.lavalink.client.loadbalancing.RegionGroup;
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider;
import dev.arbjerg.lavalink.protocol.v4.Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason;
import io.sentry.Sentry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import org.serje3.domain.TrackContext;
import org.serje3.meta.abs.AdapterContext;
import org.serje3.meta.abs.BaseListenerAdapter;
import org.serje3.meta.abs.Command;
import org.serje3.meta.decorators.MusicCommandDecorator;
import org.serje3.rest.handlers.NodeRestHandler;
import org.serje3.services.MusicService;
import org.serje3.utils.context.MusicAdapterContext;
import org.serje3.utils.TrackQueue;
import org.serje3.utils.exceptions.NoTracksInQueueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.serje3.BotApplication.Bot;

public class MusicAdapter extends BaseListenerAdapter {
    private final MusicService musicService;
    private final NodeRestHandler nodeRestHandler;
    private final Logger logger = LoggerFactory.getLogger(MusicAdapter.class);


    private final List<AudioTrackEndReason> audioTrackEndReasonsToSkip = List.of(
            AudioTrackEndReason.REPLACED
    );

    public MusicAdapter(LavalinkClient client) {
        super();
        this.nodeRestHandler = new NodeRestHandler();
        this.client = client;
        this.client.getLoadBalancer().addPenaltyProvider(new VoiceRegionPenaltyProvider());
        this.registerLavalinkNodes();
        this.registerLavalinkListeners();
        this.registerLavalinkWebsocketClosed();

        this.setClient(client);
        this.musicService = new MusicService();
    }

    @Override
    protected AdapterContext getAdapterContext() {
        return new MusicAdapterContext();
    }


    @Override
    protected Command convertCommand(Command command) {
        return new MusicCommandDecorator(command);
    }


    private void registerLavalinkWebsocketClosed() {
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
//                Bot.getDirectAudioController().reconnect(connectedChannel);
            }
        });
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


        nodes.stream().map(node -> client.addNode(
                node
        )).toList().forEach((node) -> {
            node.on(TrackStartEvent.class).subscribe((data) -> {
                final LavalinkNode node1 = data.getNode();
                final var event = data;
                System.out.printf(
                        "%s: track started: %s%n",
                        node1.getName(),
                        event.getTrack().getInfo()
                );
            });

            node.on(TrackEndEvent.class).subscribe((data) -> {
                AudioTrackEndReason endReason = data.getEndReason();
                logger.warn("TRACK ENDED {}", data.getEndReason());


                if (endReason.equals(AudioTrackEndReason.LOAD_FAILED)) {
                    Sentry.captureMessage("TRACK ENDED, REASON: " + endReason);
                } else if (audioTrackEndReasonsToSkip.contains(endReason)) {
                    return;
                }
                Long guildId = data.getGuildId();
                try {
                    TrackContext newTrack = TrackQueue.skip(client, guildId, true);
                    onNextTrack(newTrack);
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
        this.client.on(ReadyEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();

            logger.info(
                    "Node '{}' is ready, session id is '{}'!",
                    node.getName(),
                    event.getSessionId()
            );
        });

        this.client.on(StatsEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();


            logger.info(
                    "Node '{}' has stats, current players: {}/{} (link count {})",
                    node.getName(),
                    event.getPlayingPlayers(),
                    event.getPlayers(),
                    client.getLinks().size()
            );
        });
    }

    private void onNextTrack(TrackContext newTrack) {
        TextChannel textChannel = newTrack.getTextChannel();
        musicService.whatsPlayingNowWithoutInteraction(textChannel, newTrack);
    }
}
