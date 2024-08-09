package org.serje3.domain;

import dev.arbjerg.lavalink.client.player.LavalinkLoadResult;
import dev.arbjerg.lavalink.client.player.Track;
import dev.arbjerg.lavalink.client.player.TrackLoaded;
import lombok.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.serje3.services.LavalinkService;
import org.serje3.utils.SlashEventHelper;

import java.util.function.Consumer;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TrackContext {
    private Track track;
    private String identifier;
    private Member member;
    private Long guildId;
    private String title;
    private TextChannel textChannel;
    private volatile boolean repeat;
    private volatile boolean paused;
    private volatile int retryCount;


    public void retrieveTrackByIdentifier(Consumer<Track> onSuccess, Consumer<LavalinkLoadResult> onFailure) {
        LavalinkService.getInstance().getLink(member.getGuild().getIdLong())
                .loadItem(identifier)
                .subscribe(item -> {
                    if (item instanceof TrackLoaded trackLoaded){
                        onSuccess.accept(trackLoaded.getTrack());
                    } else {
                        onFailure.accept(item);
                    }
                });
    }
}
