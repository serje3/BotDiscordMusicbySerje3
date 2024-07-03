package org.serje3.components.commands.suno.handlers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.meta.interfaces.CommandExecutable;
import org.serje3.rest.handlers.SunoRestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Credits implements CommandExecutable {
    private final SunoRestHandler sunoRestHandler = new SunoRestHandler();
    private final Logger logger = LoggerFactory.getLogger(Credits.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().setEphemeral(true).queue();
        sunoRestHandler.credits(event.getUser().getIdLong())
                .thenAccept((credits) -> {
                    logger.info("Credits: {}, Limit: {}, Period: {}, Usage: {}", credits.getCreditsLeft(), credits.getMonthlyLimit(), credits.getPeriod(), credits.getMonthlyUsage());
                    event.getHook().sendMessageEmbeds(new EmbedBuilder()
                                    .setAuthor(event.getUser().getEffectiveName(), event.getUser().getEffectiveAvatarUrl(), event.getUser().getEffectiveAvatarUrl())
                                    .setTitle("Кредиты Suno.com")
                                    .addField("Кредитов осталось", credits.getCreditsLeft().toString(), false)
                                    .addField("Месячный лимит", credits.getMonthlyLimit().toString(),  false)
                                    .addField("Период", credits.getPeriod() != null ? credits.getPeriod().toString() : "Пусто", false)
                                    .addField("Monthly Usage", credits.getMonthlyUsage().toString(), false)
                            .build()).queue();
                })
                .exceptionally((e) -> {
                    event.getHook().sendMessage("Пиздец баля: "+ e.getLocalizedMessage()).queue();
                    return null;
                });
    }
}
