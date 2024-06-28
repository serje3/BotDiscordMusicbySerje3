package org.serje3.rest.responses;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.serje3.rest.base.RequestBody;
import org.serje3.rest.domain.SunoClip;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class SunoGenerateResponse{
    private final String id;
    private final List<SunoClip> clips;
}
