package org.serje3.commands.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.protocol.v4.LoadResult;
import dev.arbjerg.lavalink.protocol.v4.Track;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.meta.abs.Command;
import org.serje3.meta.enums.PlaySourceType;
import org.serje3.meta.annotations.JoinVoiceChannel;

import java.util.List;

public class PlayCommand extends Command {
    @Override
    @JoinVoiceChannel
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        final Guild guild = event.getGuild();

        final PlaySourceType playType = PlaySourceType.valueOf(event.getSubcommandName());
        String prefix = switch (playType) {
            case YOUTUBE -> "ytsearch:";
            case SOUNDCLOUD -> "scsearch:";
            case YANDEX_MUSIC -> "ymsearch:";
            default -> "";
        };
        System.out.println(prefix);
        final String identifier = event.getOption("текст").getAsString();
        if (identifier.startsWith("https://")) {
            prefix = "";
        }
        final long guildId = guild.getIdLong();
        this.play(client, event, guildId, prefix + identifier);
    }


    public void play(LavalinkClient client, SlashCommandInteractionEvent event, Long guildId, String identifier) {
        final Link link = client.getLink(guildId);
        link.loadItem(identifier).subscribe((item) -> {
            System.out.println(item);
            if (item instanceof LoadResult.TrackLoaded trackLoaded) {
                final Track track = trackLoaded.getData();

                link.createOrUpdatePlayer()
                        .setEncodedTrack(track.getEncoded())
                        .setVolume(35)
                        .asMono()
                        .subscribe((ignored) -> {
                            event.getHook().sendMessage("Сейчас играет: " + track.getInfo().getTitle()).queue();
                        });
            } else if (item instanceof LoadResult.PlaylistLoaded playlistLoaded) {
                final int trackCount = playlistLoaded.getData().getTracks().size();
                event.getHook()
                        .sendMessage("Этот плейлист имеет " + trackCount + " треков!")
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
                link.updatePlayer((update) -> update.setEncodedTrack(firstTrack.getEncoded()).setVolume(35))
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
