package org.serje3.commands.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.meta.abs.Command;

public class PauseCommand extends Command {
    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        client.getLink(event.getGuild().getIdLong())
                .getPlayer()
                .flatMap((player) -> player.setPaused(!player.getPaused()).asMono())
                .subscribe((player) -> {
                    event.reply("Player has been " + (player.getPaused() ? "paused" : "resumed") + "!").queue();
                });
    }
}
