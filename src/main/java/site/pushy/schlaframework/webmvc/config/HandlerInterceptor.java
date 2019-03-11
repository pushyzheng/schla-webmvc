package site.pushy.schlaframework.webmvc.config;

import site.pushy.schlaframework.webmvc.pojo.HttpRequest;
import site.pushy.schlaframework.webmvc.pojo.HttpResponse;

/**
 * @author Pushy
 * @since 2019/3/10 10:41
 */
public interface HandlerInterceptor {

    boolean preHandle(HttpRequest request, HttpResponse response) throws Exception;

    default void postHandle(HttpRequest request, HttpResponse response) throws Exception {

    }

}
