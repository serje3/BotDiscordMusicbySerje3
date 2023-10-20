package org.serje3.commands.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.serje3.meta.abs.Command;
import org.serje3.meta.annotations.JoinVoiceChannel;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TTSCommand extends Command {
    @SneakyThrows
    @Override
    @JoinVoiceChannel
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        final Guild guild = event.getGuild();
        final long guildId = guild.getIdLong();

        final String identifier = event.getOption("текст").getAsString();
        OptionMapping voiceOption = event.getOption("голос");
        String voiceQuery = (voiceOption != null) ? "?voice=" + voiceOption.getAsString() : "";
        final String url = "ftts://" + identifier + voiceQuery;
        new PlayCommand().play(client, event, guildId, url.replaceAll(" ", "%20"));
    }
}
