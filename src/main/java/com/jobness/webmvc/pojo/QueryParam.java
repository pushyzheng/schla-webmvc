package com.jobness.webmvc.pojo;

import lombok.Data;

/**
 * @author Pushy
 * @since 2019/3/7 19:44
 */
@Data
public class QueryParam {

    private String name;

    private String value;

    public QueryParam() { }

    public QueryParam(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
