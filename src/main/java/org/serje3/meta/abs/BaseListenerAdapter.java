package org.serje3.meta.abs;

import dev.arbjerg.lavalink.client.LavalinkClient;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.serje3.meta.interfaces.ContainSlashCommands;
import org.serje3.rest.handlers.EventRestHandler;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public abstract class BaseListenerAdapter extends ListenerAdapter implements ContainSlashCommands {
    protected final EventRestHandler eventRestHandler = new EventRestHandler();
    protected final HashMap<String, Command> commands = new HashMap<>();
    protected final HashMap<String, Button> buttons = new HashMap<>();
    protected final HashMap<String, AutoComplete> autoComplete = new HashMap<>();
    protected LavalinkClient client;

    public BaseListenerAdapter() {
        registerCommands();
        registerButtons();
        registerAutoComplete();
    }

    @Override
    public void onReady(@NotNull net.dv8tion.jda.api.events.session.ReadyEvent event) {
        System.out.println(getLogPrefix() + event.getJDA().getSelfUser().getEffectiveName() + " is ready!");
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        System.out.println(getLogPrefix() + "Command Name: " + event.getName());

        String commandName = event.getName();
        Command command = this.commands.get(commandName);
        if (command != null) {
            eventRestHandler.handleSlashEvent(event);
            command.execute(event, client);
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        System.out.println(getLogPrefix() + "Button interaction: " + event.getComponentId());
        Button button = this.buttons.get(event.getComponentId());
        if (button != null) {
            button.handle(event, client);
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        AutoComplete complete = this.autoComplete.get(event.getName());
        if (complete != null) {
            complete.handle(event, client);
        }
    }

    @Override
    public List<SlashCommandData> getSlashCommands() {
        return this.commands.values().stream().map(Command::getSlashCommand).collect(Collectors.toList());
    }

    protected abstract AdapterContext getAdapterContext();

    protected Command convertCommand(Command command){
        return command;
    }

    protected Button convertButton(Button button){
        return button;
    }

    protected AutoComplete convertAutoComplete(AutoComplete autoComplete){
        return autoComplete;
    }

    private void registerCommands() {
        getAdapterContext().forEachCommand(command -> this.commands.put(command.getName(), convertCommand(command)));
    }

    private void registerButtons() {
        getAdapterContext().forEachButton(button -> this.buttons.put(button.getComponentId(), convertButton(button)));
    }

    private void registerAutoComplete(){
        getAdapterContext().forEachAutoComplete(autoComplete -> this.autoComplete.put(autoComplete.getName(), convertAutoComplete(autoComplete)));
    }

    private String getLogPrefix(){
        return "[" + this.getClass().getName() + "] ";
    }
}
