package org.serje3.commands.music.queue;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.protocol.v4.LoadResult;
import dev.arbjerg.lavalink.protocol.v4.Track;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.serje3.commands.music.PlayCommand;
import org.serje3.meta.annotations.JoinVoiceChannel;
import org.serje3.meta.enums.PlaySourceType;
import org.serje3.utils.TrackQueue;
import org.serje3.utils.VoiceHelper;
import org.serje3.utils.exceptions.NoTracksInQueueException;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class QueueCommand extends PlayCommand {
    @Override
    @JoinVoiceChannel
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        super.execute(event, client);
    }

    @Override
    public void play(LavalinkClient client, SlashCommandInteractionEvent event,
                     Long guildId, String identifier, Integer volume) {
        final Link link = client.getLink(guildId);
        link.loadItem(identifier).subscribe((item) -> {
            System.out.println(item);
            if (item instanceof LoadResult.TrackLoaded trackLoaded) {
                final Track track = trackLoaded.getData();
                TrackQueue.add(guildId, track);

                System.out.println("Размер очереди - " + TrackQueue.size(guildId));

                queue(client, link, guildId);
                event.replyEmbeds(VoiceHelper.getTrackEmbed(track, event.getMember(), "Добавлен в очередь"))
                        .queue();
            } else if (item instanceof LoadResult.PlaylistLoaded playlistLoaded) {
                final List<Track> tracks = playlistLoaded.getData().getTracks();
                if (tracks.isEmpty()) {
                    event.reply("В этом плейлисте нет треков").queue();
                    return;
                } else if (tracks.size() >= 10000) {
                    event.reply("Этот плейлист слишком большой").queue();
                    return;
                }
                TrackQueue.addAll(guildId, tracks);
                final int trackCount = tracks.size();

                queue(client, link, guildId);
                event.reply("Этот плейлист имеет " + trackCount + " треков! Запускаю - " + tracks.get(0).getInfo().getTitle())
                        .queue();
            } else if (item instanceof LoadResult.SearchResult searchResult) {
                final List<Track> tracks = searchResult.getData().getTracks();

                if (tracks.isEmpty()) {
                    event.reply("Ни одного трека не найдено!").queue();
                    return;
                }

                final Track firstTrack = tracks.get(0);
                TrackQueue.add(guildId, firstTrack);

                queue(client, link, guildId);

                event.replyEmbeds(VoiceHelper.getTrackEmbed(firstTrack, event.getMember(), "Добавлен в очередь"))
                        .queue();
            } else if (item instanceof LoadResult.NoMatches) {
                event.getHook().sendMessage("Ничего не найдено по вашему запросу!").queue();
            } else if (item instanceof LoadResult.LoadFailed fail) {
                event.getHook().sendMessage("ЕБАТЬ Failed to load track! " + fail.getData().getMessage()).queue();
            }
        });
    }


    private void queue(LavalinkClient client, Link link, Long guildId) {
        link.getPlayer().subscribe((player) -> {
            System.out.println(player.getState() + " " + player.getTrack());
            boolean isStopped = !player.getState().getConnected() || player.getTrack() == null
                    || player.getPosition() >= player.getTrack().getInfo().getLength();
//            boolean isStream = player.getTrack() != null && player.getTrack().getInfo().isStream();
            System.out.println("P: " + isStopped);
            if (isStopped) {
                try {
                    System.out.println("START QUEUE");
                    TrackQueue.skip(client, guildId);
                } catch (NoTracksInQueueException e) {
                    // Такое может произойти в очень редких случаях
                }
            }
        });
    }
}
