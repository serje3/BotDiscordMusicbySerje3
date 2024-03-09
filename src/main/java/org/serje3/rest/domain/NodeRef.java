package org.serje3.rest.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NodeRef {
    private final Integer id;
    private final String url;
    private final String password;
    private final String region;
}
