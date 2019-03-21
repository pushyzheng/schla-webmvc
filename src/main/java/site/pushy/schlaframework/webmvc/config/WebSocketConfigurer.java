package site.pushy.schlaframework.webmvc.config;

import site.pushy.schlaframework.webmvc.registry.WebSocketHandlerRegistry;

/**
 * @author Pushy
 * @since 2019/3/10 19:28
 */
public interface WebSocketConfigurer {

    void registerWebSocketHandlers(WebSocketHandlerRegistry registry);

}
