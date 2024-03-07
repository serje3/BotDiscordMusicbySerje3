package org.serje3.components.buttons.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.serje3.meta.abs.Button;
import org.serje3.services.MusicService;

public class PausePlayButton extends Button {
    private final MusicService musicService = new MusicService();
    @Override
    public final String getComponentId() {
        return "pause-play";
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public Emoji getLabelEmoji() {
        return Emoji.fromFormatted("▶\uFE0F");
    }

    @Override
    public ButtonType getButtonType() {
        return ButtonType.SECONDARY;
    }

    @Override
    public void handle(ButtonInteractionEvent event, LavalinkClient client) {
        musicService.pauseMusic(event, client).subscribe(player -> {
            event.editButton(new PauseButton().asJDAButton()).queue();
        });
    }
}
