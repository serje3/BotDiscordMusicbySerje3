package org.serje3.components.commands.suno.handlers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.meta.interfaces.CommandExecutable;

public class Help implements CommandExecutable {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.replyEmbeds(
                new EmbedBuilder()
                        .setTitle("Помощь по настройке Suno")
                        .addField("1. Вход", "Нужно авторизоваться на сайте suno.com под своим пользователем", false)
                        .addField("2. Получить cookie", "Далее нужно открыть инструменты разработчика (F12) -> Сеть(Network)" +
                                " -> Выбрать запрос с path /v1/client?_clerk_js_version=? -> Заголовки -> Скопировать значение в заголовке Cookie (как на фото по ссылке ниже", false)
                        .addField("3. Получить session_id", "В этой же панели в браузере перейти из 'Заголовки' в Предвартельный просмотр -> Раскрыть 'response' -> " +
                                "В 'response' будет параметр 'last_active_session_id' он нам и нужен, копируем его", false)
                        .addField("4. Авторизовать аккаунт в боте", "Вызовите команду '/suno login' и введите в эти поля значения скопированные ранее. Нужны полностью значения из Cookie и last_active_session_id", false)
                        .addField("(Дополнительно)", "Скриншоты где брать cookie и session id: " +
                                "Cookie - https://github.com/serje3/Suno-API-cocker-impl/blob/main/images/cookie-export.png?raw=true \n" +
                                "Session ID - https://github.com/serje3/Suno-API-cocker-impl/blob/main/images/session-id-export.png?raw=true", false)
                        .setImage("https://github.com/serje3/Suno-API-cocker-impl/blob/main/images/window-login.png?raw=true")
                        .build()
        ).queue();
    }
}
