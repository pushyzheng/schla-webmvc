package com.jobless.demo.controller;

import com.jobless.demo.pojo.UserDTO;
import com.jobless.demo.service.UserService;
import com.jobless.webmvc.annotation.*;
import com.jobless.webmvc.enums.RequestMethod;
import com.jobless.webmvc.pojo.HttpRequest;
import com.jobless.webmvc.pojo.HttpResponse;
import com.jobless.webmvc.util.RespUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Pushy
 * @since 2019/3/7 12:55
 */
@RestController("/users")
public class UserController {

    @RequestMapping("")
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
