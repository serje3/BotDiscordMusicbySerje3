package org.serje3.components.commands.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.serje3.components.buttons.music.AddToQueueButton;
import org.serje3.components.commands.music.queue.QueueCommand;
import org.serje3.meta.abs.Command;
import org.serje3.meta.annotations.JoinVoiceChannel;
import org.serje3.rest.domain.gachi.GachiResponse;
import org.serje3.rest.domain.gachi.Song;
import org.serje3.rest.handlers.GachiBassRestHandler;
import org.serje3.services.LavalinkService;
import org.serje3.utils.VoiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class RadioCommand extends Command {

    private final GachiBassRestHandler gachiBassRestHandler = new GachiBassRestHandler();

    private final Logger logger = LoggerFactory.getLogger(RadioCommand.class);

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
                        new SubcommandData(RadioType.GACHINOW.name().toLowerCase(), "Что сейчас играет в моей ass"),
                        new SubcommandData(RadioType.LOFI.name().toLowerCase(), "Чилл"),
                        new SubcommandData(RadioType.PHONK.name().toLowerCase(), "для педиков"),
                        new SubcommandData(RadioType.SYNTHWAVE.name().toLowerCase(), "хз че написать"),
                        new SubcommandData(RadioType.DARKAMBIENT.name().toLowerCase(), "подумать над своим поведением")
                );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        RadioType radioType = RadioType.valueOf(event.getSubcommandName().toUpperCase());

        if (RadioType.getRadioList().contains(radioType)){
            VoiceHelper.joinHelper(event);
            startRadio(radioType, event);
        } else if (radioType == RadioType.GACHINOW) {
            whatPlayingNowInGachi(event);
        }

    }

    private void startRadio(RadioType radioType, SlashCommandInteractionEvent event){
        final String identifier = chooseIdentifier(radioType);
        final long guildId = event.getGuild().getIdLong();
        new QueueCommand().play(event, guildId, identifier);
    }

    private void whatPlayingNowInGachi(SlashCommandInteractionEvent event){
        try {
            GachiResponse response = gachiBassRestHandler.now();
            if (!response.isSuccessful()){
                event.getHook().sendMessage("Не даёт посмотреть сука").queue();
                return;
            }
            Song song = response.getNow_playing().getSong();
            String title = song.getTitle();


            event.getHook().sendMessageEmbeds(
                    VoiceHelper.getMessageEmbed(
                            null,
                            title,
                            "В my anal играет",
                            "Gachi bass radio",
                            song.getArt(),
                            event.getMember(),
                            song.getArtist(),
                            song.getArt()
                    )
            ).addActionRow(
                    new AddToQueueButton().asJDAButton()
            ).queue();
        } catch (ExecutionException | InterruptedException e) {
            logger.error(e.getMessage());
            event.getHook().sendMessage("Все пошло по пиздец").queue();
        }
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
        GACHINOW,
        LOFI,
        PHONK,
        SYNTHWAVE,
        DARKAMBIENT;

        public static List<RadioType> getRadioList(){
            return List.of(GACHI, SYNTHWAVE, LOFI, PHONK, DARKAMBIENT);
        }
    }
}
