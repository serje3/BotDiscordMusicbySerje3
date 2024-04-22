package org.serje3.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BotConfig {
    private static final String DEFAULT_FILE_PATH = "bot.properties";
    private static Properties properties;

    public BotConfig() throws IOException {
        if (properties != null) return;


        properties = new Properties();
        try (InputStream inputStream = createPropertiesInputStream()) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new IOException("Unable to find properties file");
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

    private InputStream createPropertiesInputStream() throws FileNotFoundException {
        String customFilePath = System.getenv("BOT_CONFIG_PATH");
        if (customFilePath == null) {
            return getClass().getClassLoader().getResourceAsStream(DEFAULT_FILE_PATH);
        }
        return new FileInputStream(customFilePath);
    }
}
