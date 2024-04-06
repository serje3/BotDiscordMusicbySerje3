package org.serje3.components.commands.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.serje3.components.commands.music.queue.QueueCommand;
import org.serje3.meta.abs.Command;
import org.serje3.meta.annotations.JoinVoiceChannel;
import org.serje3.utils.VoiceHelper;

public class RadioCommand extends Command {
    @Override
    public String getName() {
        return "radio";
    }

    @Override
    public String getDescription() {
        return "Включить стрим";
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return super.getSlashCommand()
                .addSubcommands(
                        new SubcommandData(RadioType.GACHI.name().toLowerCase(), "Spanking"),
                        new SubcommandData(RadioType.LOFI.name().toLowerCase(), "Чилл"),
                        new SubcommandData(RadioType.PHONK.name().toLowerCase(), "для педиков"),
                        new SubcommandData(RadioType.SYNTHWAVE.name().toLowerCase(), "хз че написать"),
                        new SubcommandData(RadioType.DARKAMBIENT.name().toLowerCase(), "подумать над своим поведением")
                );
    }

    @Override
    @JoinVoiceChannel
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        event.deferReply().queue();
        RadioType radioType = RadioType.valueOf(event.getSubcommandName().toUpperCase());

        final String identifier = chooseIdentifier(radioType);
        final long guildId = event.getGuild().getIdLong();
        new QueueCommand().play(client, event, guildId, identifier);
    }

    private String chooseIdentifier(RadioType type) {
        switch (type){
            case GACHI -> {
                return "https://www.youtube.com/watch?v=akHAQD3o1NA";
            }
            case LOFI -> {
                return "https://www.youtube.com/watch?v=rUxyKA_-grg";
            }
            case PHONK -> {
                return "https://www.youtube.com/watch?v=8v_kKMaq5po";
            }
            case SYNTHWAVE -> {
                return "https://www.youtube.com/watch?v=4xDzrJKXOOY";
            }
            case DARKAMBIENT -> {
                return "https://www.youtube.com/watch?v=S_MOd40zlYU";
            }
        }
        return null;
    }

    public enum RadioType {
        GACHI,
        LOFI,
        PHONK,
        SYNTHWAVE,
        DARKAMBIENT
    }
}
