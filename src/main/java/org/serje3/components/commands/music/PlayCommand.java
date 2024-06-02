package org.serje3.components.commands.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.serje3.meta.abs.Command;
import org.serje3.meta.enums.PlaySourceType;
import org.serje3.meta.annotations.JoinVoiceChannel;
import org.serje3.services.MusicService;

import java.util.List;
@Deprecated
public class PlayCommand extends Command {
    protected final MusicService musicService = new MusicService();

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Включает музыку, доступно для выбора несколько источников воспроизведения";
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return super.getSlashCommand()
                .addSubcommands(
                        new SubcommandData(PlaySourceType.YOUTUBE.name().toLowerCase(), "Поиск из ютуба")
                                .addOption(
                                        OptionType.STRING,
                                        "текст".toLowerCase(),
                                        "Строка поиска youtube",
                                        true,
                                        true
                                ),
                        new SubcommandData(PlaySourceType.SOUNDCLOUD.name().toLowerCase(), "Поиск из soundclound")
                                .addOption(
                                        OptionType.STRING,
                                        "текст",
                                        "Строка поиска soundcloud",
                                        true,
                                        true
                                ),
                        new SubcommandData(PlaySourceType.YANDEXMUSIC.name().toLowerCase(), "Поиск из Yandex Music")
                                .addOption(
                                        OptionType.STRING,
                                        "текст",
                                        "Строка поиска Yandex Music",
                                        true,
                                        true
                                ),
                        new SubcommandData(PlaySourceType.SPOTIFY.name().toLowerCase(), "Поиск из spotify")
                                .addOption(
                                        OptionType.STRING,
                                        "текст",
                                        "Строка поиска Spotify",
                                        true,
                                        true
                                ),
                        new SubcommandData(PlaySourceType.YOUTUBEMUSIC.name().toLowerCase(), "Поиск из Youtube Music")
                                .addOption(
                                        OptionType.STRING,
                                        "текст",
                                        "Строка поиска Youtube Music",
                                        true,
                                        true
                                ),
                        new SubcommandData(PlaySourceType.LOCAL.name().toLowerCase(), "Проигрывание из локальных файлов")
                                .addOption(OptionType.ATTACHMENT, "файл", "wdasdksdaasdjasdjkl")
                );
    }

    @Override
    @JoinVoiceChannel
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        final Guild guild = event.getGuild();

        final String identifier = event.getOption("текст").getAsString();
        final long guildId = guild.getIdLong();

        this.play(client, event, guildId, musicService.getSearchPrefix(event.getSubcommandName(), identifier) + identifier);
    }

    public void play(LavalinkClient client, SlashCommandInteractionEvent event,
                     Long guildId, String identifier) {
        this.play(client, event, guildId, identifier, 35);
    }

    public void play(LavalinkClient client, SlashCommandInteractionEvent event,
                     Long guildId, String identifier, Integer volume) {

    }
}
