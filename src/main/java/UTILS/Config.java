package UTILS;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties;

    static {
        properties = new Properties();
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке файла конфигурации: " + e.getMessage());
        }
    }

    public static String getApiKey() {
        return properties.getProperty("api.key");
    }

    public static String getDatabaseUrl() {
        return properties.getProperty("database.url");
    }

    public static String getShopId() {
        return properties.getProperty("api.shopid");
    }
}