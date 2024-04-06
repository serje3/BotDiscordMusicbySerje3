package org.serje3.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BotConfig {
    private static final String FILE_PATH = "bot.properties";
    private static Properties properties;

    public BotConfig() throws IOException {
        if (properties != null) return;

        properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(FILE_PATH)) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new IOException("Unable to find " + FILE_PATH);
            }
        }
    }

    // Метод для получения значения свойства из файла bot.properties
    public String get(String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(String key) throws IOException {
        return new BotConfig().get(key);
    }
}
