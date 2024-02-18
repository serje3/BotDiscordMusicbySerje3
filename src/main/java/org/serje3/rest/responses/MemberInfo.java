package org.serje3.rest.responses;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
