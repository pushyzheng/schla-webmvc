package com.jobness.demo.controller;

import com.jobness.webmvc.annotation.RequestMapping;
import com.jobness.webmvc.annotation.RestController;
import com.jobness.webmvc.pojo.HttpRequest;
import com.jobness.webmvc.pojo.HttpResponse;

/**
 * @author Pushy
 * @since 2019/3/7 12:55
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @RequestMapping("/add")
    public String hello(HttpRequest request, HttpResponse response) {
        System.out.println(request.getUri());
        String host = request.getHeaders().get("host");
        return host;
    }

}
