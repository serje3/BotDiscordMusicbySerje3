package org.serje3.commands.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.serje3.meta.abs.Command;

public class PauseCommand extends Command {
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
        client.getLink(event.getGuild().getIdLong())
                .getPlayer()
                .flatMap((player) -> player.setPaused(!player.getPaused()))
                .subscribe((player) -> {
                    event.reply("Плеер " + (player.getPaused() ? "на паузе" : "возобновлён") + "!").queue();
                });
    }
}
