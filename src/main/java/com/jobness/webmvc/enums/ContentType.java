package com.jobness.webmvc.enums;

/**
 * @author Pushy
 * @since 2019/3/7 17:44
 */
public enum ContentType {

    JSON("application/json; charset=UTF-8"),
    HTML("text/html"),
    PLAIN("text/plain");

    private String value;

    ContentType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
