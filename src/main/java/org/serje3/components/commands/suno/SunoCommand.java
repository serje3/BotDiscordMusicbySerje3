package org.serje3.components.commands.suno;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.serje3.components.commands.suno.handlers.*;
import org.serje3.meta.abs.Command;
import org.serje3.meta.interfaces.CommandExecutable;

public class SunoCommand extends Command {
    @Override
    public String getName() {
        return "suno";
    }

    @Override
    public String getDescription() {
        return "Utility for song AI generation";
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return super.getSlashCommand()
                .addSubcommands(
                        new SubcommandData(SunoHandler.GENERATE.name().toLowerCase(), "Generates music by AI"),
                        new SubcommandData(SunoHandler.LOGIN.name().toLowerCase(), "[REQUIRED] Login to suno account"),
                        new SubcommandData(SunoHandler.FEED.name().toLowerCase(), "List feed")
                                .addOption(OptionType.INTEGER, "page", "Page of feed. Starts with 0", false),
                        new SubcommandData(SunoHandler.PLAY.name().toLowerCase(), "Play track of feed")
                                .addOption(OptionType.INTEGER, "index", "Feed index", true),
                        new SubcommandData(SunoHandler.CREDITS.name().toLowerCase(), "Credits left")
                );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try{
            SunoHandler handler = SunoHandler.valueOf(event.getSubcommandName().toUpperCase());
            handler.execute(event);
        } catch (IllegalArgumentException e) {
            event.reply("Invalid subcommand `" + event.getSubcommandName() + "`").queue();
        }
    }

    enum SunoHandler {
        LOGIN(Login.class),
        GENERATE(Generate.class),
        FEED(Feed.class),
        PLAY(Play.class),
        CREDITS(Credits.class);

        private final CommandExecutable handler;

        @SneakyThrows
        SunoHandler(Class<? extends CommandExecutable> handler) {
            this.handler = handler.getDeclaredConstructor().newInstance();
        }

        public void execute(SlashCommandInteractionEvent event) {
            this.handler.execute(event);
        }
    }
}
