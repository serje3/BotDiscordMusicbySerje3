package org.serje3.utils;

import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.protocol.v4.Track;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class VoiceHelper {

    public static void joinHelper(SlashCommandInteractionEvent event) {
        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (memberVoiceState.inAudioChannel()) {
            event.getJDA().getDirectAudioController().connect(memberVoiceState.getChannel());
        }
    }

    public static void play(Link link, Track track, Integer volume) {
        link.createOrUpdatePlayer()
                .setEncodedTrack(track.getEncoded())
                .setVolume(volume).setNoReplace(false)
                .asMono()
                .subscribe((ignored) -> {
                    System.out.println("player - " + ignored);
//                    long guildId = link.getGuildId();
//                    TextChannel channel = BotApplication.Bot.getTextChannelById(guildId);
////                    if (channel != null) {
////                        channel.sendMessage("Сейчас играет: " + track.getInfo().getTitle()).queue();
////                    }
                });
    }
}
