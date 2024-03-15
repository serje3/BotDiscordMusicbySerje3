package org.serje3.rest.requests;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.serje3.rest.base.RequestBody;

@RequiredArgsConstructor
@Getter
public class DickRequest extends RequestBody {
    private final String name;
}
