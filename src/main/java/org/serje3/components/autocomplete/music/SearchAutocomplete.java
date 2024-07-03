package org.serje3.components.autocomplete.music;

import com.google.gson.Gson;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.player.LoadFailed;
import dev.arbjerg.lavalink.client.player.SearchResult;
import dev.arbjerg.lavalink.client.player.Track;
import io.sentry.Sentry;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.serje3.meta.abs.AutoComplete;
import org.serje3.meta.enums.PlaySourceType;
import org.serje3.rest.domain.Tracks;
import org.serje3.rest.handlers.YoutubeRestHandler;
import org.serje3.services.LavalinkService;
import org.serje3.services.MusicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchAutocomplete extends AutoComplete {
    private final MusicService musicService = new MusicService();
    private final YoutubeRestHandler youtubeRestHandler = new YoutubeRestHandler();
    private final Logger logger = LoggerFactory.getLogger(SearchAutocomplete.class);

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public void handle(CommandAutoCompleteInteractionEvent event) {
        logger.info(event.toString());
        logger.info(event.getSubcommandName());
        logger.info(event.getFocusedOption().getValue());
        String identifier = event.getFocusedOption().getValue();
        if (identifier.length() > 100) {
            identifier = identifier.substring(0, 100);
        }
        String subCommand = event.getSubcommandName();
        PlaySourceType playSourceType = PlaySourceType.valueOf(subCommand.toUpperCase());
        if (identifier.isEmpty() || identifier.startsWith("https://") || playSourceType.equals(PlaySourceType.TEXT_TO_SPEECH)) {
            event.replyChoices(Collections.emptyList()).queue();
            return;
        }
        if (playSourceType.equals(PlaySourceType.YOUTUBE)) {
            try {
                logger.info("CACHED YOUTUBE SEARCH");
                cachedYoutubeAutocomplete(identifier, event);
                return;
            } catch (Exception e) {
                // then pass & try default search youtube method
                logger.error(e.getMessage());
            }
        }

        String searchPrefix = musicService.getSearchPrefix(subCommand, identifier);
        LavalinkService.getInstance().getLink(event.getGuild().getIdLong())
                .loadItem(searchPrefix + identifier)
                .subscribe(item -> {
                            if (item instanceof SearchResult searchResult) {
                                final List<Track> tracks = searchResult.getTracks();

                                if (tracks.isEmpty()) {
                                    emptyChoices(event);
                                    return;
                                }

                                List<Command.Choice> options = tracks.stream()
                                        .map(track -> {
                                            String url = track.getInfo().getUri();
                                            String title = PlaySourceType.YOUTUBE.equals(playSourceType) ? track.getInfo().getTitle() : track.getInfo().getTitle() + " - " + track.getInfo().getAuthor();
                                            if (title.length() > 100) title = title.substring(0, 100);
                                            return new Command.Choice(title, url != null ? url : title);
                                        }).toList();
                                logger.info(options.toString());
                                event.replyChoices(options).queue();
                            } else if (item instanceof LoadFailed loadFailed) {
                                logger.error("{} {}", loadFailed.getException().getMessage(), loadFailed.getException().getCause());
                                event.replyChoices(Collections.emptyList()).queue();
                            }
                        }
                );

    }

    private void  cachedYoutubeAutocomplete(String identifier, CommandAutoCompleteInteractionEvent event) {
        Tracks tracks;
        try {
            tracks = youtubeRestHandler.searchCached(identifier);
        } catch (ExecutionException | InterruptedException e) {
            logger.info(e.getMessage());
            emptyChoices(event);
            return;
        }

        if (tracks.getItems() == null || tracks.getItems().isEmpty()) {
            emptyChoices(event);
            return;
        }

        List<Command.Choice> options = tracks.getItems().stream()
                .map(track -> {
                    String url = track.getYoutubeURL();
                    String title = track.title();
                    return new Command.Choice(title.length() > 99 ? title.substring(0, 99) : title, url != null ? url : title);
                }).toList().subList(0, 25);

        event.replyChoices(options).queue((s) -> {
            logger.info("success");
        }, (e) -> {
            logger.info("Error {}", e.getMessage());
            Sentry.captureException(e);
        });
    }

    private void emptyChoices(CommandAutoCompleteInteractionEvent event) {
        event.replyChoices(Collections.emptyList()).queue();
    }
}
