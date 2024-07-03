package org.serje3.components.modals.suno;

import io.sentry.Sentry;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;
import org.serje3.meta.abs.Modal;
import org.serje3.rest.handlers.SunoRestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

public class SunoLoginModal extends Modal {
    private final String COOKIE_FIELD = "login-cookie";
    private final String SESSION_ID_FIELD = "login-session-id";
    private final SunoRestHandler sunoRestHandler = new SunoRestHandler();
    private final Logger logger = LoggerFactory.getLogger(SunoLoginModal.class);


    @Override
    public String getName() {
        return "suno-login";
    }

    @Override
    public String getTitle() {
        return "Авторизация в suno.ai";
    }

    @Override
    public void handle(ModalInteractionEvent event) {
        event.deferReply().setEphemeral(true).queue();
        String cookie = event.getValue(COOKIE_FIELD).getAsString();
        String session = event.getValue(SESSION_ID_FIELD).getAsString();

        sunoRestHandler.login(event.getUser().getIdLong(), cookie, session)
                .thenAccept((obj) -> {
                    event.getHook().sendMessage("Your auth token saved.").setEphemeral(true).queue();
                })
                .exceptionally((e) -> {
                    Sentry.captureException(e);
                    logger.error(e.getLocalizedMessage());
                    event.getHook().sendMessage("Something went wrong!").setEphemeral(true).queue();
                    return null;
                });

    }

    public net.dv8tion.jda.api.interactions.modals.Modal build() {
        TextInput cookie = TextInput.create(COOKIE_FIELD, "Cookie", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Paste here your cookies from suno.com")
                .build();
        TextInput session = TextInput.create(SESSION_ID_FIELD, "Session ID", TextInputStyle.SHORT)
                .setPlaceholder("Paste here your session id from suno.com")
                .build();
        return net.dv8tion.jda.api.interactions.modals.Modal
                .create(getModalName(), getTitle())
                .addActionRow(cookie)
                .addActionRow(session)
                .build();
    }
}
