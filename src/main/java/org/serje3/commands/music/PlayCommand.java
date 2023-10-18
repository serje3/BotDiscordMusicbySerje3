package org.serje3.commands.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.protocol.v4.LoadResult;
import dev.arbjerg.lavalink.protocol.v4.Track;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.meta.abs.Command;
import org.serje3.utils.VoiceHelper;

import java.util.List;

public class PlayCommand extends Command {
    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        final Guild guild = event.getGuild();

        // We are already connected, go ahead and play
        if (guild.getSelfMember().getVoiceState().inAudioChannel()) {
            event.deferReply(false).queue();
        } else {
            // Connect to VC first
            VoiceHelper.joinHelper(event);
        }

        final String identifier = event.getOption("identifier").getAsString();
        final long guildId = guild.getIdLong();
        this.play(client, event, guildId, identifier);
    }


    public void play(LavalinkClient client, SlashCommandInteractionEvent event, Long guildId, String identifier) {
        final Link link = client.getLink(guildId);
        link.loadItem(identifier).subscribe((item) -> {
            if (item instanceof LoadResult.TrackLoaded trackLoaded) {
                final Track track = trackLoaded.getData();

                link.createOrUpdatePlayer()
                        .setEncodedTrack(track.getEncoded())
                        .setVolume(35)
                        .asMono()
                        .subscribe((ignored) -> {
                            event.getHook().sendMessage("Now playing: " + track.getInfo().getTitle()).queue();
                        });
            } else if (item instanceof LoadResult.PlaylistLoaded playlistLoaded) {
                final int trackCount = playlistLoaded.getData().getTracks().size();
                event.getHook()
                        .sendMessage("This playlist has " + trackCount + " tracks!")
                        .queue();
            } else if (item instanceof LoadResult.SearchResult searchResult) {
                final List<Track> tracks = searchResult.getData().getTracks();

                if (tracks.isEmpty()) {
                    event.getHook().sendMessage("No tracks found!").queue();
                    return;
                }

                final Track firstTrack = tracks.get(0);

                // This is a different way of updating the player! Choose your preference!
                // This method will also create a player if there is not one in the server yet
                link.updatePlayer((update) -> update.setEncodedTrack(firstTrack.getEncoded()).setVolume(35))
                        .subscribe((ignored) -> {
                            event.getHook().sendMessage("Now playing: " + firstTrack.getInfo().getTitle()).queue();
                        });

            } else if (item instanceof LoadResult.NoMatches) {
                event.getHook().sendMessage("No matches found for your input!").queue();
            } else if (item instanceof LoadResult.LoadFailed fail) {
                event.getHook().sendMessage("Failed to load track! " + fail.getData().getMessage()).queue();
            }
        });
    }
}
