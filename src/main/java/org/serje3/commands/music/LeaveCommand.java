package org.serje3.commands.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.meta.abs.Command;

import java.util.Objects;

public class LeaveCommand extends Command {
    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        event.getJDA().getDirectAudioController().disconnect(Objects.requireNonNull(event.getGuild()));
        client.getLink(event.getGuild().getIdLong()).destroyPlayer().subscribe(System.out::println);
        event.reply("Leaving your channel!").queue();
    }
}
