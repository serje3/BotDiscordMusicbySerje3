package org.serje3.commands.music.queue;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.protocol.Track;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.serje3.meta.abs.Command;
import org.serje3.utils.VoiceHelper;


public class QueueNowCommand extends Command {
    @Override
    public String getName() {
        return "now";
    }

    @Override
    public String getDescription() {
        return "Узнать что сейчас играет";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        Link link = client.getLink(event.getGuild().getIdLong());

        link.getPlayer().subscribe(player -> {
            Track track = player.getTrack();
            if (track == null ||  player.getPosition() >= track.getInfo().getLength()) {
                event.reply("Никаких треков сейчас не играет").queue();
                return;
            };
            event.replyEmbeds(VoiceHelper.wrapTrackEmbed(track, event.getMember(), "")).queue();
        });
    }
}
