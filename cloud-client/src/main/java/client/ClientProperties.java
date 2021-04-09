package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class ClientProperties {

    private static final Logger LOG = LoggerFactory.getLogger(ClientProperties.class);

    private static ClientProperties ourInstance = new ClientProperties();
    private Properties properties;

    public static ClientProperties getInstance() {
        return ourInstance;
    }

    //Загружаем конфигурацию
    private ClientProperties() {
        properties = new Properties();
        try {
            String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();

            String pathToProperties = rootPath + "clncfg.properties";

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
