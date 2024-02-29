package org.serje3.components.commands.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.serje3.meta.abs.Command;
import org.serje3.services.MusicService;

public class PauseCommand extends Command {
    private final MusicService musicService = new MusicService();
    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String getDescription() {
        return "Пауза трека";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        musicService.pauseMusic(event, client);
    }
}
