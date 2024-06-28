package org.serje3.rest.requests;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.serje3.rest.base.RequestBody;

@RequiredArgsConstructor
@Getter
public class SunoLoginRequest extends RequestBody {
    private final Long userId;
    private final String cookie;
    private final String session;
}
