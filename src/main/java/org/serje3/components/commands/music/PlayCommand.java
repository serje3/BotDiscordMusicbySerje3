package org.serje3.components.commands.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.protocol.PlaylistLoaded;
import dev.arbjerg.lavalink.client.protocol.SearchResult;
import dev.arbjerg.lavalink.client.protocol.Track;
import dev.arbjerg.lavalink.client.protocol.TrackLoaded;
import dev.arbjerg.lavalink.client.protocol.NoMatches;
import dev.arbjerg.lavalink.client.protocol.LoadFailed;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.serje3.meta.abs.Command;
import org.serje3.meta.enums.PlaySourceType;
import org.serje3.meta.annotations.JoinVoiceChannel;
import org.serje3.services.MusicService;

import java.util.List;

public class PlayCommand extends Command {
    private final MusicService musicService = new MusicService();

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Включает музыку, доступно для выбора несколько источников воспроизведения";
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return super.getSlashCommand()
                .addSubcommands(
                        new SubcommandData(PlaySourceType.YOUTUBE.name().toLowerCase(), "Поиск из ютуба")
                                .addOption(
                                        OptionType.STRING,
                                        "текст".toLowerCase(),
                                        "Строка поиска youtube",
                                        true,
                                        true
                                ),
                        new SubcommandData(PlaySourceType.SOUNDCLOUD.name().toLowerCase(), "Поиск из soundclound")
                                .addOption(
                                        OptionType.STRING,
                                        "текст",
                                        "Строка поиска soundcloud",
                                        true,
                                        true
                                ),
                        new SubcommandData(PlaySourceType.YANDEXMUSIC.name().toLowerCase(), "Поиск из Yandex Music")
                                .addOption(
                                        OptionType.STRING,
                                        "текст",
                                        "Строка поиска Yandex Music",
                                        true,
                                        true
                                ),
                        new SubcommandData(PlaySourceType.SPOTIFY.name().toLowerCase(), "Поиск из spotify")
                                .addOption(
                                        OptionType.STRING,
                                        "текст",
                                        "Строка поиска Spotify",
                                        true,
                                        true
                                )
                );
    }

    @Override
    @JoinVoiceChannel
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        final Guild guild = event.getGuild();

        final String identifier = event.getOption("текст").getAsString();
        final long guildId = guild.getIdLong();

        this.play(client, event, guildId, musicService.getSearchPrefix(event.getSubcommandName(), identifier) + identifier);
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
            if (item instanceof TrackLoaded trackLoaded) {
                final Track track = trackLoaded.getTrack();

                link.createOrUpdatePlayer()
                        .setEncodedTrack(track.getEncoded())
                        .setVolume(volume)
                        .setEndTime(track.getInfo().getLength())
                        .subscribe((ignored) -> {
                            event.getHook().sendMessage("Сейчас играет: " + track.getInfo().getTitle()).queue();
                        });
            } else if (item instanceof PlaylistLoaded playlistLoaded) {
                final int trackCount = playlistLoaded.getTracks().size();
                event.getHook()
                        .sendMessage("Этот плейлист имеет " + trackCount + " треков! Но хуй его запущу сосите")
                        .queue();
            } else if (item instanceof SearchResult searchResult) {
                final List<Track> tracks = searchResult.getTracks();

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

            } else if (item instanceof NoMatches) {
                event.getHook().sendMessage("Ничего не найдено по вашему запросу!").queue();
            } else if (item instanceof LoadFailed fail) {
                event.getHook().sendMessage("ЕБАТЬ Failed to load track! " + fail.getException().getMessage()).queue();
            }
        });
    }
}
