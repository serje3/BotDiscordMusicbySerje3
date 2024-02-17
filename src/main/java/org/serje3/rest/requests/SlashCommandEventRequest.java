package org.serje3.rest.requests;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.serje3.rest.base.RequestBody;
import org.serje3.rest.domain.Guild;
import org.serje3.rest.domain.Member;


@Getter
@RequiredArgsConstructor
public class SlashCommandEventRequest extends RequestBody {
    private final String command;
    private final String fullCommand;
    private final Member member;
    private final Guild guild;
}
