package com.jobness.webmvc.core;

import com.jobness.webmvc.annotation.RequestMapping;
import com.jobness.webmvc.annotation.RestController;
import com.jobness.webmvc.exception.BaseException;
import com.jobness.webmvc.exception.ConfigPropertiesException;
import com.jobness.webmvc.netty.HttpServer;
import com.jobness.webmvc.util.ResourceUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

/**
 * @author Pushy
 * @since 2019/3/7 12:35
 */
public class JobnessMVCApplication {

    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_HOST = "127.0.0.1";

    private static final String PROPERTIES_FILENAME = "jobness-webmvc.properties";
    private static final String BASE_PACKAGE_KEY = "base-package";
    private static final String SERVER_HOST_KEY = "server.host";
    private static final String SERVER_PORT_KEY = "server.port";
    private static final String SERVER_DEFAULT_PATH_KEY = "server.default-path";

    public static void run() {
        try {
            Properties config = ResourceUtil.getProperties(PROPERTIES_FILENAME);
            String basePackage = config.getProperty(BASE_PACKAGE_KEY);
            if (basePackage == null) {
                throw new BaseException("Missing necessary base-package property");
            }

            int port = DEFAULT_PORT;
            if (config.getProperty(SERVER_PORT_KEY) != null) {
                port = Integer.parseInt(config.getProperty(SERVER_PORT_KEY));
            }
            String host = config.getProperty(SERVER_HOST_KEY, DEFAULT_HOST);
            String defaultPath = config.getProperty(SERVER_DEFAULT_PATH_KEY, "");

            GenericApplicationContext context = new AnnotationConfigApplicationContext(basePackage);
            // 创建 CustomAnnotationScanner 对象，通过Spring扫描自定义注解类
            CustomAnnotationScanner serviceScanner = new CustomAnnotationScanner(context);
            serviceScanner.registerTypeFilter(RestController.class);
            serviceScanner.scan(basePackage);

            handlerMapping(context);
            HttpServer.run(host, port);

        } catch (ConfigPropertiesException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handlerMapping(ApplicationContext context) {
        Map<String, Object> controllers = context.getBeansWithAnnotation(RestController.class);

        for (Map.Entry<String, Object> entry : controllers.entrySet()) {
            Object controller = entry.getValue();
            Class<?> c = controller.getClass();
            RestController restController = c.getAnnotation(RestController.class);
            if (restController != null) {
                StringBuilder url = new StringBuilder();
                url.append(restController.value());
                // 遍历该Controller类所有带 @RequestMapping 注解的视图函数
                // 并将 url -> 视图函数映射注册到视图中心类 MappingRegistry
                Method[] methods = c.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        url.append(requestMapping.value());
                        MappingRegistry.registerMapping(url.toString(), method, controller);
                        url = new StringBuilder().append(restController.value());
                    }
                }
            }
        }

        MappingRegistry.printUrlMethodMapping();
    }

    public static void main(String[] args) {
        JobnessMVCApplication.run();
    }

}
