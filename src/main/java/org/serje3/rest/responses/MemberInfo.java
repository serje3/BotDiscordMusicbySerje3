package org.serje3.rest.responses;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.serje3.rest.base.ResponseBody;

import java.net.http.HttpResponse;

@RequiredArgsConstructor
@Getter
public class MemberInfo {
    private final Integer countExecutedCommands;
    private final Rating rating;

    @RequiredArgsConstructor
    @Getter
    public static class Rating {
        private final Integer rank;
        private final Long senderId;
        private final Integer count;
    }
}
