package site.pushy.schlaframework.webmvc.config;

import site.pushy.schlaframework.webmvc.pojo.HttpRequest;
import site.pushy.schlaframework.webmvc.pojo.WebSocketSession;

/**
 * @author Pushy
 * @since 2019/3/21 11:12
 */
public interface HandshakeInterceptor {

    boolean beforeHandshake(HttpRequest request, WebSocketSession session) throws Exception;

}
