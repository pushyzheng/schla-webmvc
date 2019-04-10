package site.pushy.schlaframework.webmvc.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import site.pushy.schlaframework.webmvc.exception.ConfigPropertiesException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Pushy
 * @since 2019/3/7 21:15
 */
public class ResourceUtil {

    public static Properties getProperties(Class<?> primarySource, String fileName) throws IOException {
        Resource resource = new UrlResource(primarySource.getClassLoader().getResource(fileName));
        Properties properties = new Properties();
        InputStream inputStream = resource.getInputStream();
        try {
            properties.load(inputStream);
            return properties;
        } catch (FileNotFoundException e) {
            throw new ConfigPropertiesException("The resource/schla-webmvc.properties file cannot be found");
        }
    }

}
