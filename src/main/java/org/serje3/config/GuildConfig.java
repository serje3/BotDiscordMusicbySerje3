package org.serje3.config;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GuildConfig {
    private static final ConcurrentHashMap<Long, Settings> _settings = new ConcurrentHashMap<>();

    private static void init(Long guildId) {
        Set<Long> keySet = _settings.keySet();
        if (!keySet.contains(guildId)) {
            _settings.put(guildId, new Settings(false));
        }
    }


    public static Settings getSettings(Long guildId) {
        init(guildId);

        return _settings.get(guildId);
    }

    public static boolean toggleCockinize(Long guildId){
        init(guildId);
        Settings settings = _settings.get(guildId);
        settings.setCockinize(!settings.isCockinize());
        return settings.cockinize;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Settings {
        private boolean cockinize;
    }
}
