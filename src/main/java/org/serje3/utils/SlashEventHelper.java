package org.serje3.utils;

import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.domain.TrackContext;

public class SlashEventHelper {
    public static TrackContext createTrackContextFromEvent(Track track, SlashCommandInteractionEvent event, String trackIdentifier) {
        return createTrackContextFromDiscordMeta(track, event.getMember(), event.getChannel().asTextChannel(), event.getGuild().getIdLong(), trackIdentifier);
    }

    private static TrackContext.TrackContextBuilder builder_createTrackContextFromDiscordMeta(Track track, Member member, TextChannel textChannel, String trackIdentifier) {
        return TrackContext.builder()
                .track(track)
                .title(track != null ? track.getInfo().getAuthor() + " - " + track.getInfo().getTitle() : trackIdentifier)
                .member(member)
                .identifier(trackIdentifier)
                .textChannel(textChannel)
                .repeat(false)
                .paused(false)
                .retryCount(0);
    }

    public static TrackContext createTrackContextFromDiscordMeta(Track track, Member member, TextChannel textChannel, String trackIdentifier) {
        return builder_createTrackContextFromDiscordMeta(track, member, textChannel, trackIdentifier)
                .guildId(member != null ? member.getGuild().getIdLong() : null)
                .build();
    }

    public static TrackContext createTrackContextFromDiscordMeta(Track track, Member member, TextChannel textChannel, Long guildId, String trackIdentifier) {
        return builder_createTrackContextFromDiscordMeta(track, member, textChannel, trackIdentifier)
                .guildId(guildId)
                .build();
    }
}
