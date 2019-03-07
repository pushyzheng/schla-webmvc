package com.jobness.webmvc.pojo;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Data;

/**
 * @author Pushy
 * @since 2019/3/7 17:17
 */
@Data
public class HttpResponse {

    private HttpResponseStatus status;

    private String contentType;

    public HttpResponse() {
        status = HttpResponseStatus.OK;
    }
}
