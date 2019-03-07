package com.jobness.webmvc.enums;

/**
 * @author Pushy
 * @since 2019/3/7 16:44
 */
public enum RequestMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    private String value;

    RequestMethod(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
