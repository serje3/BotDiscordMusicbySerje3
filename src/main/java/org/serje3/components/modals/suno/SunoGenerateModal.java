package org.serje3.components.modals.suno;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.serje3.meta.abs.Modal;
import org.serje3.rest.handlers.SunoRestHandler;
import org.serje3.rest.requests.SunoGenerateRequest;

public class SunoGenerateModal extends Modal {
    private final String PROMPT_INPUT = "prompt";
    private final String TITLE_INPUT = "title";
    private final String GENRE_INPUT = "genre";
    private final SunoRestHandler sunoRestHandler = new SunoRestHandler();

    @Override
    public String getName() {
        return "suno-generate-modal";
    }

    @Override
    public String getTitle() {
        return "Генерация трека";
    }

    @Override
    public void handle(ModalInteractionEvent event) {
        event.deferReply().queue();
        String prompt = event.getValue(PROMPT_INPUT).getAsString();
        String title = event.getValue(TITLE_INPUT).getAsString();
        String genre = event.getValue(GENRE_INPUT).getAsString();

        sunoRestHandler.generate(event.getUser().getIdLong(), new SunoGenerateRequest(prompt, "chirp-v3.5", title, genre))
                .thenAccept((res) -> {
                    event.getHook().sendMessage("Генерация запущена. Ваша мать попущена").queue();
                })
                .exceptionally(e -> {
                    event.getHook().sendMessage("Ошибка: " + e.getLocalizedMessage()).queue();
                    return null;
                });
    }

    @Override
    public net.dv8tion.jda.api.interactions.modals.Modal build() {
        TextInput prompt = TextInput
                .create(PROMPT_INPUT, "Lyrics", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Введите строки жёские")
                .build();
        TextInput title = TextInput
                .create(TITLE_INPUT, "Title", TextInputStyle.SHORT)
                .setPlaceholder("Введите названия трека ебана")
                .build();

        TextInput genre = TextInput
                .create(GENRE_INPUT, "Genre", TextInputStyle.SHORT)
                .setPlaceholder("Жанры хуйни")
                .build();

        return net.dv8tion.jda.api.interactions.modals.Modal.create(getModalName(), getTitle())
                .addActionRow(prompt)
                .addActionRow(title)
                .addActionRow(genre)
                .build();
    }
}
