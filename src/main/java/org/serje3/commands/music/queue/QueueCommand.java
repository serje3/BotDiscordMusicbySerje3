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
import org.serje3.utils.exceptions.NoTracksInQueueException;

import java.util.List;

public class QueueCommand extends PlayCommand {
    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return getDefaultSlashCommand("[BETA] Добавить музыку в очередь")
                .addSubcommands(
                        new SubcommandData("youtube", "Поиск из ютуба")
                                .addOption(
                                        OptionType.STRING,
                                        "текст",
                                        "Строка поиска youtube",
                                        true
                                ),
                        new SubcommandData("soundcloud", "Поиск из soundclound")
                                .addOption(
                                        OptionType.STRING,
                                        "текст",
                                        "Строка поиска soundcloud",
                                        true
                                ),
                        new SubcommandData("yandexmusic", "Поиск из Yandex Music")
                                .addOption(
                                        OptionType.STRING,
                                        "текст",
                                        "Строка поиска Yandex Music",
                                        true
                                )
                );
    }

    @Override
    @JoinVoiceChannel
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        final Guild guild = event.getGuild();

        final PlaySourceType playType = PlaySourceType.valueOf(event.getSubcommandName().toUpperCase());
        String prefix = switch (playType) {
            case YOUTUBE -> "ytsearch:";
            case SOUNDCLOUD -> "scsearch:";
            case YANDEXMUSIC -> "ymsearch:";
            default -> "";
        };

        final String identifier = event.getOption("текст").getAsString();
        if (identifier.startsWith("https://")) {
            prefix = "";
        }
        final long guildId = guild.getIdLong();
        this.play(client, event, guildId, prefix + identifier, 35);
    }

    @Override
    public void play(LavalinkClient client, SlashCommandInteractionEvent event,
                     Long guildId, String identifier, Integer volume) {
        final Link link = client.getLink(guildId);
        link.loadItem(identifier).subscribe((item) -> {
            System.out.println(item);
            if (item instanceof LoadResult.TrackLoaded trackLoaded) {
                final Track track = trackLoaded.getData();

                System.out.println("Размер очереди - " + TrackQueue.size(guildId));
                queue(client, link, guildId, track);
                event.reply(track.getInfo().getTitle() + " - Добавлен в очередь").queue();
            } else if (item instanceof LoadResult.PlaylistLoaded playlistLoaded) {
                final int trackCount = playlistLoaded.getData().getTracks().size();
                event.getHook()
                        .sendMessage("Этот плейлист имеет " + trackCount + " треков! Но хуй его запущу сосите")
                        .queue();
            } else if (item instanceof LoadResult.SearchResult searchResult) {
                final List<Track> tracks = searchResult.getData().getTracks();

                if (tracks.isEmpty()) {
                    event.getHook().sendMessage("Ни одного трека не найдено!").queue();
                    return;
                }

                final Track firstTrack = tracks.get(0);

                queue(client, link, guildId, firstTrack);
                event.reply(firstTrack.getInfo().getTitle() + " - Добавлен в очередь").queue();
            } else if (item instanceof LoadResult.NoMatches) {
                event.getHook().sendMessage("Ничего не найдено по вашему запросу!").queue();
            } else if (item instanceof LoadResult.LoadFailed fail) {
                event.getHook().sendMessage("ЕБАТЬ Failed to load track! " + fail.getData().getMessage()).queue();
            }
        });
    }


    private void queue(LavalinkClient client, Link link, Long guildId, Track track) {
        TrackQueue.add(guildId, track);
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
