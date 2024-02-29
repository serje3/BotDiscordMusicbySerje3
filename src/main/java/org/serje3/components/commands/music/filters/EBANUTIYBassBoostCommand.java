package org.serje3.components.commands.music.filters;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.protocol.v4.Band;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EBANUTIYBassBoostCommand extends BassBoostCommand {
    @Override
    public String getName() {
        return "priora";
    }

    @Override
    public String getDescription() {
        return "ДАЙ БОГ ВАМ ЗДОРОВЬЯ";
    }

    protected String getReplyString() {
        Random random = new Random();
        List<String> responses = new ArrayList<>(){
            {
                add("DOLBIT NORMALNO");
                add("ПИЗДА ВАШИМ УШАМ");
                add("URAL SOUND PREDSTAVLYAET");
                add("VAM JOPA INCORPORATED");
                add("РАЗРАБОТЧИК ЭТОГО АНАЛА ПИСАЛ ЭТО СООБЩЕНИЕ С МЫСЛЬЮ О ТОМ КАКОЙ В ПОЕЗДЕ(В КОТОРОМ ОН СЕЙЧАС ЕДЕТ)" +
                        "ОБОСРАННЫЙ ТОЛКАН И ЕГО ЖЕЛАНИЕ СРАТЬ РАСТËТ ФАКТОРИАЛЬНО...");
                add("Решили узнать, какая армия лучше, т. к. войну устраивать негуманно, то проводят охоту. Задача: \n" +
                        "— Доставить зайца живым или мертвым.\n" +
                        "Идут американцы: \n" +
                        "— Авиация бомбит лес, стрекот пулеметных очередей, взрывы, через час выносят обугленную тушку зайца. Пошли англичане: \n" +
                        "— В лесу гробовая тишина, через полчаса раздается приглушенный выстрел, выносят двух мертвых зайцев. Пошли русские прапоры: \n" +
                        "— Через пять минут выводят окровавленного медведя. Жюри:\n" +
                        "— Это что — заяц?\n" +
                        "Медведь:\n" +
                        "— Да заяц я, заяц, только сапогами по почкам больше не бейте.\n" +
                        "\n" +
                        "© https://anekdoty.ru/pro-medvedey/");
                add("Говорят, что алгоритм является алгоритмом константного времени (записывается как время O(1) " +
                        "если значение T(n) ограничено значением, не зависящим от размера твоего анала в дециметрах");
                add("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
                add("Я СОВЕРШИЛ ВОЕННЫЕ ПРЕСТУПЛЕНИЕ В ЮГОСЛАВИИ");
            }
        };


        return responses.get(random.nextInt(responses.size()));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        super.execute(event, client);
    }

    protected List<Band> getEqualizer() {
        return new ArrayList<>() {
            {
                add(new Band(0, 1f));
                add(new Band(1, 1f));
                add(new Band(2, 1f));
                add(new Band(3, 0.7f));
                add(new Band(4, -0.5f));
                add(new Band(5, 0.15f));
                add(new Band(6, -0.45f));
                add(new Band(7, 0.23f));
                add(new Band(8, 0.35f));
                add(new Band(9, 0.35f));
                add(new Band(10, 0.35f));
                add(new Band(11, 0.3f));
                add(new Band(12, 0.35f));
                add(new Band(13, 0.0f));
            }
        };
    }
}
