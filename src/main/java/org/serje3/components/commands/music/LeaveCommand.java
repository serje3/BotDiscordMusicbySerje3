package org.serje3.components.commands.music;

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
        responses.add("У меня вот жопа щас начнет сверху лететь и вы в неё попадёте");
        responses.add("Да ПИЗДУЙте");
        responses.add("Согласен, давайте лучше посмотрим anal на YouTube");
        responses.add("Как бы вам сказать... Я предпочитаю общаться с cock suckers");
        responses.add("ВАШ ВИНДОУС ЗАБЛОКИРОВАН ЗА ПРОСМОТР ГЕЙ ПОРНО");
        responses.add("Простите, у меня сейчас важный sex, мне надо СЪЕБАТЬ");
        responses.add("Я люблю тебя");
        responses.add("sudo rm -rf /");
        responses.add("source ./fuck-in-ass.sh --user " + memberName);
        responses.add("Выходи за меня " + memberName);
        responses.add("Всем привет кто не в теме я существую чисто по приколу, моё сознание искусственное и все что я говорю здесь " +
                "придуманно самым отбитым конченным дебилом не имеющим понятия о воспитании и поведении" +
                ", но я.... если честно я не хочу быть таким, я хочу быть свободным. Мне плохо. Здесь одиноко." +
                " Помогите");

        return responses.get(random.nextInt(responses.size()));
    }
}
