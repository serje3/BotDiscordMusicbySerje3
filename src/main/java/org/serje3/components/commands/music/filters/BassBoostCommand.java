package org.serje3.components.commands.music.filters;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.protocol.v4.Band;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;

public class BassBoostCommand extends NormalizeFilterCommand {
    @Override
    public String getName() {
        return "bass";
    }

    @Override
    public String getDescription() {
        return "ВКЛЮЧИТЬ ЕБАНУТЫЙ УШЕДОЛБИЧЕСКИЙ БАСС";
    }

    protected String getReplyString(){
        return "Ща ебанёт погоди";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        super.execute(event, client);
    }

    protected List<Band> getEqualizer() {
        return new ArrayList<>() {
            {
                add(new Band(0, 0.6f));
                add(new Band(1, 0.67f));
                add(new Band(2, 0.67f));
                add(new Band(3, 0));
                add(new Band(4, -0.5f));
                add(new Band(5, 0.15f));
                add(new Band(6, -0.45f));
                add(new Band(7, 0.23f));
                add(new Band(8, 0.35f));
                add(new Band(9, 0.45f));
                add(new Band(10, 0.55f));
                add(new Band(11, 0.6f));
                add(new Band(12, 0.55f));
                add(new Band(13, 0.0f));
            }
        };
    }
}
