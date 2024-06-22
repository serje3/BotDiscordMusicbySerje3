package org.serje3.config;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GuildConfig {
    private static final ConcurrentHashMap<Long, Settings> _settings = new ConcurrentHashMap<>();

    private static void init(Long guildId) {
        Set<Long> keySet = _settings.keySet();
        if (!keySet.contains(guildId)) {
            _settings.put(guildId, new Settings());
        }
    }


    public static Settings getSettings(Long guildId) {
        init(guildId);
        Settings settings = _settings.get(guildId);
        return new Settings(settings.isCockinize(),
                settings.getLastInteractionChannel(),
                settings.getLastInteractedMember());
    }

    public static boolean toggleCockinize(Long guildId) {
        init(guildId);
        Settings settings = _settings.get(guildId);
        settings.setCockinize(!settings.isCockinize());
        return settings.cockinize;
    }

    public static void setLastInteractionTextChannel(Long guildId, TextChannel channel) {
        init(guildId);

        Settings settings = _settings.get(guildId);
        settings.setLastInteractionChannel(channel);
    }

    public static void setLastInteractedMember(Long guildId, Member member) {
        init(guildId);

        Settings settings = _settings.get(guildId);
        settings.setLastInteractedMember(member);
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Settings {
        private boolean cockinize = false;
        private TextChannel lastInteractionChannel;
        private Member lastInteractedMember;
    }
}
