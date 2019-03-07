package com.jobness.demo.controller;

import com.jobness.demo.pojo.UserDTO;
import com.jobness.demo.service.UserService;
import com.jobness.webmvc.annotation.QueryString;
import com.jobness.webmvc.annotation.RequestBody;
import com.jobness.webmvc.annotation.RequestMapping;
import com.jobness.webmvc.annotation.RestController;
import com.jobness.webmvc.enums.RequestMethod;
import com.jobness.webmvc.pojo.HttpRequest;
import com.jobness.webmvc.pojo.HttpResponse;
import com.jobness.webmvc.util.RespUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Pushy
 * @since 2019/3/7 12:55
 */
@RestController("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("")
    public String index(HttpRequest request, HttpResponse response) {
        return RespUtil.success(request.getUri() + " => " + request.getMethod());
    }

    @RequestMapping("/get")
    public int getUserById(@QueryString(required = true) int id) {
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
