package org.serje3.commands.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.serje3.meta.abs.Command;
import org.serje3.meta.annotations.JoinVoiceChannel;


public class TTSCommand extends Command {
    @Override
    public String getName() {
        return "tts";
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return getDefaultSlashCommand("TTS")
                .addOption(OptionType.STRING,
                        "текст",
                        "текст в голос че не понятного",
                        true)
                .addOption(OptionType.STRING,
                        "голос",
                        "Выберите нужный голос из списка https://api.flowery.pw/v1/tts/voices",
                        false);
    }

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
        new PlayCommand().play(client, event, guildId, url.replaceAll(" ", "%20"), 100);
    }
}
