package app.config;

import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    private final Properties properties = new Properties();
    private final boolean deployed;

    public AppConfig() {
        this.deployed = System.getenv("DEPLOYED") != null;

        if (!deployed) {
            try (InputStream input = getClass()
                    .getClassLoader()
                    .getResourceAsStream("config.properties")) {

                if (input == null) {
                    throw new IllegalStateException(
                            "config.properties was not found"
                    );
                }

                properties.load(input);

            } catch (java.io.IOException e) {
                throw new IllegalStateException(
                        "Could not read config.properties", e
                );
            }
        }
    }

    public String getSetting(String key) {
        String value = deployed
                ? System.getenv(key)
                : properties.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing configuration setting: " + key
            );
        }

        return value;
    }
}