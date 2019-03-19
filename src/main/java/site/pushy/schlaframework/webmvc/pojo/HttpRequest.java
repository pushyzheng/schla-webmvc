package site.pushy.schlaframework.webmvc.pojo;

import site.pushy.schlaframework.webmvc.enums.RequestMethod;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.Data;

/**
 * @author Pushy
 * @since 2019/3/7 17:17
 */
@Data
public class HttpRequest {

    private String uri;

    private RequestMethod method;

    private String version;

    private HttpHeaders headers;

    private HttpSession session;

}
