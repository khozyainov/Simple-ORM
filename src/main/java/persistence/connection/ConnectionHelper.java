package persistence.connection;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by entony on 26.12.2017.
 */
public class ConnectionHelper {
    private static final String PROPERTY_FILE = "db-connection.properties";
    private static Properties properties;

    private ConnectionHelper(){}

    public static Properties getConnectionProperties(){
        if (properties == null){
            properties = new Properties();

            ClassLoader classLoader = ConnectionHelper.class.getClassLoader();
            try{
                properties.load(classLoader.getResourceAsStream(PROPERTY_FILE));
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        }
        return properties;
    }
}
