package org.serje3.components.commands.suno.handlers;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.components.modals.suno.SunoLoginModal;
import org.serje3.meta.interfaces.CommandExecutable;

public class Login implements CommandExecutable {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.replyModal(new SunoLoginModal().build()).queue();
    }
}
