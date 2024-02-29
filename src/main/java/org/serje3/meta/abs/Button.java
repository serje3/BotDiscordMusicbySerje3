package org.serje3.meta.abs;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public abstract class Button {
    public abstract String getComponentId();
    public abstract String getLabel();
    public abstract ButtonType getButtonType();

    public abstract void handle(ButtonInteractionEvent event, LavalinkClient client);

    public net.dv8tion.jda.api.interactions.components.buttons.Button asJDAButton(){
        ButtonType type = getButtonType();
        switch (type) {
            case PRIMARY -> {
                return net.dv8tion.jda.api.interactions.components.buttons.Button.primary(getComponentId(), getLabel());
            }
            case DANGER -> {
                return net.dv8tion.jda.api.interactions.components.buttons.Button.danger(getComponentId(), getLabel());
            }
            case SECONDARY -> {
                return net.dv8tion.jda.api.interactions.components.buttons.Button.secondary(getComponentId(), getLabel());
            }
            case SUCCESS -> {
                return net.dv8tion.jda.api.interactions.components.buttons.Button.success(getComponentId(), getLabel());
            }
        }
        return net.dv8tion.jda.api.interactions.components.buttons.Button.of(ButtonStyle.UNKNOWN, getComponentId(), getLabel());
    }

    public enum ButtonType {
        PRIMARY,
        SECONDARY,
        DANGER,
        SUCCESS
    }
}
