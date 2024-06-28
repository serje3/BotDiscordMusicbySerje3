package org.serje3.meta.abs;

import kotlin.NotImplementedError;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.serje3.config.BotConfig;

import java.io.IOException;

public abstract class Modal {
    public abstract String getName();
    public abstract String getTitle();
    public final String getModalName() {
        boolean isTest = false;
        try {
            isTest = BotConfig.getProperty("test").equals("true");
        } catch (IOException ignored) {
        }


        String prefix = isTest ? "test_" : "";
        return prefix + getName();
    }
    public abstract void handle(ModalInteractionEvent event);

    public abstract net.dv8tion.jda.api.interactions.modals.Modal build();
}
