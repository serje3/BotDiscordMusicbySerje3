package org.serje3.components.commands.suno.handlers;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.components.buttons.music.AddToQueueButton;
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
                    event.getHook().sendMessage("Аудио ещё не готово").queue();
                    return;
                }
                VoiceHelper.joinHelper(event);
                new QueueCommand().play(event, event.getGuild().getIdLong(), clip.getAudioUrl(), 35, (track) -> {
                    event.getHook().sendMessageEmbeds(
                            VoiceHelper.getMessageEmbed(
                                    clip.getVideoUrl(),
                                    clip.getTitle(),
                                    "Жанр: " + clip.getMetadata().get("tags"),
                                    "suno.com",
                                    clip.getImageUrl(),
                                    event.getMember(),
                                    event.getMember().getEffectiveName(),
                                    clip.getImageLargeUrl()
                            )
                    ).addActionRow(new AddToQueueButton().asJDAButton()).queue();
                });
            } catch (IndexOutOfBoundsException e) {
                event.getHook().sendMessage("Индекс неверный: " + e.getLocalizedMessage()).queue();
            }
        }).exceptionally((e) -> {
            event.getHook().sendMessage("Пиздец баля: " + e.getLocalizedMessage()).queue();
            return null;
        });
    }
}
