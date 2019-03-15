package com.example.demo.controller;

import com.example.demo.pojo.Person;
import com.example.demo.pojo.UserDTO;
import com.mongodb.client.MongoCollection;
import org.springframework.beans.factory.annotation.Autowired;
import site.pushy.schlaframework.webmvc.annotation.*;
import site.pushy.schlaframework.webmvc.annotation.mapping.GET;
import site.pushy.schlaframework.webmvc.core.MongoComponent;
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

    @RequestMapping("/all")
    public String index(HttpRequest request, HttpResponse response) {
        MongoCollection<Person> collection = mongoComponent.getDB().getCollection("persons", Person.class);
        List<Person> res = new ArrayList<>();
        Iterator<Person> iterator = collection.find(Person.class).iterator();
        while (iterator.hasNext()) {
            res.add(iterator.next());
        }
        return RespUtil.success(res);
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
