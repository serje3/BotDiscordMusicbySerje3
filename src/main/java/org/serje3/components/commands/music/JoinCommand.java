package org.serje3.components.commands.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.serje3.meta.abs.Command;
import org.serje3.utils.VoiceHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JoinCommand extends Command {
    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getDescription() {
        return "Присоединиться к каналу.";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        VoiceHelper.joinHelper(event);
        event.reply(this.getReplyMessage(event)).queue();
    }

    private String getReplyMessage(SlashCommandInteractionEvent event) {
        Random random = new Random();
        List<String> responses = new ArrayList<>();
        String channelName = event.getMember().getVoiceState().getChannel().getName();
        String memberName = event.getMember().getEffectiveName();
        responses.add("Честно говоря я ваш " + channelName + " в рот ебал, но я присоединился");
        responses.add("Нет ничего хуже чем " + memberName + " без маски клоуна... Рекомендую забанить его на лет 8");
        responses.add("Воняет от " + memberName);
        responses.add("ИДИ НАХУЙ " + memberName);
        responses.add("жопа жопа жопа жопа ");
        responses.add(" пенис пенис пенис пенис ");
        responses.add("ЖАЛЬ ЧТО ТЫ РОДИЛСЯ " + memberName);
        responses.add("УНИКАЛЬНЫЙ ОТВЕТ ДЛЯ ДЕБИЛА " + memberName);
        responses.add("Город может и маленький только из этого города ТУЛУП вышел");
        return responses.get(random.nextInt(responses.size()));
    }
}
