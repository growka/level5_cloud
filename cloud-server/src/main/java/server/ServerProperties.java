package server;

import lombok.Getter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

public class ServerProperties {

    private static ServerProperties ourInstance = new ServerProperties();
    private Properties properties;

    public static ServerProperties getInstance() {
        return ourInstance;
    }

    //Загружаем конфигурацию сервера
    private ServerProperties() {
        properties = new Properties();
        try {
            String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();

            String pathToProperties = rootPath + "srvcfg.properties";

            FileInputStream filePropertiesInputStream = new FileInputStream(pathToProperties);
            properties.load(filePropertiesInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperties(String key) {
        return properties.getProperty(key);
    }


}
