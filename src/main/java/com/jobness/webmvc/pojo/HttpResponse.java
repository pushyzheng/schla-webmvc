package com.jobness.webmvc.pojo;

import com.jobness.webmvc.enums.ContentType;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Data;

/**
 * @author Pushy
 * @since 2019/3/7 17:17
 */
@Data
public class HttpResponse {

    private HttpResponseStatus status;

    private ContentType contentType;

    public HttpResponse() {
        status = HttpResponseStatus.OK;
        contentType = ContentType.JSON;
    }
}
