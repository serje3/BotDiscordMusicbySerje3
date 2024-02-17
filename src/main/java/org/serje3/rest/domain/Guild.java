package org.serje3.rest.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Guild {
    private final String guildId;
    private final String ownerId;
    private final String name;
    //    private final Role botRole;
//    private final Channel systemChannel;
    private final String iconUrl;

    public static Guild fromDiscordGuild(net.dv8tion.jda.api.entities.Guild discordGuild) {
        if (discordGuild == null) throw new IllegalArgumentException("discord.guild.null");

        return new Guild(
                discordGuild.getId(),
                discordGuild.getOwnerId(),
                discordGuild.getName(),
                discordGuild.getIconUrl()
        );
    }
}
