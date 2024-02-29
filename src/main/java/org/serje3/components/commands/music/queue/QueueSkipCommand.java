package org.serje3.components.commands.music.queue;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.protocol.v4.VoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.serje3.meta.abs.Command;
import org.serje3.services.MusicService;

public class QueueSkipCommand extends Command {
    private final MusicService musicService = new MusicService();
    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getDescription() {
        return "Пропустить трек в очереди";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        musicService.skipTrack(event, client);
    }
}
