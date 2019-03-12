package com.example.demo;

import site.pushy.schlaframework.webmvc.annotation.RequestMapping;
import site.pushy.schlaframework.webmvc.annotation.mapping.GET;

/**
 * @author Pushy
 * @since 2019/3/12 19:56
 */
public class TestController {

    @GET("/jfkajsdf")
//    @RequestMapping("/fadsfasdf")
    public String get() {
        return "";
    }

}
