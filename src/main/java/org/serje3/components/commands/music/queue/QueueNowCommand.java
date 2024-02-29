package org.serje3.components.commands.music.queue;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.meta.abs.Command;
import org.serje3.services.MusicService;


public class QueueNowCommand extends Command {
    private final MusicService musicService = new MusicService();

    @Override
    public String getName() {
        return "now";
    }

    @Override
    public String getDescription() {
        return "Узнать что сейчас играет";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        musicService.whatsPlayingNow(event, client);
    }
}
