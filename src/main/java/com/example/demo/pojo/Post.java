package com.example.demo.pojo;

import eu.dozd.mongo.annotation.Entity;
import eu.dozd.mongo.annotation.Id;
import lombok.Data;

/**
 * @author Pushy
 * @since 2019/3/15 9:53
 */
@Entity
@Data
public class Post {

    @Id
    String id;

    String title;

    String content;

    public Post() {
    }

    public Post(String title) {
        this.title = title;
    }
}
