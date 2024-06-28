package org.serje3.meta.interfaces;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface CommandExecutable {
    void execute(SlashCommandInteractionEvent event);
}
