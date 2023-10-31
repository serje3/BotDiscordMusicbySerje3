package org.serje3.commands.music.queue;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.meta.abs.Command;
import org.serje3.utils.TrackQueue;

public class QueueClearCommand extends Command {
    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        TrackQueue.clear(event.getGuild().getIdLong());
        event.reply("Очередь очищена").queue();
    }
}
