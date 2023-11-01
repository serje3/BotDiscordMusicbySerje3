package org.serje3.commands.music.filters;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.protocol.v4.Band;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.List;

public class EBANUTIYBassBoostCommand extends BassBoostCommand {
    @Override
    public String getName() {
        return "ebanutiybass";
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return getDefaultSlashCommand("ДАЙ БОГ ВАМ ЗДОРОВЬЯ");
    }

    protected String getReplyString() {
        return "ПИЗДА ВАШИМ УШАМ";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        super.execute(event, client);
    }

    protected List<Band> getEqualizer() {
        return new ArrayList<>() {
            {
                add(new Band(0, 1f));
                add(new Band(1, 1f));
                add(new Band(2, 1f));
                add(new Band(3, 0.7f));
                add(new Band(4, -0.5f));
                add(new Band(5, 0.15f));
                add(new Band(6, -0.45f));
                add(new Band(7, 0.23f));
                add(new Band(8, 0.35f));
                add(new Band(9, 0.35f));
                add(new Band(10, 0.35f));
                add(new Band(11, 0.3f));
                add(new Band(12, 0.35f));
                add(new Band(13, 0.0f));
            }
        };
    }
}
