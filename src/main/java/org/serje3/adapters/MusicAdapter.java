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

    @Override
    protected AdapterContext getAdapterContext() {
        return new MusicAdapterContext();
    }


    @Override
    protected Command convertCommand(Command command) {
        return new MusicCommandDecorator(command);
    }
}
