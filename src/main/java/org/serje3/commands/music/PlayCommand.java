package org.serje3.commands.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.protocol.v4.LoadResult;
import dev.arbjerg.lavalink.protocol.v4.Track;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.serje3.meta.abs.Command;
import org.serje3.meta.enums.PlaySourceType;
import org.serje3.meta.annotations.JoinVoiceChannel;

import java.util.List;
import java.util.Objects;

public class PlayCommand extends Command {
    @Override
    public String getName() {
        return "play";
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return getDefaultSlashCommand("Играет музыку")
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
        this.play(client, event, guildId, prefix + identifier);
    }

    public void play(LavalinkClient client, SlashCommandInteractionEvent event,
                     Long guildId, String identifier) {
        this.play(client, event, guildId, identifier, 35);
    }

    public void play(LavalinkClient client, SlashCommandInteractionEvent event,
                     Long guildId, String identifier, Integer volume) {

        final Link link = client.getLink(guildId);
        link.loadItem(identifier).subscribe((item) -> {
            System.out.println(item);
            if (item instanceof LoadResult.TrackLoaded trackLoaded) {
                final Track track = trackLoaded.getData();

                link.createOrUpdatePlayer()
                        .setEncodedTrack(track.getEncoded())
                        .setVolume(volume)
                        .setEndTime(track.getInfo().getLength())
                        .asMono()
                        .subscribe((ignored) -> {
                            event.getHook().sendMessage("Сейчас играет: " + track.getInfo().getTitle()).queue();
                        });
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


                // This is a different way of updating the player! Choose your preference!
                // This method will also create a player if there is not one in the server yet
                link.updatePlayer((update) ->
                                update
                                        .setEncodedTrack(firstTrack.getEncoded())
                                        .setVolume(volume)
                                        .setEndTime(firstTrack.getInfo().getLength())
                                        .setNoReplace(false)
                        )
                        .subscribe((ignored) -> {
                            event.getHook().sendMessage("Сейчас играет: " + firstTrack.getInfo().getTitle()).queue();
                        });

            } else if (item instanceof LoadResult.NoMatches) {
                event.getHook().sendMessage("Ничего не найдено по вашему запросу!").queue();
            } else if (item instanceof LoadResult.LoadFailed fail) {
                event.getHook().sendMessage("ЕБАТЬ Failed to load track! " + fail.getData().getMessage()).queue();
            }
        });
    }
}
