package org.serje3.components.commands.suno.handlers;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.serje3.components.commands.music.queue.QueueCommand;
import org.serje3.domain.TrackContext;
import org.serje3.meta.interfaces.CommandExecutable;
import org.serje3.rest.domain.SunoClip;
import org.serje3.rest.handlers.SunoRestHandler;
import org.serje3.services.SunoService;
import org.serje3.utils.SlashEventHelper;
import org.serje3.utils.TrackQueue;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;


public class Playlist implements CommandExecutable {

    private final SunoRestHandler sunoRestHandler = new SunoRestHandler();
    private final SunoService sunoService = new SunoService();

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        AtomicBoolean deferAcknowledged = new AtomicBoolean(false);

        Long guildId = event.getGuild().getIdLong();

        OptionMapping pageOption = event.getOption("page");
        int page;
        try {
            page = sunoService.handlePageOptionMapping(pageOption);
        } catch (IllegalArgumentException e) {
            event.getHook().sendMessage(e.getLocalizedMessage()).queue();
            return;
        }


        sunoRestHandler.feed(event.getUser().getIdLong(), page).thenAccept((clips) -> {
            if (clips.isEmpty()) {
                event.getHook().sendMessage("Страница пуста").queue();
            }

            SunoClip firstClip = clips.get(0);
            new QueueCommand().play(event, guildId, firstClip.getAudioUrl(), 35, tracks -> {
                if (clips.size() > 1) {
                    TrackQueue.addAll(guildId, clips.subList(1, clips.size()).stream()
                            .map(clip -> {
                                TrackContext context = SlashEventHelper.createTrackContextFromDiscordMeta(null, event.getMember(), event.getChannel().asTextChannel(), guildId, clip.getAudioUrl());
                                context.setTitle("Suno: " + clip.getTitle());
                                return context;
                            })
                            .toList());
                }

                event.getHook().sendMessage("Suno клипы добавлены в очередь: " + clips.size()).queue();
                deferAcknowledged.set(true);
            });

            // answer later if no answer already
            new Thread(() -> {
                try {
                    Thread.sleep(7000);
                    if (!deferAcknowledged.get()) {
                        event.getHook().sendMessage("да хуй знает че так долго сам хз").queue();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, "suno_playlist_send_message").start();

        }).exceptionally((e) -> {
            event.getHook().sendMessage("Пиздец баля: " + e.getLocalizedMessage()).queue();
            return null;
        });
    }
}
