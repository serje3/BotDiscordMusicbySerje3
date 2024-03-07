package org.serje3.meta.abs;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public abstract class Button {
    public abstract String getComponentId();

    public abstract ButtonType getButtonType();

    public abstract void handle(ButtonInteractionEvent event, LavalinkClient client);

    public abstract String getLabel();

    public Emoji getLabelEmoji(){
        return null;
    }

    public net.dv8tion.jda.api.interactions.components.buttons.Button asJDAButton() {
        ButtonType type = getButtonType();
        String label = getLabel();
        if (label == null || label.isEmpty()){
            return createButtonWithEmoji(type);
        } else {
            return createButtonWithLabel(type);
        }
    }

    public enum ButtonType {
        PRIMARY,
        SECONDARY,
        DANGER,
        SUCCESS
    }


    private net.dv8tion.jda.api.interactions.components.buttons.Button createButtonWithLabel(ButtonType type){
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

    private net.dv8tion.jda.api.interactions.components.buttons.Button createButtonWithEmoji(ButtonType type){
        switch (type) {
            case PRIMARY -> {
                return net.dv8tion.jda.api.interactions.components.buttons.Button.primary(getComponentId(), getLabelEmoji());
            }
            case DANGER -> {
                return net.dv8tion.jda.api.interactions.components.buttons.Button.danger(getComponentId(), getLabelEmoji());
            }
            case SECONDARY -> {
                return net.dv8tion.jda.api.interactions.components.buttons.Button.secondary(getComponentId(), getLabelEmoji());
            }
            case SUCCESS -> {
                return net.dv8tion.jda.api.interactions.components.buttons.Button.success(getComponentId(), getLabelEmoji());
            }
        }
        return net.dv8tion.jda.api.interactions.components.buttons.Button.of(ButtonStyle.UNKNOWN, getComponentId(), getLabelEmoji());
    }
}
