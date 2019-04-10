package site.pushy.schlaframework.webmvc.registry;

import site.pushy.schlaframework.webmvc.enums.RequestMethod;
import io.netty.handler.codec.http.HttpMethod;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Pushy
 * @since 2019/3/7 16:50
 */
public class MappingRegistry {

    // 接口Uri -> (Http方法 -> 视图函数)
    private final static Map<String, Map<RequestMethod, Method>> urlMethodMapping = new HashMap<>();

    // 视图函数 -> 函数的类
    private final static Map<Method, Object> methodControllerMapping = new HashMap<>();

    public static void registerMapping(String url, RequestMethod requestMethod,
                                       Method method, Object controller) {
        Map<RequestMethod, Method> map = urlMethodMapping.get(url);
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(requestMethod, method);
        urlMethodMapping.put(url, map);
        methodControllerMapping.put(method, controller);
    }

    public static Method getUrlMethod(String url, HttpMethod httpMethod) {
        Map<RequestMethod, Method> map = urlMethodMapping.get(url);
        if (map == null) return null;
        else return map.get(RequestMethod.convertHttpMethod(httpMethod));
    }

    public static Object getMethodController(Method method) {
        return methodControllerMapping.get(method);
    }

    @Deprecated
    public static void printUrlMethodMapping() {
        System.out.println(urlMethodMapping);
    }

}
