package com.jobness.webmvc.config;

import com.jobness.webmvc.pojo.HttpRequest;
import com.jobness.webmvc.pojo.HttpResponse;

/**
 * @author Pushy
 * @since 2019/3/10 10:41
 */
public interface HandlerInterceptor {

    boolean preHandle(HttpRequest request, HttpResponse response) throws Exception;

    default void postHandle(HttpRequest request, HttpResponse response) throws Exception {

    }

}
