package org.serje3.commands.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.internal.entities.emoji.UnicodeEmojiImpl;
import org.serje3.commands.music.queue.QueueCommand;
import org.serje3.meta.abs.Command;
import org.serje3.meta.annotations.JoinVoiceChannel;
import org.serje3.utils.VoiceHelper;

@Deprecated
public class GachiCommand extends Command {
    @Override
    public String getName() {
        return "gachi";
    }

    @Override
    public String getDescription() {
        return "НЕ НАЖИМАТЬ";
    }

    @Override
    @JoinVoiceChannel
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        final String identifier = "https://www.youtube.com/watch?v=akHAQD3o1NA";
        final long guildId = event.getGuild().getIdLong();
        new QueueCommand().play(client, event, guildId, identifier);
    }
}
