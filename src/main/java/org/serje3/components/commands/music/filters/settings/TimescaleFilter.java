package org.serje3.components.commands.music.filters.settings;

import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.LavalinkPlayer;
import dev.arbjerg.lavalink.client.player.PlayerUpdateBuilder;
import dev.arbjerg.lavalink.protocol.v4.Filters;
import dev.arbjerg.lavalink.protocol.v4.Omissible;
import dev.arbjerg.lavalink.protocol.v4.Timescale;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.meta.interfaces.CommandExecutable;
import org.serje3.services.LavalinkService;

import java.util.Objects;
import java.util.Optional;

public class TimescaleFilter implements CommandExecutable {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // 3 params: speed?: str, pitch?: str, rate?: str

        event.deferReply().queue();

        TimescaleEventParams params = getTimescaleEventParams(event);

        Link link = LavalinkService.getInstance().getLink(event.getGuild().getIdLong());

        link
                .getPlayer()
                .flatMap(player -> setTimescale(params, player))
                .subscribe(player -> event.getHook().sendMessage(params.toString()).queue());
    }


    private PlayerUpdateBuilder setTimescale(TimescaleEventParams params, LavalinkPlayer player) {
        Filters currentFilters = player.getFilters();

        Timescale updatedTimescale = getTimescale(params);
        Filters updatedFilters = new Filters(
                currentFilters.getVolume(),
                currentFilters.getEqualizer(),
                currentFilters.getKaraoke(),
                Omissible.Companion.omittedIfNull(updatedTimescale),
                currentFilters.getTremolo(),
                currentFilters.getVibrato(),
                currentFilters.getDistortion(),
                currentFilters.getRotation(),
                currentFilters.getChannelMix(),
                currentFilters.getLowPass(),
                currentFilters.getPluginFilters()
        );

        return player.setFilters(updatedFilters);
    }

    private Timescale getTimescale(TimescaleEventParams params) {
        return new Timescale(
                params.speed.orElse(1.0),
                params.pitch.orElse(1.0),
                params.rate.orElse(1.0)
        );
    }

    private TimescaleEventParams getTimescaleEventParams(SlashCommandInteractionEvent event) {
        Optional<Double> speed = Optional.ofNullable(getParam("speed", event));
        Optional<Double> pitch = Optional.ofNullable(getParam("pitch", event));
        Optional<Double> rate = Optional.ofNullable(getParam("rate", event));
        return new TimescaleEventParams(speed, pitch, rate);
    }

    private Double getParam(String name, SlashCommandInteractionEvent event) {
        Double param;
        try {
            param = Math.max(Objects.requireNonNull(event.getOption(name)).getAsDouble(), 0.1);
        } catch (NullPointerException | IllegalStateException e) {
            param = null;
        }
        return param;
    }


    record TimescaleEventParams(Optional<Double> speed, Optional<Double> pitch, Optional<Double> rate) {
        @Override
        public String toString() {
            return "TimescaleEventParams{" +
                    "speed=" + speed +
                    ", pitch=" + pitch +
                    ", rate=" + rate +
                    '}';
        }
    }
}
