package org.serje3.rest.requests;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.serje3.rest.base.RequestBody;

@Getter
@RequiredArgsConstructor
public class SunoGenerateRequest extends RequestBody {
    private final String prompt;
    private final String mv;
    private final String title;
    private final String tags;
}
