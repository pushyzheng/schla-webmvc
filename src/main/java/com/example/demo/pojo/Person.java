package com.example.demo.pojo;

import eu.dozd.mongo.annotation.Entity;
import eu.dozd.mongo.annotation.Id;
import lombok.Data;

import java.util.List;

/**
 * @author Pushy
 * @since 2019/3/15 9:53
 */
@Entity
@Data
public class Person {

    @Id
    String id;

    String name;

    int age;

    List<Post> posts;

}
