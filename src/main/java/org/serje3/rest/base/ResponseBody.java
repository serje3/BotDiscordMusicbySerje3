package org.serje3.rest.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResponseBody<T> {
    private final T body;
}
