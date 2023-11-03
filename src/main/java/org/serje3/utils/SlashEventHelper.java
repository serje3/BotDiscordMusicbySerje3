package org.serje3.utils;

import dev.arbjerg.lavalink.protocol.v4.Track;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.domain.TrackContext;

public class SlashEventHelper {
    public static TrackContext createTrackContextFromEvent(Track track, SlashCommandInteractionEvent event) {
        return TrackContext.builder()
                .track(track)
                .member(event.getMember())
                .textChannel(event.getChannel().asTextChannel())
                .build();
    }
}
