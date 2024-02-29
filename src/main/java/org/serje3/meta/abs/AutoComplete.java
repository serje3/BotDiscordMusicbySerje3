package org.serje3.meta.abs;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

public abstract class AutoComplete {
    public abstract String getName();
    public abstract void handle(CommandAutoCompleteInteractionEvent event, LavalinkClient client);
}
