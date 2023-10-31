package org.serje3.meta.decorators;


import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.meta.abs.Command;
import org.serje3.meta.annotations.JoinVoiceChannel;
import org.serje3.utils.VoiceHelper;

public class MusicCommandDecorator extends Command {
    private final Command command;

    public MusicCommandDecorator(Command command) {
        this.command = command;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        this.preExecute(event, client);
        this.command.execute(event, client);
    }


    private void preExecute(SlashCommandInteractionEvent event, LavalinkClient client) {
        Class<?> commandClass = this.command.getClass();
        try {
            JoinVoiceChannel joinAnnotation = commandClass.getDeclaredMethod(
                            "execute", SlashCommandInteractionEvent.class, LavalinkClient.class)
                    .getAnnotation(JoinVoiceChannel.class);
            System.out.println(joinAnnotation);
            if (joinAnnotation != null) {
                this.annotationJoin(event, client);
            }
        } catch (NoSuchMethodException e) {
            System.out.println("No execute method");
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            System.out.println("Null joinVoiceChannel annotation");
            // do nothing, its ok
        }
    }

    private void annotationJoin(SlashCommandInteractionEvent event, LavalinkClient client) {
        final Guild guild = event.getGuild();
        System.out.println("ANNOTATION JOIN");
        // We are already connected, go ahead and play
        if (guild.getSelfMember().getVoiceState().inAudioChannel()) {
//            event.deferReply(false).queue();
        } else {
            // Connect to VC first
            VoiceHelper.joinHelper(event);
        }

    }
}
