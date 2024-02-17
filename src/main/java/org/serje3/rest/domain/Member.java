package org.serje3.rest.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Member {
    private final String userId;
    private final String guildId;

    private final String effectiveUsername;

    public static Member fromDiscordMember(net.dv8tion.jda.api.entities.Member discordMember) {
        if (discordMember == null) throw new IllegalArgumentException("discord.member.null");
        return new Member(
                discordMember.getUser().getId(),
                discordMember.getGuild().getId(),
                discordMember.getEffectiveName()
        );
    }
}