package com.example.demo.controller;

import com.example.demo.pojo.Person;
import com.example.demo.pojo.Post;
import com.example.demo.pojo.UserDTO;
import com.mongodb.client.MongoCollection;
import org.springframework.beans.factory.annotation.Autowired;
import site.pushy.schlaframework.webmvc.annotation.*;
import site.pushy.schlaframework.webmvc.annotation.mapping.GET;
import site.pushy.schlaframework.webmvc.core.MongoComponent;
import com.example.demo.pojo.User;
import site.pushy.schlaframework.webmvc.core.RedisComponent;
import site.pushy.schlaframework.webmvc.enums.RequestMethod;
import site.pushy.schlaframework.webmvc.pojo.HttpRequest;
import site.pushy.schlaframework.webmvc.pojo.HttpResponse;
import site.pushy.schlaframework.webmvc.util.RespUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Pushy
 * @since 2019/3/7 12:55
 */
@Controller("/users")
public class UserController {

    @Autowired
    private MongoComponent mongoComponent;

    @Autowired
    private RedisComponent redisComponent;

    @RequestMapping("/all")
    public String index(HttpRequest request, HttpResponse response) {
        Person person = new Person();
        person.setName("Tom");
        person.setAge(34);
        mongoComponent.insertOne(person, Person.class);

        Person pushyzheng = mongoComponent.findOneByKey("name", "pushyzheng", Person.class);
        System.out.println(pushyzheng);

        mongoComponent.deleteByKey("name", "pushyzheng", Person.class);

        return RespUtil.success(mongoComponent.findAll(Person.class));
    }

    @RequestMapping("/{id}")
    public String fetchUserById(@PathVariable String id) {
        return id;
    }

    @RequestMapping("/get")
    public User getUserById() {
        User user = new User();
        user.setId(1);
        user.setName(redisComponent.get("username"));
        return user;
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
