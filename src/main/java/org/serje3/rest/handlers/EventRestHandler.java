package org.serje3.rest.handlers;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.config.GuildConfig;
import org.serje3.rest.base.BaseRestClient;
import org.serje3.rest.domain.Guild;
import org.serje3.rest.domain.GuildId;
import org.serje3.rest.domain.Member;
import org.serje3.rest.domain.UserId;
import org.serje3.rest.requests.SlashCommandEventRequest;

import java.net.http.HttpResponse;

public class EventRestHandler extends BaseRestClient {
    public void handleSlashEvent(SlashCommandInteractionEvent event) {
        String commandString = event.getName();
        String fullCommandString = event.getFullCommandName();
        net.dv8tion.jda.api.entities.Member discordMember = event.getMember();
        net.dv8tion.jda.api.entities.Guild discordGuild = event.getGuild();
        if (discordMember == null || discordGuild == null) {
            return;
        }
        Member member = Member.fromDiscordMember(discordMember);
        Guild guild = Guild.fromDiscordGuild(discordGuild);
        SlashCommandEventRequest request = new SlashCommandEventRequest(
                commandString,
                fullCommandString,
                member,
                guild
        );

        if (event.getChannel().getType() == ChannelType.TEXT) {
            GuildConfig.setLastInteractionTextChannel(discordGuild.getIdLong(), event.getChannel().asTextChannel());
            GuildConfig.setLastInteractedMember(discordGuild.getIdLong(), event.getMember());
        }

        this.post("/events/slash-received", request, Object.class);
    }
}
