package org.serje3.commands.log;

import dev.arbjerg.lavalink.client.LavalinkClient;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.meta.abs.Command;
import org.serje3.rest.handlers.MemberRestHandler;

import java.time.OffsetDateTime;
import java.util.ArrayList;

public class InfoCommand  extends Command {

    private final MemberRestHandler memberRestHandler;

    public InfoCommand() {
        this.memberRestHandler = new MemberRestHandler();
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Выводит информацию о пользователе";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        memberRestHandler.performAsyncGetMemberInfo(event, (memberInfo) -> {
            System.out.println(memberInfo);
            String name = event.getMember().getEffectiveName();
            event.replyEmbeds(
                    new MessageEmbed(
                            null,
                            "Информация о пользователе " + name,
                            "Статистика",
                            EmbedType.AUTO_MODERATION,
                            OffsetDateTime.now(),
                            event.getMember().getColorRaw(),
                            null,
                            null,
                            new MessageEmbed.AuthorInfo(name, null, event.getMember().getEffectiveAvatarUrl(), null),
                            null,
                            null,
                            null,
                            new ArrayList<>(){
                                {
                                    add(new MessageEmbed.Field("Место в рейтинге самых активных пользователей", memberInfo.getRating().getRank().toString(), false));
                                    add(new MessageEmbed.Field("Общее количество вызванных команд", memberInfo.getRating().getCount().toString(), false));
                                }
                            }
                    )
            ).queue();
        });
    }
}
