package org.serje3.components.commands.suno.handlers;
import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.components.buttons.music.AddToQueueButton;
import org.serje3.components.commands.music.queue.QueueCommand;
import org.serje3.meta.interfaces.CommandExecutable;
import org.serje3.rest.requests.SaveRecentTrackRequest;
import org.serje3.services.EmbedService;
import org.serje3.services.MusicService;
import org.serje3.utils.VoiceHelper;

public class Id implements CommandExecutable {
    private final MusicService musicService = new MusicService();

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        String id = event.getOption("id").getAsString();
        VoiceHelper.joinMemberVoiceChannel(event);
        String url = "https://audiopipe.suno.ai/?item_id="+id;

        new QueueCommand().play(event, event.getGuild().getIdLong(), url, 35, ignored -> {
            event.getHook().sendMessageEmbeds(
                    EmbedService.getInstance().getMessageTrackEmbed(
                            url,
                            "Да мне похуй",
                            "Жанр: " + "Да мне похуй",
                            "suno.com",
                            null,
                            event.getMember(),
                            event.getMember().getEffectiveName(),
                            null
                    )
            ).addActionRow(new AddToQueueButton().asJDAButton()).queue();
        });
        musicService.saveRecentTrack(SaveRecentTrackRequest.builder()
                .trackName(id)
                .url(url)
                .guildId(event.getGuild().getIdLong())
                .build() );
    }
}
