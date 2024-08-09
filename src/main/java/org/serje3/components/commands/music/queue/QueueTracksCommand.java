package org.serje3.components.commands.music.queue;

import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.domain.TrackContext;
import org.serje3.meta.abs.Command;
import org.serje3.utils.TrackQueue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
    public void execute(SlashCommandInteractionEvent event) {
        Long guildId = event.getGuild().getIdLong();
        List<TrackContext> trackContextList = TrackQueue.getQueueList(guildId);
        TrackContext now = TrackQueue.peekNow(guildId);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Текущий плейлист и трек");

        embedBuilder.addField("Играет сейчас:", now != null ? now.getTitle() : "Ничего", false);

        StringBuilder trackListBuilder = new StringBuilder();
        AtomicInteger count = new AtomicInteger(0);
        trackContextList.forEach(context -> {
            Track track = context.getTrack();
            count.incrementAndGet();
            trackListBuilder.append(count).append(". ").append(track != null ? track.getInfo().getTitle() : context.getTitle()).append("\n");
        });

        String tracksList = trackListBuilder.toString();
        if (tracksList.length() > 850) {
            tracksList = tracksList.substring(0, 850) + "\n...[не могу вывести всё]...";
        }

        embedBuilder.addField("Сейчас в очереди:", !tracksList.isEmpty() ? tracksList : "Пусто", false);

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
