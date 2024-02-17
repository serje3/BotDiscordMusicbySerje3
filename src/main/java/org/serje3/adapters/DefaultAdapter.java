package org.serje3.adapters;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.serje3.meta.abs.Command;
import org.serje3.meta.interfaces.ContainSlashCommands;
import org.serje3.rest.handlers.EventRestHandler;
import org.serje3.utils.commands.DefaultCommandList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultAdapter extends ListenerAdapter implements ContainSlashCommands {
    private final HashMap<String, Command> commands = new HashMap<>();

    private final EventRestHandler eventRestHandler;

    public DefaultAdapter() {
        eventRestHandler = new EventRestHandler();
        new DefaultCommandList().forEach(command -> {
            System.out.println(command);
            this.commands.put(command.getName(), command);
            return null;
        });

    }

    @Override
    public void onReady(ReadyEvent event) {
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        System.out.println("Command Name: " + event.getName());
        String commandName = event.getName();

        Command command = this.commands.get(commandName);
        if (command != null) {
            command.execute(event, null);
        } else {
            // скорее всего ответ должен придти в другом listener
            return;
        }
        eventRestHandler.handleSlashEvent(event);
    }

    @Override
    public List<SlashCommandData> getSlashCommands() {
        return this.commands.values().stream().map(Command::getSlashCommand).collect(Collectors.toList());
    }
}
