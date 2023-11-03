package org.serje3.commands.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.serje3.meta.abs.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class LeaveCommand extends Command {
    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getDescription() {
        return "Выйти из голосового канала";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        event.getJDA().getDirectAudioController().disconnect(Objects.requireNonNull(event.getGuild()));
        Link link = client.getLink(event.getGuild().getIdLong());
        link.destroyPlayer().subscribe(System.out::println);
        event.reply(this.getReplyMessage(event, client)).queue();
    }


    private String getReplyMessage(SlashCommandInteractionEvent event, LavalinkClient client) {
        Random random = new Random();
        List<String> responses = new ArrayList<>();
        String channelName = event.getMember().getVoiceState().getChannel().getName();
        String memberName = event.getMember().getEffectiveName();
        responses.add("Честно говоря я ваш " + channelName + " в рот ебал, отключаюсь");
        responses.add("От вас воняет я по съебам");
        responses.add("Мне мама кушать приготовила, но это неважно, я просто с лохами не общаюсь пока");
        responses.add("P4ROM L3DOK0L");
        responses.add(memberName + " эта шваль меня выкинула");


        return responses.get(random.nextInt(responses.size()));
    }
}
