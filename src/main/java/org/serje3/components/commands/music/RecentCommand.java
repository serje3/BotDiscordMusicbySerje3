package org.serje3.components.commands.music;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.serje3.meta.abs.Command;
import org.serje3.rest.domain.RecentTrack;
import org.serje3.rest.handlers.MusicRestHandler;

import java.util.stream.Collectors;

public class RecentCommand extends Command {
    private final MusicRestHandler musicRestHandler = new MusicRestHandler();


    @Override
    public String getName() {
        return "recent";
    }


    @Override
    public String getDescription() {
        return "Показывает 5 последних воспроизведенных треков";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        musicRestHandler.getRecentTracks(event.getGuild().getIdLong())
                .thenAccept(tracks -> {
                    event.getHook().sendMessage(tracks.stream().map(RecentTrack::getName).collect(Collectors.joining("\n"))).queue();
                }).exceptionally(e -> {
                    e.printStackTrace();
                    event.getHook().sendMessage(e.getMessage()).queue();
                    return null;
                });
    }
}
