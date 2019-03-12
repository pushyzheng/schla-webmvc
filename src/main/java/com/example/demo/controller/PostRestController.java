package com.example.demo.controller;

import com.example.demo.pojo.UserDTO;
import site.pushy.schlaframework.webmvc.annotation.RequestBody;
import site.pushy.schlaframework.webmvc.annotation.RestController;
import site.pushy.schlaframework.webmvc.enums.ContentType;

/**
 * @author Pushy
 * @since 2019/3/12 19:17
 */
@RestController(value = "/posts", contentType = ContentType.JSON)
public class PostRestController {

    public String get() {
        return "PostRestController::get";
    }

    public String post(@RequestBody UserDTO userDTO) {
        return "PostRestController::post";
    }

    public String put(@RequestBody UserDTO userDTO) {
        return "PostRestController::put";
    }

    public String delete(@RequestBody UserDTO userDTO) {
        return "PostRestController::delete";
    }

}
