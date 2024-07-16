package org.serje3.utils;

import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.domain.TrackContext;

public class SlashEventHelper {
    public static TrackContext createTrackContextFromEvent(Track track, SlashCommandInteractionEvent event) {
        return createTrackContextFromDiscordMeta(track, event.getMember(), event.getChannel().asTextChannel());
    }

    public static TrackContext createTrackContextFromDiscordMeta(Track track, Member member, TextChannel textChannel) {
        return TrackContext.builder()
                .track(track)
                .member(member)
                .textChannel(textChannel)
                .repeat(false)
                .paused(false)
                .retryCount(0)
                .build();
    }
}
