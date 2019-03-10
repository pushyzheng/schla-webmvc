package com.jobness.webmvc.handler;

import com.jobness.webmvc.annotation.RequestMapping;
import com.jobness.webmvc.annotation.RestController;
import com.jobness.webmvc.core.MappingRegistry;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Pushy
 * @since 2019/3/8 9:04
 */
public class MappingHandler {

    private ApplicationContext context;

    public MappingHandler(ApplicationContext context) {
        this.context = context;
    }

    public void doHandle() {
        handlerRestController();
    }

    private void handlerRestController() {
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
                        MappingRegistry.registerMapping(url.toString(), requestMapping.method(),
                                method, controller);
                        url = new StringBuilder().append(restController.value());
                    }
                }
            }
        }

        MappingRegistry.printUrlMethodMapping();
    }

}
