package com.jobless.webmvc.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author Pushy
 * @since 2019/3/7 20:40
 */
public class MissingQueryStringException extends HttpBaseException {

    public MissingQueryStringException(String message, HttpResponseStatus status) {
        super(message, status);
    }
}
