package org.serje3.components.commands.music.queue;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.serje3.meta.abs.Command;
import org.serje3.utils.TrackQueue;

public class QueueClearCommand extends Command {
    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "Очистить очередь";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        TrackQueue.clear(event.getGuild().getIdLong());
        event.reply("Очередь очищена").queue();
    }
}
