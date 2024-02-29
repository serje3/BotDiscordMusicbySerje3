package org.serje3.components.commands.music.filters;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.protocol.v4.Band;
import dev.arbjerg.lavalink.protocol.v4.Filters;
import dev.arbjerg.lavalink.protocol.v4.Omissible;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.serje3.meta.abs.Command;
import org.serje3.meta.annotations.JoinVoiceChannel;

import java.util.List;

public class NormalizeFilterCommand extends Command {
    @Override
    public String getName() {
        return "normalize";
    }

    @Override
    public String getDescription() {
        return "Вернуть звук как было";
    }

    @JoinVoiceChannel
    @Override
    public void execute(SlashCommandInteractionEvent event, LavalinkClient client) {
        Omissible<List<Band>> equalizer = Omissible.Companion.omittedIfNull(getEqualizer());

        Filters defaultFilters = new Filters();
        Filters filters = new Filters(
                defaultFilters.getVolume(),
                equalizer,
                defaultFilters.getKaraoke(),
                defaultFilters.getTimescale(),
                defaultFilters.getTremolo(),
                defaultFilters.getVibrato(),
                defaultFilters.getDistortion(),
                defaultFilters.getRotation(),
                defaultFilters.getChannelMix(),
                defaultFilters.getLowPass(),
                defaultFilters.getPluginFilters());
        System.out.println(filters.getVolume() + " " + filters.getEqualizer());
        client.getLink(event.getGuild().getIdLong()).createOrUpdatePlayer()
                .setFilters(filters)
                .subscribe(p -> {
                    System.out.println(p);
                    event.reply(getReplyString()).queue();
                });
    }

    protected String getReplyString() {
        return "Теперь все фильтры в аудио должны съебать";
    }

    protected List<Band> getEqualizer() {
        return null;
    }
}
