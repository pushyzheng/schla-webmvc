package site.pushy.schlaframework.webmvc.config;

import site.pushy.schlaframework.webmvc.pojo.WebSocketSession;

/**
 * @author Pushy
 * @since 2019/3/10 19:28
 */
public interface WebSocketHandler {

    void afterConnectionEstablished(WebSocketSession session);

    void processMessage(WebSocketSession session, String message);

    void afterConnectionCloses(WebSocketSession session);

}
