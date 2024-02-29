package org.serje3.services;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.protocol.Track;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.serje3.components.buttons.music.PauseButton;
import org.serje3.components.buttons.music.SkipButton;
import org.serje3.meta.enums.PlaySourceType;
import org.serje3.utils.VoiceHelper;

@RequiredArgsConstructor
public class MusicService {

    public void pauseMusic(SlashCommandInteractionEvent event, LavalinkClient client){
        client.getLink(event.getGuild().getIdLong())
                .getPlayer()
                .flatMap((player) -> player.setPaused(!player.getPaused()))
                .subscribe((player) -> {
                    event.reply("Плеер " + (player.getPaused() ? "на паузе" : "возобновлён") + "!").queue();
                });
    }

    public void pauseMusic(ButtonInteractionEvent event, LavalinkClient client){
        client.getLink(event.getGuild().getIdLong())
                .getPlayer()
                .flatMap((player) -> player.setPaused(!player.getPaused()))
                .subscribe((player) -> {
                    event.reply("Плеер " + (player.getPaused() ? "на паузе" : "возобновлён") + "!").queue();
                });
    }


    public void whatsPlayingNow(SlashCommandInteractionEvent event, LavalinkClient client) {
        client.getLink(event.getGuild().getIdLong()).getPlayer().subscribe(player -> {
            Track track = player.getTrack();
            if (track == null ||  player.getPosition() >= track.getInfo().getLength()) {
                event.reply("Никаких треков сейчас не играет").queue();
                return;
            };
            event.replyEmbeds(VoiceHelper.wrapTrackEmbed(track, event.getMember(), ""))
                    .addActionRow(
                            new PauseButton().asJDAButton(),
                            new SkipButton().asJDAButton(),
                            Button.link(track.getInfo().getUri(), "Ссылка на трек")
                    ).queue();
        });
    }

    public void skipTrack(SlashCommandInteractionEvent event, LavalinkClient client) {
        Link link = client.getLink(event.getGuild().getIdLong());
        link.getPlayer().subscribe(player -> {
            if (player.getTrack() == null || !player.getState().getConnected()) {
                event.reply("Никаких треков сейчас не играет").queue();
                return;
            }
            System.out.println("SKIPPING " + player.getTrack().getInfo().getTitle());
            System.out.println("isStream " + player.getTrack().getInfo().isStream());
            System.out.println("isSeekable " + player.getTrack().getInfo().isSeekable());


            link.createOrUpdatePlayer().setPosition(player.getTrack().getInfo().getLength())
                    .subscribe(d -> {
                        System.out.println("succ& " + d);
                    });

            if (player.getTrack().getInfo().isStream()) {
                event.reply("Стрим выключен").queue();
            } else {
                event.reply("Трек пропущен").queue();
            }
        });
    }

    public void skipTrack(ButtonInteractionEvent event, LavalinkClient client) {
        Link link = client.getLink(event.getGuild().getIdLong());
        link.getPlayer().subscribe(player -> {
            if (player.getTrack() == null || !player.getState().getConnected()) {
                event.reply("Никаких треков сейчас не играет").queue();
                return;
            }
            System.out.println("SKIPPING " + player.getTrack().getInfo().getTitle());
            System.out.println("isStream " + player.getTrack().getInfo().isStream());
            System.out.println("isSeekable " + player.getTrack().getInfo().isSeekable());


            link.createOrUpdatePlayer().setPosition(player.getTrack().getInfo().getLength())
                    .subscribe(d -> {
                        System.out.println("succ& " + d);
                    });

            if (player.getTrack().getInfo().isStream()) {
                event.reply("Стрим выключен").queue();
            } else {
                event.reply("Трек пропущен").queue();
            }
        });
    }

    public String getSearchPrefix(String subCommandName, String identifier){
        final PlaySourceType playType = PlaySourceType.valueOf(subCommandName.toUpperCase());
        String prefix = switch (playType) {
            case YOUTUBE -> "ytsearch:";
            case SOUNDCLOUD -> "scsearch:";
            case YANDEXMUSIC -> "ymsearch:";
            case SPOTIFY -> "spsearch:";
            default -> "";
        };

        if (identifier.startsWith("https://")) {
            prefix = "";
        }
        return prefix;
    }
}
