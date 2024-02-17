package org.serje3.meta.abs;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class Command {
    public abstract String getName();

    public abstract String getDescription();

    public SlashCommandData getSlashCommand() {
        return getDefaultSlashCommand(getDescription());
    }

    public SlashCommandData getDefaultSlashCommand(String description) {
        return Commands.slash(getName(), description.length() > 99 ? description.substring(0, 99): description);
    }

    public abstract void execute(SlashCommandInteractionEvent event, LavalinkClient client);
}
