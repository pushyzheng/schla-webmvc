package com.jobness.webmvc.handler;

import com.jobness.webmvc.exception.BaseException;
import com.jobness.webmvc.util.ResourceUtil;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Pushy
 * @since 2019/3/8 8:52
 */
public class PropertiesConfigHandler {

    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_HOST = "127.0.0.1";

    private static final String PROPERTIES_FILENAME = "jobness-webmvc.properties";
    private static final String BASE_PACKAGE_KEY = "base-package";
    private static final String SERVER_HOST_KEY = "server.host";
    private static final String SERVER_PORT_KEY = "server.port";
    private static final String SERVER_DEFAULT_PATH_KEY = "server.default-path";
    private static final String DATASOURCE_URL = "datasource.url";
    private static final String DATASOURCE_USERNAME = "datasource.name";
    private static final String DATASOURCE_PASSWORD = "datasource.password";

    private int port;
    private String host;
    private String defaultPath;
    private String basePackage;
    // 数据源配置参数
    private String datasourceUrl;
    private String datasourceUsername;
    private String datasourcePassword;

    public void read(Class<?> primarySource) throws IOException {
        Properties config = ResourceUtil.getProperties(primarySource, PROPERTIES_FILENAME);
        basePackage = config.getProperty(BASE_PACKAGE_KEY);
        if (basePackage == null) {
            throw new BaseException("Missing necessary base-package property");
        }

        if (config.getProperty(SERVER_PORT_KEY) != null) {
            port = Integer.parseInt(config.getProperty(SERVER_PORT_KEY));
        } else {
            port = DEFAULT_PORT;
        }
        host = config.getProperty(SERVER_HOST_KEY, DEFAULT_HOST);
        defaultPath = config.getProperty(SERVER_DEFAULT_PATH_KEY, "");

        // 获取数据源
        datasourceUrl = config.getProperty(DATASOURCE_URL);
        datasourceUsername = config.getProperty(DATASOURCE_USERNAME);
        datasourcePassword = config.getProperty(DATASOURCE_PASSWORD);
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getDefaultPath() {
        return defaultPath;
    }

    public String getBasePackage() {
        return basePackage;
    }
}
