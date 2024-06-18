package org.serje3.meta.abs;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.serje3.config.BotConfig;
import org.serje3.utils.exceptions.NoTrackIsPlayingNow;

import java.io.IOException;

public abstract class Command {

    public abstract String getName();

    public final String getCommandName() {
        boolean isTest = false;
        try {
            isTest = BotConfig.getProperty("test").equals("true");
        } catch (IOException ignored) {
        }


        String prefix = isTest ? "test_" : "";
        return prefix + getName();
    }

    public abstract String getDescription();

    public SlashCommandData getSlashCommand() {
        return getDefaultSlashCommand(getDescription());
    }

    public SlashCommandData getDefaultSlashCommand(String description) {
        return Commands.slash(getCommandName(), description.length() > 99 ? description.substring(0, 99) : description);
    }

    public abstract void execute(SlashCommandInteractionEvent event);
}
