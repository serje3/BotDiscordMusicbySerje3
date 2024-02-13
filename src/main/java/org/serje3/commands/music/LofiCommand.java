package org.serje3.commands.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.commands.music.queue.QueueCommand;
import org.serje3.meta.abs.Command;
import org.serje3.utils.VoiceHelper;

public class LofiCommand extends Command {
    @Override
    public String getName() {
        return "lofi";
    }

    @Override
    public String getDescription() {
        return "chill";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        final Guild guild = event.getGuild();

        if (!guild.getSelfMember().getVoiceState().inAudioChannel()) {
            VoiceHelper.joinHelper(event);
        }

        final String identifier = "https://www.youtube.com/watch?v=rUxyKA_-grg";
        final long guildId = guild.getIdLong();
        new QueueCommand().play(client, event, guildId, identifier);
    }
}
