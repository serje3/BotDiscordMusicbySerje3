package org.serje3.meta.abs;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.serje3.config.BotConfig;

import java.io.IOException;

public abstract class Button {
    public abstract String getComponentId();

    public final String getButtonComponentId(){
        boolean isTest = false;
        try {
            isTest = BotConfig.getProperty("test").equals("true");
        } catch (IOException ignored) {
        }
        String prefix = isTest ? "test_" : "";
        return prefix + getComponentId();
    };

    public abstract ButtonType getButtonType();

    public abstract void handle(ButtonInteractionEvent event);

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
                return net.dv8tion.jda.api.interactions.components.buttons.Button.primary(getButtonComponentId(), getLabel());
            }
            case DANGER -> {
                return net.dv8tion.jda.api.interactions.components.buttons.Button.danger(getButtonComponentId(), getLabel());
            }
            case SECONDARY -> {
                return net.dv8tion.jda.api.interactions.components.buttons.Button.secondary(getButtonComponentId(), getLabel());
            }
            case SUCCESS -> {
                return net.dv8tion.jda.api.interactions.components.buttons.Button.success(getButtonComponentId(), getLabel());
            }
        }
        return net.dv8tion.jda.api.interactions.components.buttons.Button.of(ButtonStyle.UNKNOWN, getButtonComponentId(), getLabel());
    }

    private net.dv8tion.jda.api.interactions.components.buttons.Button createButtonWithEmoji(ButtonType type){
        switch (type) {
            case PRIMARY -> {
                return net.dv8tion.jda.api.interactions.components.buttons.Button.primary(getButtonComponentId(), getLabelEmoji());
            }
            case DANGER -> {
                return net.dv8tion.jda.api.interactions.components.buttons.Button.danger(getButtonComponentId(), getLabelEmoji());
            }
            case SECONDARY -> {
                return net.dv8tion.jda.api.interactions.components.buttons.Button.secondary(getButtonComponentId(), getLabelEmoji());
            }
            case SUCCESS -> {
                return net.dv8tion.jda.api.interactions.components.buttons.Button.success(getButtonComponentId(), getLabelEmoji());
            }
        }
        return net.dv8tion.jda.api.interactions.components.buttons.Button.of(ButtonStyle.UNKNOWN, getButtonComponentId(), getLabelEmoji());
    }
}
