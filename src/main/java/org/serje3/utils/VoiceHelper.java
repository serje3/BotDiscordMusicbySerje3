package org.serje3.utils;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.protocol.v4.Track;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.net.URI;
import java.net.URISyntaxException;

public class VoiceHelper {

    public static void joinHelper(SlashCommandInteractionEvent event) {
        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (memberVoiceState.inAudioChannel()) {
            event.getJDA().getDirectAudioController().connect(memberVoiceState.getChannel());
        }
    }

    public static void play(Link link, Track track, Integer volume) {
        link.createOrUpdatePlayer()
                .setEncodedTrack(track.getEncoded())
                .setVolume(volume).setNoReplace(false)
                .subscribe((ignored) -> {
                    System.out.println("player - " + ignored);
                });
    }


    public static MessageEmbed wrapTrackEmbed(Track track, Member member, String description) {
        if (track == null) return null;
        String title = track.getInfo().getTitle();
        String author = track.getInfo().getAuthor();
        String url = track.getInfo().getUri();
        String artUrl = track.getInfo().getArtworkUrl();
        String source = track.getInfo().getSourceName();
        try {
            URI uri = new URI("https", "cataas.com", "/cat/says/" + title, "fontSize=30&fontColor=red",null);
            String thumbnailUrl = uri.toString();
            int color = member.getColorRaw();

            System.out.println(thumbnailUrl);

            return new MessageEmbed(
                    url,
                    title,
                    description + "\nИсточник: " + source,
                    EmbedType.AUTO_MODERATION,
                    null,
                    color,
                    new MessageEmbed.Thumbnail(thumbnailUrl, thumbnailUrl, 100, 100),
                    null,
                    new MessageEmbed.AuthorInfo(author, url, null, null),
                    null,
                    new MessageEmbed.Footer(member.getEffectiveName(), member.getEffectiveAvatarUrl(), member.getEffectiveAvatarUrl()),
                    new MessageEmbed.ImageInfo(artUrl, artUrl, 100, 100),
                    null
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
