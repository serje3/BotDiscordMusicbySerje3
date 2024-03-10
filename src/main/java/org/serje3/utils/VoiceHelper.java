package org.serje3.utils;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.protocol.Track;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.utils.exceptions.NoTracksInQueueException;

import java.net.URI;
import java.net.URISyntaxException;

public class VoiceHelper {

    public static void joinHelper(SlashCommandInteractionEvent event) {
        final Member member = event.getMember();
        final Guild guild = event.getGuild();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        guild.getAudioManager().setSelfDeafened(true);
        if (memberVoiceState.inAudioChannel()) {
            event.getJDA().getDirectAudioController().connect(memberVoiceState.getChannel());
        }
    }

    public static void play(Link link, Track track, Integer volume) {
        link.createOrUpdatePlayer()
                .setEncodedTrack(track.getEncoded())
                .setVolume(volume)
                .setNoReplace(false)
                .setEndTime(track.getInfo().getLength())
                .subscribe((ignored) -> {
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

    public static void queue(LavalinkClient client, Link link, Long guildId) {
        link.getPlayer().subscribe((player) -> {

            System.out.println(player.getState() + " " + player.getTrack());
            boolean isStopped = !player.getState().getConnected() || player.getTrack() == null
                    || player.getPosition() >= player.getTrack().getInfo().getLength();

            if (isStopped) {
                try {
                    System.out.println("START QUEUE");
                    TrackQueue.skip(client, guildId, false);
                } catch (NoTracksInQueueException e) {
                    // Такое может произойти в очень редких случаях
                    // с учётом того что перед тем как запустить queue,
                    // мы добавляем трек в TrackQueue
                    // В случае если это все-таки произошло - удалите system32,
                    // а если вы на linux или macos, то напишите rm -rf /. И проблема исчезнет
                }
            }
        });
    }
}
