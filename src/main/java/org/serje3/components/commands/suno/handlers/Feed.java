package org.serje3.components.commands.suno.handlers;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.serje3.meta.interfaces.CommandExecutable;
import org.serje3.rest.handlers.SunoRestHandler;
import org.serje3.services.SunoService;

import java.util.concurrent.atomic.AtomicInteger;

public class Feed implements CommandExecutable {

    private final SunoRestHandler sunoRestHandler = new SunoRestHandler();
    private final SunoService sunoService = new SunoService();

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        OptionMapping pageOption = event.getOption("page");
        int page;
        try{
            page = sunoService.handlePageOptionMapping(pageOption);
        } catch (IllegalArgumentException e) {
            event.getHook().sendMessage(e.getLocalizedMessage()).queue();
            return;
        }

        sunoRestHandler.feed(event.getUser().getIdLong(), page).thenAccept((clips) -> {
            StringBuilder builder = new StringBuilder();
            builder.append("<@!").append(event.getUser().getIdLong()).append("> \n");
            AtomicInteger counter = new AtomicInteger(page * 20);
            clips.forEach(clip -> {
                builder.append(counter.get()).append(": ");
                builder.append(clip.getTitle()).append(" - ").append(clip.getStatus()).append("\n");
                counter.getAndIncrement();
            });

            event.getHook().sendMessage(builder.toString()).queue();
        }).exceptionally((e) -> {
            event.getHook().sendMessage("Пиздец баля: " + e.getLocalizedMessage()).queue();
            return null;
        });
    }
}
