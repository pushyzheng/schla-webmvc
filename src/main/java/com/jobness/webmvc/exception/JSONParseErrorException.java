package com.jobness.webmvc.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author Pushy
 * @since 2019/3/8 9:24
 */
public class JSONParseErrorException extends HttpBaseException {

    public JSONParseErrorException(String message, HttpResponseStatus status) {
        super(message, status);
    }
}
