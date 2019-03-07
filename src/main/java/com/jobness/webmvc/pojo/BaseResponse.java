package com.jobness.webmvc.pojo;

import lombok.Data;

/**
 * @author Pushy
 * @since 2019/3/6 13:14
 */
@Data
public class BaseResponse<T> {

    private T data;

    private String message;

    private int code;

    public BaseResponse(T data, String message, int code) {
        this.data = data;
        this.message = message;
        this.code = code;
    }

}
