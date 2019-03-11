package com.example.demo.config;

import com.example.demo.handler.MyWebSocketHandler;
import site.pushy.schlaframework.webmvc.config.WebSocketConfigurer;
import site.pushy.schlaframework.webmvc.config.WebSocketHandlerRegistry;

/**
 * @author Pushy
 * @since 2019/3/10 19:33
 */
//@Configuration
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.setHandler(new MyWebSocketHandler(), "/ws");
    }
}
