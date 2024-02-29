package org.serje3.components.commands.music.queue;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.protocol.Track;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.serje3.domain.TrackContext;
import org.serje3.meta.abs.Command;
import org.serje3.utils.TrackQueue;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class QueueTracksCommand extends Command {
    @Override
    public String getName() {
        return "tracks";
    }

    @Override
    public String getDescription() {
        return "Показывает список треков в очереди";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        Long guildId = event.getGuild().getIdLong();
        List<TrackContext> trackContextList = TrackQueue.listQueue(guildId);
        List<Track> tracks = trackContextList.stream().map(TrackContext::getTrack).collect(Collectors.toList());
        System.out.println(tracks);
        Link link = client.getLink(event.getGuild().getIdLong());
        link.getPlayer().subscribe(lavalinkPlayer -> {
            System.out.println("CURRENT TRACK " + lavalinkPlayer.getTrack());
            System.out.println("CURRENT PLAYER " + lavalinkPlayer.getPosition());
        });
        AtomicReference<Integer> count = new AtomicReference<>(0);
        String tracksList = tracks.stream().map((t) -> {
            count.updateAndGet(v -> v + 1);
            return count + " " + t.getInfo().getTitle();
        }).collect(Collectors.joining("\n"));
        event.reply(
                "Сейчас в очереди: \n" + ((tracksList.length() != 0) ? tracksList : "Пусто")
                ).queue();
    }
}
