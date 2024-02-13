package org.serje3.commands.music.queue;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.serje3.meta.abs.Command;

public class QueueSkipCommand extends Command {
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
        Link link = client.getLink(event.getGuild().getIdLong());
        link.getPlayer().subscribe(player -> {
            if (player.getTrack() == null || !player.getState().getConnected()){
                return;
            }
            System.out.println("SKIPPING " + player.getTrack().getInfo().getTitle());
            System.out.println("isStream " + player.getTrack().getInfo().isStream());
            System.out.println("isSeekable " + player.getTrack().getInfo().isSeekable());

            link.createOrUpdatePlayer().setPosition(player.getTrack().getInfo().getLength())
                    .subscribe(d -> {
                        System.out.println("succ& " + d);
                    });
        });
        event.reply("Трек пропущен").queue();
    }
}
