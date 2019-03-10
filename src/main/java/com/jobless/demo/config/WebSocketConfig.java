package com.jobless.demo.config;

import com.jobless.demo.handler.MyWebSocketHandler;
import com.jobless.webmvc.config.WebSocketConfigurer;
import com.jobless.webmvc.config.WebSocketHandlerRegistry;
import org.springframework.context.annotation.Configuration;

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
