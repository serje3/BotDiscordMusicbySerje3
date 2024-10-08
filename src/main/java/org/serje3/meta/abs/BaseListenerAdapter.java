package org.serje3.meta.abs;

import dev.arbjerg.lavalink.client.LavalinkClient;
import io.sentry.Hint;
import io.sentry.Sentry;
import io.sentry.SentryEvent;
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
import org.serje3.services.LavalinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private Logger logger;

    public BaseListenerAdapter() {
        logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
        logger.info("register commands {}", getAdapterContext().getCommands().size());
        registerCommands();
        logger.info("register buttons {}", getAdapterContext().getButtons().size());
        registerButtons();
        logger.info("register autocompletes {}", getAdapterContext().getAutoCompletes().size());
        registerAutoComplete();
    }

    @Override
    public void onReady(@NotNull net.dv8tion.jda.api.events.session.ReadyEvent event) {
        System.out.println(getLogPrefix() + event.getJDA().getSelfUser().getEffectiveName() + " is ready!");
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        String commandName = event.getName();
        Command command = this.commands.get(commandName);
        if (command != null) {
            System.out.println(getLogPrefix() + "Command Name: " + event.getName());
            eventRestHandler.handleSlashEvent(event);
            try {
                command.execute(event);
            } catch (Exception e) {
                Sentry.captureException(e);
                throw e;
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Button button = this.buttons.get(event.getComponentId());
        if (button != null) {
            try {
                button.handle(event);
            } catch (Exception ex) {
                Sentry.captureException(ex);
                throw ex;
            }
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        AutoComplete complete = this.autoComplete.get(event.getName());
        if (complete != null) {
            try {
                complete.handle(event);
            } catch (Exception e) {
                Sentry.captureException(e);
                throw e;
            }
        }
    }

    @Override
    public List<SlashCommandData> getSlashCommands() {
        return this.commands.values().stream().map(Command::getSlashCommand).collect(Collectors.toList());
    }

    protected abstract AdapterContext getAdapterContext();

    protected Command convertCommand(Command command) {
        return command;
    }

    protected Button convertButton(Button button) {
        return button;
    }

    protected AutoComplete convertAutoComplete(AutoComplete autoComplete) {
        return autoComplete;
    }

    private void registerCommands() {
        getAdapterContext().forEachCommand(command -> {
            logger.info("___registering command {}", command.getCommandName());
            this.commands.put(command.getCommandName(), convertCommand(command));
            return null;
        });
    }

    private void registerButtons() {
        getAdapterContext().forEachButton(button -> {
            logger.info("___registering button for component id {}", button.getButtonComponentId());
            this.buttons.put(button.getButtonComponentId(), convertButton(button));
            return null;
        });
    }

    private void registerAutoComplete() {
        getAdapterContext().forEachAutoComplete(autoComplete -> {
            logger.info("___registering autocomplete for command {}", autoComplete.getName());
            this.autoComplete.put(autoComplete.getAutoCompleteName(), convertAutoComplete(autoComplete));
            return null;
        });
    }

    private String getLogPrefix() {
        return "[" + this.getClass().getSimpleName() + "] ";
    }
}
