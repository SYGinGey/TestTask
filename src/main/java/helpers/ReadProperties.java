package helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ReadProperties {
    private static ReadProperties instance = null;
    private Properties properties;

    protected ReadProperties() throws IOException {
        try {
            properties = new Properties();
            properties.load(Files.newInputStream(Paths.get(System.getProperty("user.dir")
                    + "/src/main/resources/" + "application.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ReadProperties getInstance() {
        if (instance == null) {
            try {
                instance = new ReadProperties();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return instance;
    }

    public String getValue(String key) {
        return properties.getProperty(key);
    }

}
