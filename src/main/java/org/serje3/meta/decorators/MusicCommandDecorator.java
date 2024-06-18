package org.serje3.meta.decorators;


import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.serje3.meta.abs.Command;
import org.serje3.meta.annotations.JoinVoiceChannel;
import org.serje3.services.MusicService;
import org.serje3.utils.VoiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MusicCommandDecorator extends Command {
    private final Command command;
    private final Logger logger = LoggerFactory.getLogger(MusicCommandDecorator.class);

    public MusicCommandDecorator(Command command) {
        this.command = command;
    }

    @Override
    public String getName() {
        return this.command.getName();
    }

    @Override
    public String getDescription() {
        return this.command.getDescription();
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return command.getSlashCommand();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        this.preExecute(event);
        this.command.execute(event);
    }


    private void preExecute(SlashCommandInteractionEvent event) {
        Class<?> commandClass = this.command.getClass();
        try {
            JoinVoiceChannel joinAnnotation = commandClass.getDeclaredMethod(
                            "execute", SlashCommandInteractionEvent.class)
                    .getAnnotation(JoinVoiceChannel.class);
            if (joinAnnotation != null) {
                this.annotationJoin(event);
                logger.info("Bot joined in voice channel in {} guild", event.getGuild().getId());
            }
        } catch (NoSuchMethodException e) {
            System.out.println("No execute method");
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            System.out.println("Null joinVoiceChannel annotation");
            // do nothing, its ok
        }
    }

    private void annotationJoin(SlashCommandInteractionEvent event) {
        final Guild guild = event.getGuild();
        // We are already connected, go ahead and play
        if (!guild.getSelfMember().getVoiceState().inAudioChannel()) {
            // Connect to VC first
            VoiceHelper.joinHelper(event);
        }
    }

}
