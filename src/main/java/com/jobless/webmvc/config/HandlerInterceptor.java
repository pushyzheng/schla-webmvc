package com.jobless.webmvc.config;

import com.jobless.webmvc.pojo.HttpRequest;
import com.jobless.webmvc.pojo.HttpResponse;

/**
 * @author Pushy
 * @since 2019/3/10 10:41
 */
public interface HandlerInterceptor {

    boolean preHandle(HttpRequest request, HttpResponse response) throws Exception;

    default void postHandle(HttpRequest request, HttpResponse response) throws Exception {

    }

}
