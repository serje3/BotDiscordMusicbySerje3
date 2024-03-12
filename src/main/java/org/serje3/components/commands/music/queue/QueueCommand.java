package org.serje3.components.commands.music.queue;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.loadbalancing.VoiceRegion;
import dev.arbjerg.lavalink.client.protocol.*;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.components.buttons.music.AddToQueueButton;
import org.serje3.components.commands.music.PlayCommand;
import org.serje3.domain.TrackContext;
import org.serje3.meta.annotations.JoinVoiceChannel;
import org.serje3.meta.enums.PlaySourceType;
import org.serje3.rest.domain.Tracks;
import org.serje3.rest.handlers.YoutubeRestHandler;
import org.serje3.utils.SlashEventHelper;
import org.serje3.utils.TrackQueue;
import org.serje3.utils.VoiceHelper;
import org.serje3.utils.exceptions.NoTracksInQueueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class QueueCommand extends PlayCommand {
    private final YoutubeRestHandler youtubeRestHandler = new YoutubeRestHandler();
    private final Logger logger = LoggerFactory.getLogger(QueueCommand.class);

    @Override
    @JoinVoiceChannel
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        event.deferReply().queue();
        final Guild guild = event.getGuild();
        String subcommandName = event.getSubcommandName();
        String identifier = event.getOption("текст").getAsString();
        if (identifier.length() > 100 && !identifier.startsWith("https://")){
            identifier = identifier.substring(0, 100);
        }

        if (identifier.strip().equals("cock\" exit(-1)")){
            event.getHook().sendMessage("Димас нет не получится").queue();
            return;
        }

        final long guildId = guild.getIdLong();
        PlaySourceType type = PlaySourceType.valueOf(subcommandName.toUpperCase());
        String prefix = musicService.getSearchPrefix(subcommandName, identifier);
        if (type == PlaySourceType.YOUTUBE && !identifier.startsWith("https://")) {
            String url = trySearchCachedUrl(prefix, identifier);
            this.play(client, event, guildId, url);
            return;
        }
        this.play(client, event, guildId, prefix + identifier);
    }

    private String trySearchCachedUrl(String prefix, String identifier) {
        Tracks tracks;
        try {
            tracks = youtubeRestHandler.searchCached(identifier);
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e);
            return prefix + identifier;
        }
        if (tracks.getItems() == null || tracks.getItems().isEmpty()) {
            return prefix + identifier;
        }

        Tracks.CachedTrack track = tracks.getItems().get(0);

        return track.getYoutubeURL();
    }

    @Override
    public void play(LavalinkClient client, SlashCommandInteractionEvent event,
                     Long guildId, String identifier, Integer volume) {
        System.out.println("IDENTIFIER:" + identifier);

        if (identifier == null){
            event.getHook().sendMessage("Параметр 'Текст' не должен быть нулевым").queue();
            return;
        }
        logger.info("Now {} nodes active", client.getNodes().size());
        client.getLink(guildId).loadItem(identifier)
                .subscribe((item) -> {
                    System.out.println(item);
                    if (item instanceof TrackLoaded trackLoaded) {
                        final Track track = trackLoaded.getTrack();

                        musicService.queue(track, guildId, event.getMember(), event.getChannel().asTextChannel(), client);

                        event.getHook().sendMessageEmbeds(VoiceHelper.wrapTrackEmbed(track, event.getMember(), "Добавлен в очередь"))
                                .addActionRow(new AddToQueueButton().asJDAButton())
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
                        musicService.queue(tracks, guildId, event.getMember(), event.getChannel().asTextChannel(), client);

                        final int trackCount = tracks.size();
                        event.getHook().sendMessage("Этот плейлист имеет " + trackCount + " треков! Запускаю - " + tracks.get(0).getInfo().getTitle())
                                .queue();
                    } else if (item instanceof SearchResult searchResult) {
                        final List<Track> tracks = searchResult.getTracks();

                        if (tracks.isEmpty()) {
                            event.getHook().sendMessage("Ни одного трека не найдено!").queue();
                            return;
                        }

                        final Track firstTrack = tracks.get(0);

                        musicService.queue(firstTrack, guildId, event.getMember(), event.getChannel().asTextChannel(), client);

                        event.getHook().sendMessageEmbeds(VoiceHelper.wrapTrackEmbed(firstTrack, event.getMember(), "Добавлен в очередь"))
                                .addActionRow(new AddToQueueButton().asJDAButton())
                                .queue();
                    } else if (item instanceof NoMatches) {
                        event.getHook().sendMessage("Ничего не найдено по вашему запросу!").queue();
                    } else if (item instanceof LoadFailed fail) {
                        event.getHook().sendMessage("ЕБАТЬ НЕ ПОЛУЧИЛОСЬ ЗАГРУЗИТЬ АУДИО! Ашибка: " + fail.getException().getMessage()).queue();
                    }
                });
    }
}
