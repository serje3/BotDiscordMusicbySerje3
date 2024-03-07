package org.serje3.components.commands.music.queue;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.protocol.*;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.components.commands.music.PlayCommand;
import org.serje3.domain.TrackContext;
import org.serje3.meta.annotations.JoinVoiceChannel;
import org.serje3.utils.SlashEventHelper;
import org.serje3.utils.TrackQueue;
import org.serje3.utils.VoiceHelper;
import org.serje3.utils.exceptions.NoTracksInQueueException;

import java.util.List;
import java.util.stream.Collectors;

public class QueueCommand extends PlayCommand {
    @Override
    @JoinVoiceChannel
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        super.execute(event, client);
    }

    @Override
    public void play(LavalinkClient client, SlashCommandInteractionEvent event,
                     Long guildId, String identifier, Integer volume) {
        event.deferReply().queue();
        System.out.println("IDENTIFIER:" + identifier);
        final Link link = client.getLink(guildId);
        link.loadItem(identifier).subscribe((item) -> {
            System.out.println(item);
            if (item instanceof TrackLoaded trackLoaded) {
                final Track track = trackLoaded.getTrack();
                TrackQueue.add(guildId, SlashEventHelper.createTrackContextFromEvent(track, event));

                System.out.println("Размер очереди - " + TrackQueue.size(guildId));

                VoiceHelper.queue(client, link, guildId);
                event.getHook().sendMessageEmbeds(VoiceHelper.wrapTrackEmbed(track, event.getMember(), "Добавлен в очередь"))
                        .queue();
            } else if (item instanceof PlaylistLoaded playlistLoaded) {
                final List<Track> tracks = playlistLoaded.getTracks();
                if (tracks.isEmpty()) {
                    event.getHook().sendMessage("В этом плейлисте нет треков").queue();
                    return;
                } else if (tracks.size() >= 10000) {
                    event.getHook().sendMessage("Этот плейлист слишком большой").queue();
                    return;
                }

                List<TrackContext> trackContextList = tracks.stream()
                        .map(track -> SlashEventHelper.createTrackContextFromEvent(track, event)).toList();

                TrackQueue.addAll(guildId, trackContextList);
                final int trackCount = tracks.size();

                VoiceHelper.queue(client, link, guildId);
                event.getHook().sendMessage("Этот плейлист имеет " + trackCount + " треков! Запускаю - " + tracks.get(0).getInfo().getTitle())
                        .queue();
            } else if (item instanceof SearchResult searchResult) {
                final List<Track> tracks = searchResult.getTracks();

                if (tracks.isEmpty()) {
                    event.getHook().sendMessage("Ни одного трека не найдено!").queue();
                    return;
                }

                final Track firstTrack = tracks.get(0);
                final TrackContext trackContext = SlashEventHelper.createTrackContextFromEvent(firstTrack, event);

                TrackQueue.add(guildId, trackContext);

                VoiceHelper.queue(client, link, guildId);

                event.getHook().sendMessageEmbeds(VoiceHelper.wrapTrackEmbed(firstTrack, event.getMember(), "Добавлен в очередь"))
                        .queue();
            } else if (item instanceof NoMatches) {
                event.getHook().sendMessage("Ничего не найдено по вашему запросу!").queue();
            } else if (item instanceof LoadFailed fail) {
                event.getHook().sendMessage("ЕБАТЬ НЕ ПОЛУЧИЛОСЬ ЗАГРУЗИТЬ АУДИО! Ашибка: " + fail.getException().getMessage()).queue();
            }
        });
    }
}
