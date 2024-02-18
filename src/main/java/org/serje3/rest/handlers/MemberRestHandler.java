package org.serje3.rest.handlers;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.serje3.rest.base.BaseRestClient;
import org.serje3.rest.requests.SlashCommandEventRequest;
import org.serje3.rest.responses.MemberInfo;

import java.net.http.HttpResponse;
import java.util.function.Consumer;
import java.util.function.Function;

public class MemberRestHandler extends BaseRestClient {

    public void performAsyncGetMemberInfo(SlashCommandInteractionEvent event,
                                          Consumer<MemberInfo> function) {
        String id = event.getMember().getId();
        this.get("/members/" + id + "/info", MemberInfo.class)
                .thenAccept(function);
    }

    public MemberInfo performGetMemberInfo(SlashCommandInteractionEvent event) {
        String id = event.getMember().getId();
        return this.get("/members/" + id + "/info", MemberInfo.class).join();
    }
}
