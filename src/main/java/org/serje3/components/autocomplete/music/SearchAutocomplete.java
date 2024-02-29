package org.serje3.components.autocomplete.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.protocol.LoadFailed;
import dev.arbjerg.lavalink.client.protocol.SearchResult;
import dev.arbjerg.lavalink.client.protocol.Track;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.serje3.domain.TrackContext;
import org.serje3.meta.abs.AutoComplete;
import org.serje3.meta.enums.PlaySourceType;
import org.serje3.services.MusicService;
import org.serje3.utils.SlashEventHelper;
import org.serje3.utils.TrackQueue;
import org.serje3.utils.VoiceHelper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchAutocomplete extends AutoComplete {
    private final MusicService musicService = new MusicService();

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
        String searchPrefix = musicService.getSearchPrefix(subCommand, identifier);
        if (client == null || identifier.isEmpty() || identifier.startsWith("https://")) {
            event.replyChoices(Collections.emptyList()).queue();
            return;
        }
        client.getLink(event.getGuild().getIdLong())
                .loadItem(searchPrefix + identifier)
                .subscribe(item -> {
                            if (item instanceof SearchResult searchResult) {
                                final List<Track> tracks = searchResult.getTracks();

                                if (tracks.isEmpty()) {
                                    event.replyChoices(Collections.emptyList()).queue();
                                    return;
                                }

                                List<Command.Choice> options = tracks.stream()
                                        .map(track -> {
                                            String url = track.getInfo().getUri();
                                            String title = PlaySourceType.YOUTUBE.equals(playSourceType) ? track.getInfo().getTitle() : track.getInfo().getTitle() + " - " + track.getInfo().getAuthor();
                                            if (title.length() > 100) title = title.substring(0, 100);
                                            return new Command.Choice(title, url != null ? url : title);
                                        }).toList();
                                event.replyChoices(options).queue();
                            } else if (item instanceof LoadFailed loadFailed) {
                                event.replyChoices(Collections.emptyList()).queue();
                            }
                        }
                );
//        List<Command.Choice> options = Stream.of(words)
//                .filter(word -> word.startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
//                .map(word -> new Command.Choice(word, word)) // map the words to choices
//                .collect(Collectors.toList());
//        event.replyChoices(options).queue();
    }
}
