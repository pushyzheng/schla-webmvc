package com.jobless.webmvc.config;

/**
 * @author Pushy
 * @since 2019/3/10 19:28
 */
public interface WebSocketConfigurer {

    void registerWebSocketHandlers(WebSocketHandlerRegistry registry);

}
