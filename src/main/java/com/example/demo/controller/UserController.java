package com.example.demo.controller;

import com.example.demo.pojo.UserDTO;
import site.pushy.schlaframework.webmvc.annotation.*;
import site.pushy.schlaframework.webmvc.annotation.mapping.GET;
import site.pushy.schlaframework.webmvc.enums.RequestMethod;
import site.pushy.schlaframework.webmvc.pojo.HttpRequest;
import site.pushy.schlaframework.webmvc.pojo.HttpResponse;
import site.pushy.schlaframework.webmvc.util.RespUtil;

/**
 * @author Pushy
 * @since 2019/3/7 12:55
 */
@Controller("/users")
public class UserController {

    @GET("")
    public String index(HttpRequest request, HttpResponse response) {
        return RespUtil.success(request.getUri() + " => " + request.getMethod());
    }

    @RequestMapping("/{id}")
    public String fetchUserById(@PathVariable String id) {
        return id;
    }

    @RequestMapping("/get")
    public int getUserById(@QueryString int id) {
        return id;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addUser(@RequestBody UserDTO userDTO) {
        return RespUtil.success(userDTO);
    }

    @RequestMapping("/remove")
    public String remove() {
        return "remove succeed";
    }

}
