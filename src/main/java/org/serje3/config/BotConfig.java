package org.serje3.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BotConfig {
    protected final String DEFAULT_FILE_PATH = "bot.properties";
    private final Properties properties;

    private static BotConfig instance;

    private BotConfig() throws IOException {
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
        return get(key, null);
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static String getProperty(String key) throws IOException {
        return getInstance().get(key);
    }

    public static boolean getPropertyAsBoolean(String key) throws IOException {
        return Boolean.parseBoolean(getProperty(key));
    }


    public static BotConfig getInstance() throws IOException {
        if (instance == null) {
            instance = new BotConfig();
        }
        return instance;
    }

    private InputStream createPropertiesInputStream() throws FileNotFoundException {
        String customFilePath = System.getenv("BOT_CONFIG_PATH");
        if (customFilePath == null) {
            return getClass().getClassLoader().getResourceAsStream(DEFAULT_FILE_PATH);
        }
        return new FileInputStream(customFilePath);
    }
}
