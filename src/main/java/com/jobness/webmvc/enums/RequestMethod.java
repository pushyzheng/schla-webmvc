package com.jobness.webmvc.enums;

import io.netty.handler.codec.http.HttpMethod;

/**
 * @author Pushy
 * @since 2019/3/7 16:44
 */
public enum RequestMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH"),

    OPTIONS("OPTIONS"),
    HEAD("HEAD"),
    TRACE("TRACE"),
    CONNECT("CONNECT");

    private String value;

    RequestMethod(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    /**
     * 将Netty的HttpMethod对象转换为该枚举类
     */
    public static RequestMethod convertHttpMethod(HttpMethod httpMethod) {
        if (httpMethod.equals(HttpMethod.GET)) {
            return GET;
        } else if (httpMethod.equals(HttpMethod.POST)) {
            return POST;
        } else if (httpMethod.equals(HttpMethod.PUT)) {
            return PUT;
        } else if (httpMethod.equals(HttpMethod.DELETE)) {
            return DELETE;
        } else if (httpMethod.equals(HttpMethod.PATCH)) {
            return PATCH;
        } else if (httpMethod.equals(HttpMethod.TRACE)) {
            return TRACE;
        } else if (httpMethod.equals(HttpMethod.CONNECT)) {
            return CONNECT;
        } else if (httpMethod.equals(HttpMethod.OPTIONS)) {
            return OPTIONS;
        }
        return HEAD;
    }

}
