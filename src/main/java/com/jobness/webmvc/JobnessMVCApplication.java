package com.jobness.webmvc;

import com.jobness.webmvc.annotation.RequestMapping;
import com.jobness.webmvc.annotation.RestController;
import com.jobness.webmvc.netty.HttpServer;
import io.netty.util.Mapping;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Pushy
 * @since 2019/3/7 12:35
 */
public class JobnessMVCApplication {

    public static void run() {
        // Todo 读取配置文件，例如属性 port/base-package/default-path

        String basePackage = "com.jobness.demo";

        GenericApplicationContext context = new AnnotationConfigApplicationContext(basePackage);

        // 创建 CustomAnnotationScanner 对象，通过Spring扫描自定义注解类
        CustomAnnotationScanner serviceScanner = new CustomAnnotationScanner(context);
        serviceScanner.registerTypeFilter(RestController.class);
        serviceScanner.scan(basePackage);

        handlerMapping(context);

        HttpServer.run("127.0.0.1", 80);
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
