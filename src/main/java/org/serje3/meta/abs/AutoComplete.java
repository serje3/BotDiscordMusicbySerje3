package org.serje3.meta.abs;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.serje3.config.BotConfig;

import java.io.IOException;

public abstract class AutoComplete {
    public abstract String getName();
    public final String getAutoCompleteName() {
        boolean isTest = false;
        try {
            isTest = BotConfig.getProperty("test").equals("true");
        } catch (IOException ignored) {
        }


        String prefix = isTest ? "test_" : "";
        return prefix + getName();
    }
    public abstract void handle(CommandAutoCompleteInteractionEvent event, LavalinkClient client);
}
