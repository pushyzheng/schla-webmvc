package com.jobness.webmvc.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author Pushy
 * @since 2019/3/7 20:40
 */
public class MissQueryStringException extends HttpBaseException {

    public MissQueryStringException(String message, HttpResponseStatus status) {
        super(message, status);
    }
}
