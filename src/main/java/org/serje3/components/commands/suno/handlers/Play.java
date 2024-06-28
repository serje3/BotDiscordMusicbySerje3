package org.serje3.components.commands.suno.handlers;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.components.commands.music.queue.QueueCommand;
import org.serje3.meta.interfaces.CommandExecutable;
import org.serje3.rest.domain.SunoClip;
import org.serje3.rest.handlers.SunoRestHandler;
import org.serje3.utils.VoiceHelper;

public class Play implements CommandExecutable {
    private final SunoRestHandler sunoRestHandler = new SunoRestHandler();


    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        int playIndex = event.getOption("index").getAsInt();
        if (playIndex < 0) {
            event.reply("Неверный индекс").setEphemeral(true).queue();
            return;
        }

        sunoRestHandler.feed(event.getUser().getIdLong()).thenAccept((clips) -> {
            try{
                SunoClip clip = clips.get(playIndex);
                if (clip.getAudioUrl() == null){
                    event.reply("Аудио ещё не готово").queue();
                    return;
                }
                VoiceHelper.joinHelper(event);
                new QueueCommand().play(event, event.getGuild().getIdLong(), clip.getAudioUrl());
            } catch (IndexOutOfBoundsException e) {
                event.reply("Индекс неверный: " + e.getLocalizedMessage()).queue();
            }
        }).exceptionally((e) -> {
            event.reply("Пиздец баля: " + e.getLocalizedMessage()).queue();
            return null;
        });
    }
}
