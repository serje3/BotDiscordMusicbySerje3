package org.serje3.commands.log;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.meta.abs.Command;

public class TestCommand extends Command {
    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getDescription() {
        return "just a test command";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        event.reply("Hello everyone").queue();
    }
}
