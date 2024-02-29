package org.serje3.components.commands.base;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
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
                .addActionRow(
                        Button.link("https://pay.cloudtips.ru/p/47dd3faa", "Оставить чаевые")
                                .withEmoji(Emoji.fromFormatted("\uD83E\uDE99"))
                )
                .queue();
    }
}
