package ee.smkv.executor.remote;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    public final static Properties PROPERTIES = new Properties();
    public static final String ENVIRONMENT = "environment";

    static {
        loadProperties();
    }

    private static void loadProperties() {
        Properties configProperties = readPropertiesFromClassPathFile("config.properties");
        Properties environmentProperties = readPropertiesFromClassPathFile(configProperties.getProperty(ENVIRONMENT) + File.separator + "environment.properties");
        Properties systemProperties = System.getProperties();

        PROPERTIES.putAll(systemProperties);
        PROPERTIES.putAll(environmentProperties);
        PROPERTIES.putAll(configProperties);
    }

    private static Properties readPropertiesFromClassPathFile(String path) {
        Properties properties = new Properties();
        ClassPathResource classPathResource = new ClassPathResource(path);

        try (InputStream inputStream = classPathResource.getInputStream()) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Unable to read property file %s from class path: %s", path, e.getMessage()), e);
        }
        return properties;
    }

    public static String getProperty(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    public static String getProperty(String key) {
        return getProperty(key, null);
    }
}
