package com.jobness.demo.controller;

import com.jobness.demo.service.UserService;
import com.jobness.webmvc.annotation.RequestMapping;
import com.jobness.webmvc.annotation.RestController;
import com.jobness.webmvc.pojo.HttpRequest;
import com.jobness.webmvc.pojo.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Pushy
 * @since 2019/3/7 12:55
 */
@RestController("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/add")
    public String hello(HttpRequest request, HttpResponse response) {
        String host = request.getHeaders().get("host");
        String name = userService.getName();
        System.out.println(name);
        System.out.println(request.getUri());
        return host;
    }

    @RequestMapping("/remove")
    public String remove() {
        return "remove succeed";
    }
}
