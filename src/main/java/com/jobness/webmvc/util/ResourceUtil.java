package com.jobness.webmvc.util;

import com.jobness.webmvc.exception.ConfigPropertiesException;

import java.io.FileInputStream;
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

        Properties properties = new Properties();
        String path = primarySource.getClassLoader().getResource("").getPath();
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(path + "/" + fileName);
            properties.load(inputStream);
            return properties;
        } catch (FileNotFoundException e) {
            throw new ConfigPropertiesException("The resource/jobness-webmvc.properties file cannot be found");
        }
    }

}
