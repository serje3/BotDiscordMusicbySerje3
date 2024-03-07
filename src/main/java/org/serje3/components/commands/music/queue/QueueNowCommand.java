package org.serje3.components.commands.music.queue;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.serje3.components.buttons.music.PauseButton;
import org.serje3.components.buttons.music.PausePlayButton;
import org.serje3.components.buttons.music.RepeatButton;
import org.serje3.components.buttons.music.SkipButton;
import org.serje3.meta.abs.Command;
import org.serje3.services.MusicService;
import org.serje3.utils.exceptions.NoTrackIsPlayingNow;


public class QueueNowCommand extends Command {
    private final MusicService musicService = new MusicService();

    @Override
    public String getName() {
        return "now";
    }

    @Override
    public String getDescription() {
        return "Узнать что сейчас играет";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        Link link = client.getLink(event.getGuild().getIdLong());
        link.getPlayer().subscribe((player) -> {
            try {
                if (player.getTrack() == null) throw new NoTrackIsPlayingNow();
                Button playPauseBtn = player.getPaused() ? new PausePlayButton().asJDAButton() : new PauseButton().asJDAButton();
                event.replyEmbeds(musicService.whatsPlayingNow(event.getMember(), player))
                        .addActionRow(
                                new RepeatButton().asJDAButton(),
                                playPauseBtn,
                                new SkipButton().asJDAButton(),
                                Button.link(player.getTrack().getInfo().getUri(), "Ссылка на трек")
                        ).queue();
            } catch (NoTrackIsPlayingNow e) {
                event.reply(e.getMessage()).queue();
            }
        });    }
}
