package org.serje3.services;

import dev.arbjerg.lavalink.client.*;
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider;
import lombok.Getter;
import org.serje3.rest.handlers.NodeRestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LavalinkService {

    private static LavalinkService instance = null;

    @Getter
    private final LavalinkClient client;
    private final Logger logger = LoggerFactory.getLogger(LavalinkService.class);
    private final NodeService nodeService;

    public Link getLink(Long guildId) {
        Link link = client.getLinkIfCached(guildId);
        if (link == null || link.getState() != LinkState.CONNECTED) {
            logger.info("Link for {} is null or not connected. Current state is {} Creating new link...", guildId, link != null ? link.getState() : "Unknown");
            link = client.getOrCreateLink(guildId);
        }

        return link;
    }

    public void destroyLink(Long guildId) {
        Link link = client.getLinkIfCached(guildId);
        if (link != null) {
            link.destroy().subscribe(u -> logger.info("Destroying link {}", u));
        }
    }


    private LavalinkService() {
        logger.info("Creating LavalinkService...");
        String token = System.getenv("BOT_TOKEN");
        if (token == null) {
            throw new RuntimeException("No token provided");
        }

        client = new LavalinkClient(Helpers.getUserIdFromToken(token));

        client.getLoadBalancer().addPenaltyProvider(new VoiceRegionPenaltyProvider());

        nodeService = new NodeService(this);
        nodeService.register();
        logger.info("LavalinkService created");
    }


    public static synchronized LavalinkService getInstance() {
        if (instance == null) {
            instance = new LavalinkService();
        }
        return instance;
    }

}
