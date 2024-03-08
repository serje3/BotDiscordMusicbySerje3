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
        TrackContext now = TrackQueue.peekNow(guildId);
        List<Track> tracks = trackContextList.stream().map(TrackContext::getTrack).collect(Collectors.toList());
        System.out.println(tracks);

        AtomicReference<Integer> count = new AtomicReference<>(0);
        String tracksList = tracks.stream().map((t) -> {
            count.updateAndGet(v -> v + 1);
            return count + " " + t.getInfo().getTitle();
        }).collect(Collectors.joining("\n"));
        String content = "Играет сейчас: " + (now != null ? now.getTrack().getInfo().getTitle() : "Ничего") + "\nСейчас в очереди: \n" + ((!tracksList.isEmpty()) ? tracksList : "Пусто");
        event.reply(content).queue();
    }
}
