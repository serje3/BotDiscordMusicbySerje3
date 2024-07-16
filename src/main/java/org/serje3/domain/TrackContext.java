package org.serje3.domain;

import dev.arbjerg.lavalink.client.player.Track;
import lombok.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackContext {
    private Track track;
    private Member member;
    private TextChannel textChannel;
    private volatile boolean repeat;
    private volatile boolean paused;
    private volatile int retryCount;
}
