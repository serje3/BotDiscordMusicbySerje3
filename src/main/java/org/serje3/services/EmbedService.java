package org.serje3.services;

import dev.arbjerg.lavalink.client.player.Track;
import io.sentry.Sentry;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.net.URI;
import java.net.URISyntaxException;

public class EmbedService {

    private static EmbedService instance = null;

    private EmbedService() {}


    public MessageEmbed wrapTrackEmbed(Track track, Member member, String description) {
        if (track == null) return null;
        String title = track.getInfo().getTitle();
        String author = track.getInfo().getAuthor();
        String url = track.getInfo().getUri();
        String artUrl = track.getInfo().getArtworkUrl();
        String source = track.getInfo().getSourceName();
        try {
            URI uri = new URI("https", "cataas.com", "/cat/says/" + title, "fontSize=30&fontColor=red", null);
            String thumbnailUrl = uri.toString();

            System.out.println(thumbnailUrl);

            return getMessageTrackEmbed(url, title, description, source, thumbnailUrl, member, author, artUrl);
        } catch (URISyntaxException e) {
            Sentry.captureException(e);
            throw new RuntimeException(e);
        }
    }

    public MessageEmbed getMessageTrackEmbed(String url,
                                               String title,
                                               String description,
                                               String source,
                                               String thumbnailUrl,
                                               Member member,
                                               String author,
                                               String artUrl
    ) {
        return new MessageEmbed(
                url,
                title,
                description + "\nИсточник: " + source,
                EmbedType.AUTO_MODERATION,
                null,
                member.getColorRaw(),
                new MessageEmbed.Thumbnail(thumbnailUrl, thumbnailUrl, 100, 100),
                null,
                new MessageEmbed.AuthorInfo(author, url, null, null),
                null,
                new MessageEmbed.Footer(member.getEffectiveName(), member.getEffectiveAvatarUrl(), member.getEffectiveAvatarUrl()),
                new MessageEmbed.ImageInfo(artUrl, artUrl, 100, 100),
                null
        );
    }


    public static synchronized EmbedService getInstance() {
        if (instance == null) {
            instance = new EmbedService();
        }
        return instance;
    }
}
