package com.jobness.webmvc.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Pushy
 * @since 2019/3/7 16:50
 */
public class MappingRegistry {

    private static Map<String, Method> urlMethodMapping;

    private static Map<Method, Object> methodControllerMapping;

    static {
        urlMethodMapping = new HashMap<>();
        methodControllerMapping = new HashMap<>();
    }

    public static void registerMapping(String url, Method method, Object controller) {
        urlMethodMapping.put(url, method);
        methodControllerMapping.put(method, controller);
    }

    public static Method getUrlMethod(String url) {
        return urlMethodMapping.get(url);
    }

    public static Object getMethodController(Method method) {
        return methodControllerMapping.get(method);
    }

    @Deprecated
    public static void printUrlMethodMapping() {
        System.out.println(urlMethodMapping);
    }

}
