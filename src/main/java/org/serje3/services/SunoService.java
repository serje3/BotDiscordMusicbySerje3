package org.serje3.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@RequiredArgsConstructor
@Getter
public class SunoService {

    public Integer handlePageOptionMapping(OptionMapping pageOption) {
        int page;
        if (pageOption != null) {
            page = pageOption.getAsInt();
            if (page < 0) {
                throw new IllegalArgumentException("Page must be a positive integer");
            }
        } else {
            page = 0;
        }
        return page;
    }
}
