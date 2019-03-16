package site.pushy.schlaframework.webmvc.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author Pushy
 * @since 2019/3/16 16:47
 */
public class MissingBodyFieldException extends HttpBaseException {

    public MissingBodyFieldException(String message, HttpResponseStatus status) {
        super(message, status);
    }
}
