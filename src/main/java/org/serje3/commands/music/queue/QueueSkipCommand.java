package org.serje3.commands.music.queue;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.meta.abs.Command;
import org.serje3.utils.TrackQueue;
import org.serje3.utils.exceptions.NoTracksInQueueException;

public class QueueSkipCommand extends Command {
    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        try {
            TrackQueue.skip(client, event.getGuild().getIdLong());
            event.reply("Трек пропущен").queue();
        } catch (NoTracksInQueueException e) {
            System.out.println("ПРОПУСК: Треков в очереди нет");
            Link link = client.getLink(event.getGuild().getIdLong());
            link.getPlayer().subscribe(player -> {
                link.createOrUpdatePlayer().setPosition(player.getTrack().getInfo().getLength())
                        .asMono().subscribe(d -> {
                    System.out.println("succ& " + d);
                });
            });
            event.reply("Трек пропущен, треков в очереди больше нет").queue();
        }
    }
}
