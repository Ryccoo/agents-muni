package config;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by tulak on 13.05.2014.
 */
public class IsisConfig {
    static Properties instance;

    public static Properties getInstance() {
        if (instance == null) {
            instance = new Properties();
            try {
                instance.load(IsisConfig.class.getClassLoader().getResourceAsStream("config.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public static String getProperty(String key) {
        return getInstance().getProperty(key);
    }

}
