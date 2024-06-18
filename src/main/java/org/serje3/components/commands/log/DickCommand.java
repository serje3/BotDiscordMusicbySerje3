package org.serje3.components.commands.log;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.serje3.meta.abs.Command;
import org.serje3.rest.handlers.DickRestHandler;
import org.serje3.rest.responses.DickResponse;

public class DickCommand extends Command {
    private final DickRestHandler dickRestHandler = new DickRestHandler();
    private final String option = "слово";

    @Override
    public String getName() {
        return "dick";
    }

    @Override
    public String getDescription() {
        return "Превращает ваше слово в хуй (русские only)";
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return super.getSlashCommand().addOption(
                OptionType.STRING,
                option,
                getDescription(),
                true
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        DickResponse dickResponse = dickRestHandler.generateDickName(event.getOption(option).getAsString());

        event.getHook().sendMessage(dickResponse.getDickName()).queue();
    }
}
