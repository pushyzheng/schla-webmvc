package com.jobness.webmvc.util;

import com.jobness.webmvc.exception.ConfigPropertiesException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Pushy
 * @since 2019/3/7 21:15
 */
public class ResourceUtil {

    public static Properties getProperties(String fileName) throws IOException, ConfigPropertiesException {
        Properties properties = new Properties();
        InputStream inputStream = ResourceUtil.class.getResourceAsStream("/" + fileName);
        if (inputStream == null) {
            throw new ConfigPropertiesException("The resource/jobness-webmvc.properties file cannot be found");
        }
        properties.load(inputStream);
        return properties;
    }

}
