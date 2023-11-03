package org.serje3.commands.base;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.serje3.meta.abs.Command;
import org.serje3.utils.commands.DefaultCommandList;
import org.serje3.utils.commands.MusicCommandList;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand extends Command {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Помощь по боту";
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return getDefaultSlashCommand("Помощь по боту");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        event.replyEmbeds(createHelpEmbed(event))
                .addActionRow(
                        Button.link("https://vk.com/club200458779", Emoji.fromFormatted("<:VK_EMOJI:1170054842077683842>"))
                )
                .queue();
    }

    private MessageEmbed createHelpEmbed(SlashCommandInteractionEvent event) {
        User bot = event.getJDA().getSelfUser();
        Member member = event.getMember();
        String memberName = member != null ? member.getEffectiveName() : null;
        String memberAvatar = member != null ? member.getAvatarUrl() : null;
        String memberMention = member != null ? member.getAsMention() : null;

        return new MessageEmbed(
                null,
                "Доступные к использованию команды",
                "Здесь вы увидите доступные вам команды бота",
                EmbedType.RICH,
                null,
                event.getMember().getColorRaw(),
                new MessageEmbed.Thumbnail(bot.getEffectiveAvatarUrl(), bot.getEffectiveAvatarUrl(), 100, 100),
                null,
                new MessageEmbed.AuthorInfo(bot.getEffectiveName(), null, bot.getEffectiveAvatarUrl(), bot.getEffectiveAvatarUrl()),
                null,
                new MessageEmbed.Footer(memberName, memberAvatar, memberAvatar),
                null,
                new ArrayList<>() {
                    {
                        addAll(createCommandFields());
                    }
                }
        );
    }


    private List<MessageEmbed.Field> createCommandFields() {
        MusicCommandList musicCommandList = new MusicCommandList();
        DefaultCommandList defaultCommandList = new DefaultCommandList();
        List<MessageEmbed.Field> fields = new ArrayList<>();
        fields.add(new MessageEmbed.Field("Музыка", "", false, false));
        musicCommandList.forEach(command ->
                fields.add(new MessageEmbed.Field("/" + command.getName(), command.getDescription(), true, true)));
        fields.add(new MessageEmbed.Field("", "", false, false));
        fields.add(new MessageEmbed.Field("Базовые", "", false, false));
        defaultCommandList.forEach(command ->
                fields.add(new MessageEmbed.Field("/" + command.getName(), command.getDescription(), true)));
        return fields;
    }
}
