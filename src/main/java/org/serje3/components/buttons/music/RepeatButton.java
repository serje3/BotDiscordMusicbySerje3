package org.serje3.components.buttons.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.serje3.meta.abs.Button;
import org.serje3.utils.TrackQueue;

import java.util.Objects;

public class RepeatButton extends Button {
    @Override
    public String getComponentId() {
        return "repeat";
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public Emoji getLabelEmoji() {
        return Emoji.fromFormatted("\uD83D\uDD01");
    }

    @Override
    public ButtonType getButtonType() {
        return ButtonType.SECONDARY;
    }

    @Override
    public void handle(ButtonInteractionEvent event, LavalinkClient client) {
        Boolean repeat = TrackQueue.toggleRepeat(event.getGuild().getIdLong());

        if (repeat){
            event.editButton(new On().asJDAButton()).queue();
        } else {
            event.editButton(new RepeatButton().asJDAButton()).queue();
        }
    }


    public static class On extends RepeatButton {
        @Override
        public String getComponentId() {
            return "repeat-on";
        }

        @Override
        public ButtonType getButtonType() {
            return ButtonType.PRIMARY;
        }
    }
}
