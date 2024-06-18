package org.serje3.components.commands.music.queue;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.config.GuildConfig;
import org.serje3.meta.abs.Command;

public class QueueCockinizeCommand extends Command {
    @Override
    public String getName() {
        return "cockinize";
    }

    @Override
    public String getDescription() {
        return "Хуизация треков";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        boolean cockinize = GuildConfig.toggleCockinize(event.getGuild().getIdLong());
        String state = cockinize ? "Включена" : "Отключена";
        event.reply("Хуизация теперь `" + state + "`").queue();
    }
}
