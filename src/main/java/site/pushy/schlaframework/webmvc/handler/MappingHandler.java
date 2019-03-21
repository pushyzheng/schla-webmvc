package site.pushy.schlaframework.webmvc.handler;

import site.pushy.schlaframework.webmvc.annotation.RequestMapping;
import site.pushy.schlaframework.webmvc.annotation.Controller;
import site.pushy.schlaframework.webmvc.annotation.RestController;
import site.pushy.schlaframework.webmvc.annotation.mapping.*;
import site.pushy.schlaframework.webmvc.registry.MappingRegistry;
import org.springframework.context.ApplicationContext;
import site.pushy.schlaframework.webmvc.enums.RequestMethod;

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
        handleController();
        handleRestController();
    }

    private void handleController() {
        Map<String, Object> controllers = context.getBeansWithAnnotation(Controller.class);

        for (Map.Entry<String, Object> entry : controllers.entrySet()) {
            Object controller = entry.getValue();
            Class<?> c = controller.getClass();
            Controller controllerAnno = c.getAnnotation(Controller.class);
            if (controllerAnno != null) {
                StringBuilder url = new StringBuilder();
                url.append(controllerAnno.value());
                // 遍历该Controller类所有带 @RequestMapping 注解的视图函数
                // 并将 url -> 视图函数映射注册到视图中心类 MappingRegistry
                Method[] methods = c.getMethods();
                for (Method method : methods) {
                    boolean res = registerViewMethod(method, url, controller, controllerAnno);
                    if (res) {
                        url = new StringBuilder(controllerAnno.value());
                    }
                }
            }
        }

//        MappingRegistry.printUrlMethodMapping();
    }

    private void handleRestController() {
        Map<String, Object> restControllers = context.getBeansWithAnnotation(RestController.class);
        for (Map.Entry<String, Object> entry : restControllers.entrySet()) {
            Object controller = entry.getValue();
            Class<?> c = controller.getClass();
            RestController controllerAnno = c.getAnnotation(RestController.class);
            if (controllerAnno != null) {
                String restfulUri = controllerAnno.value();
                Method[] methods = c.getMethods();
                for (Method method : methods) {
                    if (method.getName().equalsIgnoreCase(RequestMethod.GET.value())) {
                        MappingRegistry.registerMapping(restfulUri, RequestMethod.GET, method, controller);
                    }
                    else if (method.getName().equalsIgnoreCase(RequestMethod.POST.value())) {
                        MappingRegistry.registerMapping(restfulUri, RequestMethod.POST, method, controller);
                    }
                    else if (method.getName().equalsIgnoreCase(RequestMethod.DELETE.value())) {
                        MappingRegistry.registerMapping(restfulUri, RequestMethod.DELETE, method, controller);
                    }
                    else if (method.getName().equalsIgnoreCase(RequestMethod.PUT.value())) {
                        MappingRegistry.registerMapping(restfulUri, RequestMethod.PUT, method, controller);
                    }
                    else if (method.getName().equalsIgnoreCase(RequestMethod.PATCH.value())) {
                        MappingRegistry.registerMapping(restfulUri, RequestMethod.PATCH, method, controller);
                    }
                }
            }
        }
    }

    private boolean registerViewMethod(Method method, StringBuilder url,
                                    Object controller, Controller controllerAnno) {
        RequestMethod reqMethod;
        String value;
        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            reqMethod = requestMapping.method();
            value = requestMapping.value();
        }
        else if (method.isAnnotationPresent(GET.class)) {
            value = method.getAnnotation(GET.class).value();
            reqMethod = RequestMethod.GET;
        }
        else if (method.isAnnotationPresent(POST.class)) {
            value = method.getAnnotation(POST.class).value();
            reqMethod = RequestMethod.POST;
        }
        else if (method.isAnnotationPresent(DELETE.class)) {
            value = method.getAnnotation(DELETE.class).value();
            reqMethod = RequestMethod.DELETE;
        }
        else if (method.isAnnotationPresent(PUT.class)) {
            value = method.getAnnotation(PUT.class).value();
            reqMethod = RequestMethod.PUT;
        }
        else if (method.isAnnotationPresent(PATCH.class)) {
            value = method.getAnnotation(PATCH.class).value();
            reqMethod = RequestMethod.PATCH;
        }
        else {
            return false;
        }
        url.append(value);
        MappingRegistry.registerMapping(url.toString(), reqMethod,
                method, controller);
        return true;
    }

}
