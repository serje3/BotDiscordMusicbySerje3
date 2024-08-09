package org.serje3.rest.requests;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.serje3.rest.base.RequestBody;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class SaveRecentTrackRequest extends RequestBody {
    private Long guildId;
    private String trackName;
    private String url;
}
