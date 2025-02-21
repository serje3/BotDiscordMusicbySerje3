package org.serje3.components.commands.music.filters;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.serje3.components.commands.music.filters.settings.TimescaleFilter;
import org.serje3.meta.abs.Command;
import org.serje3.meta.interfaces.CommandExecutable;

public class FilterCommand extends Command {
    @Override
    public String getName() {
        return "filter";
    }

    @Override
    public String getDescription() {
        return "Utility to access player filters";
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return super.getSlashCommand()
                .addSubcommands(
                        new SubcommandData(FilterHandler.TIMESCALE.name().toLowerCase(), "Changes the speed, pitch, and rate. All default to 1.0.")
                                .addOption(OptionType.NUMBER, "speed", "The playback speed 0.0 ≤ x", false)
                                .addOption(OptionType.NUMBER, "pitch", "The pitch 0.0 ≤ x", false)
                                .addOption(OptionType.NUMBER, "rate", "The rate 0.0 ≤ x", false)
                );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try{
            FilterHandler handler = FilterHandler.valueOf(event.getSubcommandName().toUpperCase());
            handler.execute(event);
        } catch (IllegalArgumentException e) {
            if (!event.isAcknowledged()){
                event.reply("Something went wrong with `" + event.getSubcommandName() + "`").queue();
            } else {
                event.getHook().sendMessage("Все пошло коту под кал").queue();
            }
        }
    }


    enum FilterHandler {
        TIMESCALE(TimescaleFilter.class);

        private final CommandExecutable handler;

        @SneakyThrows
        FilterHandler(Class<? extends CommandExecutable> handler) {
            this.handler = handler.getDeclaredConstructor().newInstance();
        }

        public void execute(SlashCommandInteractionEvent event) {
            this.handler.execute(event);
        }
    }
}
