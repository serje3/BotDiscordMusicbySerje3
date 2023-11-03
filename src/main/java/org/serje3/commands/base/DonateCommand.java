package org.serje3.commands.base;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.meta.abs.Command;

public class DonateCommand extends Command {
    @Override
    public String getName() {
        return "donate";
    }

    @Override
    public String getDescription() {
        return "Поддержать разработчика";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        event.reply("""
                        Поддержать:
                        USDT TRC20 - THwRveqYjoRCcpUM2Ardxs687KBfU37GiS \s
                        """)
                .queue();
    }
}
