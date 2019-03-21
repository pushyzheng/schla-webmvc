package site.pushy.schlaframework.webmvc.pojo;

import io.netty.handler.codec.http.FullHttpRequest;
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

    public HttpRequest() {
    }

    /**
     * 获取封装的HttpRequest对象
     * 因为不能将Netty内置的 FullHttpRequest 暴露给客户使用
     */
    public HttpRequest(FullHttpRequest request) {
        RequestMethod requestMethod = RequestMethod.convertHttpMethod(request.method());
        setMethod(requestMethod);
        setUri(request.uri());
        setVersion(request.protocolVersion().toString());
        setHeaders(request.headers());
    }

}
