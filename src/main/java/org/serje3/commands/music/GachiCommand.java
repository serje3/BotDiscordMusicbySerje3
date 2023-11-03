package org.serje3.commands.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.internal.entities.emoji.UnicodeEmojiImpl;
import org.serje3.meta.abs.Command;
import org.serje3.utils.VoiceHelper;

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
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        final Guild guild = event.getGuild();

        // We are already connected, go ahead and play
        if (guild.getSelfMember().getVoiceState().inAudioChannel()) {
//            event.deferReply(false).queue();
        } else {
            // Connect to VC first
            VoiceHelper.joinHelper(event);
        }

        final String identifier = "https://www.youtube.com/watch?v=akHAQD3o1NA";
        final long guildId = guild.getIdLong();
        new PlayCommand().play(client, event, guildId, identifier);
    }
}
