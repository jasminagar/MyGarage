package app.config;

import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    public AppConfig() {
    }

    public String getSetting(String key) {
        if (System.getenv("DEPLOYED") != null) {
            String value = System.getenv(key);

            if (value == null || value.isBlank()) {
                throw new IllegalStateException(
                        "Missing environment variable: " + key
                );
            }

            return value;
        }

        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new IllegalStateException(
                        "app.config.properties was not found"
                );
            }

            Properties properties = new Properties();
            properties.load(input);

            String value = properties.getProperty(key);

            if (value == null || value.isBlank()) {
                throw new IllegalStateException(
                        "Missing setting in app.config.properties: " + key
                );
            }

            return value;

        } catch (java.io.IOException e) {
            throw new IllegalStateException(
                    "Could not read app.config.properties", e
            );
        }
    }
}
