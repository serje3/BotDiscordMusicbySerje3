package org.serje3.components.commands.music.queue;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.LinkState;
import dev.arbjerg.lavalink.client.player.*;
import io.sentry.Sentry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.serje3.components.buttons.music.AddToQueueButton;
import org.serje3.components.commands.music.PlayCommand;
import org.serje3.meta.abs.Command;
import org.serje3.meta.annotations.JoinVoiceChannel;
import org.serje3.meta.enums.PlaySourceType;
import org.serje3.rest.domain.Tracks;
import org.serje3.rest.handlers.YoutubeRestHandler;
import org.serje3.services.EmbedService;
import org.serje3.services.LavalinkService;
import org.serje3.services.MusicService;
import org.serje3.utils.VoiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class QueueCommand extends Command {
    private final MusicService musicService = new MusicService();
    private final YoutubeRestHandler youtubeRestHandler = new YoutubeRestHandler();
    private final Logger logger = LoggerFactory.getLogger(QueueCommand.class);

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Включает музыку. Доступно для выбора несколько источников воспроизведения";
    }

    private SubcommandData addTextField(SubcommandData obj){
        return obj
                .addOption(
                        OptionType.STRING,
                        "текст",
                        obj.getDescription(),
                        true,
                        true
                );
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return super.getSlashCommand()
                .addSubcommands(
                        addTextField(new SubcommandData(PlaySourceType.YOUTUBE.name().toLowerCase(), "Поиск из ютуба")),
                        addTextField(new SubcommandData(PlaySourceType.SOUNDCLOUD.name().toLowerCase(), "Поиск из soundclound")),
                        addTextField(new SubcommandData(PlaySourceType.YANDEXMUSIC.name().toLowerCase(), "Поиск из Yandex Music")),
                        addTextField(new SubcommandData(PlaySourceType.SPOTIFY.name().toLowerCase(), "Поиск из spotify")),
                        addTextField(new SubcommandData(PlaySourceType.YOUTUBEMUSIC.name().toLowerCase(), "Поиск из Youtube Music")),
                        addTextField(new SubcommandData(PlaySourceType.RECENT.name().toLowerCase(), "Последние воспроизведенные треки")),
                        new SubcommandData(PlaySourceType.LOCAL.name().toLowerCase(), "Проигрывание из локальных файлов")
                                .addOption(OptionType.ATTACHMENT, "файл", "wdasdksdaasdjasdjkl")
                );
    }

    @Override
    @JoinVoiceChannel
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();


        final Guild guild = event.getGuild();
        String subcommandName = event.getSubcommandName();
        String identifier = event.getOption("текст").getAsString();
        if (identifier.length() > 100 && !identifier.startsWith("https://")) {
            identifier = identifier.substring(0, 100);
        }

        if (identifier.strip().equals("cock\" exit(-1)")) {
            event.getHook().sendMessage("Димас нет не получится").queue();
            return;
        }

        final long guildId = guild.getIdLong();
        PlaySourceType type = PlaySourceType.valueOf(subcommandName.toUpperCase());
        String prefix = musicService.getSearchPrefix(subcommandName, identifier);
        if (type == PlaySourceType.YOUTUBE && !identifier.startsWith("https://")) {
            String url = trySearchCachedUrl(prefix, identifier);
            this.play(event, guildId, url);
            return;
        }
        this.play(event, guildId, prefix + identifier);
    }

    private String trySearchCachedUrl(String prefix, String identifier) {
        Tracks tracks;
        try {
            tracks = youtubeRestHandler.searchCached(identifier);
        } catch (InterruptedException | ExecutionException e) {
            Sentry.captureException(e);
            return prefix + identifier;
        }
        if (tracks.getItems() == null || tracks.getItems().isEmpty()) {
            return prefix + identifier;
        }

        Tracks.CachedTrack track = tracks.getItems().get(0);

        return track.getYoutubeURL();
    }

    public void play(SlashCommandInteractionEvent event,
                     Long guildId, String identifier) {
        this.play(event, guildId, identifier, 35, null);
    }

    public void play(SlashCommandInteractionEvent event,
                     Long guildId, String identifier, Integer volume, Consumer<List<Track>> onSuccess) {
        System.out.println("IDENTIFIER:" + identifier);

        if (identifier == null) {
            event.getHook().sendMessage("Параметр 'Текст' не должен быть нулевым").queue();
            return;
        }

        LavalinkService service = LavalinkService.getInstance();
        logger.info("Now {} nodes active", service.getClient().getNodes().size());
        service.getLink(guildId).loadItem(identifier)
                .subscribe((item) -> {
                    System.out.println(item);
                    if (item instanceof TrackLoaded trackLoaded) {
                        Track track = trackLoaded.getTrack();
                        track = musicService.cockinizeTrackIfNowIsTheTime(guildId, track);

                        musicService.queue(track, guildId, event.getMember(), event.getChannel().asTextChannel());

                        if (onSuccess != null){
                            onSuccess.accept(List.of(track));
                            return;
                        }
                        event.getHook().sendMessageEmbeds(EmbedService.getInstance().wrapTrackEmbed(track, event.getMember(), "Добавлен в очередь"))
                                .addActionRow(new AddToQueueButton().asJDAButton())
                                .queue();
                    } else if (item instanceof PlaylistLoaded playlistLoaded) {
                        List<Track> tracks = playlistLoaded.getTracks();
                        if (tracks.isEmpty()) {
                            event.getHook().sendMessage("В этом плейлисте нет треков").queue();
                            return;
                        } else if (tracks.size() >= 10000) {
                            event.getHook().sendMessage("Этот плейлист слишком большой").queue();
                            return;
                        }


                        tracks = musicService.cockinizeTracksIfNowIsTheTime(guildId, tracks);

                        musicService.queue(tracks, guildId, event.getMember(), event.getChannel().asTextChannel());

                        if (onSuccess != null){
                            onSuccess.accept(tracks);
                            return;
                        }
                        final int trackCount = tracks.size();
                        event.getHook().sendMessage("Этот плейлист имеет " + trackCount + " треков! Запускаю - " + tracks.get(0).getInfo().getTitle())
                                .queue();
                    } else if (item instanceof SearchResult searchResult) {
                        final List<Track> tracks = searchResult.getTracks();

                        if (tracks.isEmpty()) {
                            event.getHook().sendMessage("Ни одного трека не найдено!").queue();
                            return;
                        }

                        final Track firstTrack = musicService.cockinizeTrackIfNowIsTheTime(guildId, tracks.get(0));

                        musicService.queue(firstTrack, guildId, event.getMember(), event.getChannel().asTextChannel());

                        event.getHook().sendMessageEmbeds(EmbedService.getInstance().wrapTrackEmbed(firstTrack, event.getMember(), "Добавлен в очередь"))
                                .addActionRow(new AddToQueueButton().asJDAButton())
                                .queue();

                    } else if (item instanceof NoMatches) {
                        event.getHook().sendMessage("Ничего не найдено по вашему запросу!").queue();
                    } else if (item instanceof LoadFailed fail) {
                        event.getHook().sendMessage("ЕБАТЬ НЕ ПОЛУЧИЛОСЬ ЗАГРУЗИТЬ АУДИО! Ашибка: " + fail.getException().getMessage()).queue();
                    }
                }, Sentry::captureException);
    }
}
