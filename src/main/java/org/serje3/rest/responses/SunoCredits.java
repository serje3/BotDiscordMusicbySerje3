package org.serje3.rest.responses;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SunoCredits {
    private final Integer creditsLeft;
    private final Integer period;
    private final Integer monthlyLimit;
    private final Integer monthlyUsage;
}
