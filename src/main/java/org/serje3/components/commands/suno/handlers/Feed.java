package org.serje3.components.commands.suno.handlers;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.meta.interfaces.CommandExecutable;
import org.serje3.rest.domain.SunoClip;
import org.serje3.rest.handlers.SunoRestHandler;

public class Feed implements CommandExecutable {

    private final SunoRestHandler sunoRestHandler = new SunoRestHandler();

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        sunoRestHandler.feed(event.getUser().getIdLong()).thenAccept((clips) -> {
            StringBuilder builder = new StringBuilder();
            builder.append("<@!").append(event.getUser().getIdLong()).append("> \n");
            for (int i = 0; i < clips.size(); i++) {
                SunoClip clip = clips.get(i);
                builder.append(i).append(". ");
                builder.append(clip.getTitle()).append(" - ").append(clip.getStatus()).append("\n");
            }

            event.getHook().sendMessage(builder.toString()).queue();
        }).exceptionally((e) -> {
            event.getHook().sendMessage("Пиздец баля: " + e.getLocalizedMessage()).queue();
            return null;
        });
    }
}
