package org.serje3.components.autocomplete.music;

import com.google.gson.Gson;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.protocol.LoadFailed;
import dev.arbjerg.lavalink.client.protocol.SearchResult;
import dev.arbjerg.lavalink.client.protocol.Track;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.serje3.domain.TrackContext;
import org.serje3.meta.abs.AutoComplete;
import org.serje3.meta.enums.PlaySourceType;
import org.serje3.rest.domain.Tracks;
import org.serje3.rest.handlers.YoutubeRestHandler;
import org.serje3.services.MusicService;
import org.serje3.utils.SlashEventHelper;
import org.serje3.utils.TrackQueue;
import org.serje3.utils.VoiceHelper;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchAutocomplete extends AutoComplete {
    private final MusicService musicService = new MusicService();
    private final YoutubeRestHandler youtubeRestHandler = new YoutubeRestHandler();

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public void handle(CommandAutoCompleteInteractionEvent event, LavalinkClient client) {
        System.out.println(event);
        System.out.println(event.getSubcommandName());
        System.out.println(event.getFocusedOption().getValue());
        String identifier = event.getFocusedOption().getValue();
        String subCommand = event.getSubcommandName();
        PlaySourceType playSourceType = PlaySourceType.valueOf(subCommand.toUpperCase());
        if (client == null || identifier.isEmpty() || identifier.startsWith("https://") || playSourceType.equals(PlaySourceType.TEXT_TO_SPEECH)) {
            event.replyChoices(Collections.emptyList()).queue();
            return;
        }
        if (playSourceType.equals(PlaySourceType.YOUTUBE)){
            try {
                System.out.println("CACHED YOUTUBE SEARCH");
                cachedYoutubeAutocomplete(identifier, event);
                return;
            } catch (Exception e){
                // then pass & try default search youtube method
                System.out.println(e.getMessage());
            }
        }
        System.out.println("WTF???????");
        String searchPrefix = musicService.getSearchPrefix(subCommand, identifier);
        client.getLink(event.getGuild().getIdLong())
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
                                System.out.println(options);
                                event.replyChoices(options).queue();
                            } else if (item instanceof LoadFailed loadFailed) {
                                event.replyChoices(Collections.emptyList()).queue();
                            }
                        }
                );

    }

    private void cachedYoutubeAutocomplete(String identifier, CommandAutoCompleteInteractionEvent event) {
        System.out.println("WTF NEXT IS YOUTUBE RESPONSE");
        Tracks tracks;
        try{
            tracks = youtubeRestHandler.searchCached(identifier);
        } catch (ExecutionException | InterruptedException e){
            System.out.println(e.getMessage());
            emptyChoices(event);
            return;
        }
        System.out.println("NEXT IS YOUTUBE RESPONSE");
        System.out.println(tracks + " " + tracks.getItems());
        if (tracks.getItems().isEmpty()) {
            emptyChoices(event);
            return;
        }

        List<Command.Choice> options = tracks.getItems().stream()
                .map(track -> {
                    String url = track.getYoutubeURL();
                    String title = track.title();
                    return new Command.Choice(title, url != null ? url : title);
                }).toList().subList(0, 25);
        System.out.println(options);
        event.replyChoices(options).queue((s) -> {
            System.out.println("success");
        }, (e) -> {
            System.out.println("Error");
        });
    }

    private void emptyChoices(CommandAutoCompleteInteractionEvent event){
        event.replyChoices(Collections.emptyList()).queue();
    }
}
