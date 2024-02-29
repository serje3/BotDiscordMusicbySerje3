package org.serje3.components.buttons.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.serje3.meta.abs.Button;
import org.serje3.services.MusicService;

public class SkipButton extends Button {
    private final MusicService musicService = new MusicService();

    @Override
    public String getComponentId() {
        return "skip";
    }

    @Override
    public String getLabel() {
        return "Пропустить";
    }

    @Override
    public ButtonType getButtonType() {
        return ButtonType.PRIMARY;
    }

    @Override
    public void handle(ButtonInteractionEvent event, LavalinkClient client) {
        musicService.skipTrack(event, client);
    }
}
