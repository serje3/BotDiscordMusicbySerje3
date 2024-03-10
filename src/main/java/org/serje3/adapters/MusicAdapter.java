package org.serje3.adapters;

import dev.arbjerg.lavalink.client.*;
import dev.arbjerg.lavalink.client.loadbalancing.RegionGroup;
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider;
import dev.arbjerg.lavalink.protocol.v4.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.serje3.domain.TrackContext;
import org.serje3.meta.abs.AdapterContext;
import org.serje3.meta.abs.BaseListenerAdapter;
import org.serje3.meta.abs.Command;
import org.serje3.meta.decorators.MusicCommandDecorator;
import org.serje3.rest.domain.NodeRef;
import org.serje3.rest.handlers.NodeRestHandler;
import org.serje3.services.MusicService;
import org.serje3.utils.commands.MusicAdapterContext;
import org.serje3.utils.TrackQueue;
import org.serje3.utils.exceptions.NoTracksInQueueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.serje3.BotApplication.Bot;

public class MusicAdapter extends BaseListenerAdapter {
    private final MusicService musicService;
    private final NodeRestHandler nodeRestHandler;
    private final Logger logger = LoggerFactory.getLogger(MusicAdapter.class);


    public MusicAdapter(LavalinkClient client) {
        super();
        this.nodeRestHandler = new NodeRestHandler();
        this.client = client;
        this.client.getLoadBalancer().addPenaltyProvider(new VoiceRegionPenaltyProvider());
        this.registerLavalinkNodes();
        this.registerLavalinkListeners();

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

    private void registerLavalinkNodes() {
        List<NodeRef> nodes = null;
        try{
            nodes = this.nodeRestHandler.getNodes();
            System.out.println(nodes);
        }catch (Exception e){
            System.out.println(e.getMessage());
//            throw new RuntimeException("Error with nodes receive: " + e.getMessage());
        }

        if (nodes == null || nodes.isEmpty()){
            nodes = List.of(new NodeRef(0, "ws://localhost:2333", "testing", "EUROPE"));
        }

        nodes.stream().map(node -> client.addNode(
                node.getUrl(),
                URI.create(node.getUrl()),
                node.getPassword(),
                RegionGroup.EUROPE
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
                System.out.println("TRACK ENDED " + data.getEndReason());
                Long guildId = data.getGuildId();
                if (data.getEndReason() == Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason.LOAD_FAILED){
                    User userById = Bot.getUserById(263430624080035841L);
                    userById.openPrivateChannel().queue((channel) -> channel.sendMessage("Трек " + data.getTrack().getInfo().getTitle() + " закончился с ошибкой").queue());
                }
                try {
                    TrackContext newTrack = TrackQueue.skip(client, guildId, true);
                    onNextTrack(newTrack);
                } catch (NoTracksInQueueException e) {
                    //pass
                    logger.warn("No Tracks in Queue in guild {}", guildId);
                }
            });
        });
    }

    private void registerLavalinkListeners() {
        this.client.on(dev.arbjerg.lavalink.client.ReadyEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();

            System.out.printf(
                    "Node '%s' is ready, session id is '%s'!%n",
                    node.getName(),
                    event.getSessionId()
            );
        });

        this.client.on(StatsEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();


            System.out.printf(
                    "Node '%s' has stats, current players: %d/%d%n",
                    node.getName(),
                    event.getPlayingPlayers(),
                    event.getPlayers()
            );
        });
    }

    private void onNextTrack(TrackContext newTrack) {
        TextChannel textChannel = newTrack.getTextChannel();
        if (textChannel == null) return;
        MessageHistory history = textChannel.getHistoryAround(textChannel.getLatestMessageId(), 10).submit().join();
        List<net.dv8tion.jda.api.entities.Message> retrievedHistory = history.getRetrievedHistory();
        List<net.dv8tion.jda.api.entities.Message> messageToDelete = new ArrayList<>();
        for (net.dv8tion.jda.api.entities.Message message: retrievedHistory){
            net.dv8tion.jda.api.entities.Message.Interaction interaction = message.getInteraction();
            if (checkInteractionValidForDelete(interaction)){
                messageToDelete.add(message);
            }
        }
        if (!messageToDelete.isEmpty()) textChannel.deleteMessages(messageToDelete).queue();
        musicService.whatsPlayingNowWithoutInteraction(textChannel, newTrack);
    }

    private boolean checkInteractionValidForDelete(net.dv8tion.jda.api.entities.Message.Interaction interaction) {
        if (interaction == null) return false;
        String name = interaction.getName();
        return name.equals("now") || name.startsWith("play") || name.startsWith("radio");
    }
}
