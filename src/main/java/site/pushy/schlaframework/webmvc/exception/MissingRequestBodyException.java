package site.pushy.schlaframework.webmvc.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author Pushy
 * @since 2019/3/8 9:17
 */
public class MissingRequestBodyException extends HttpBaseException {

    public MissingRequestBodyException(String message, HttpResponseStatus status) {
        super(message, status);
    }
}
