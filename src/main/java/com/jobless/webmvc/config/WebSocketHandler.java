package com.jobless.webmvc.config;

import com.jobless.webmvc.pojo.WebSocketSession;

/**
 * @author Pushy
 * @since 2019/3/10 19:28
 */
public interface WebSocketHandler {

    default void afterConnectionEstablished(WebSocketSession session) {

    }

    default void processMessage(WebSocketSession session, String message) {

    }

    default void afterConnectionCloses(WebSocketSession session) {

    }

}
