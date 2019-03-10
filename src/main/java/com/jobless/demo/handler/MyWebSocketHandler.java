package com.jobless.demo.handler;

import com.jobless.webmvc.config.WebSocketHandler;
import com.jobless.webmvc.pojo.WebSocketSession;

/**
 * @author Pushy
 * @since 2019/3/10 19:39
 */
public class MyWebSocketHandler implements WebSocketHandler {

    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("【" + session.getId() + "】 connected connection");
    }

    public void processMessage(WebSocketSession session, String message) {
        System.out.println("Got 【" + session.getId() + "】 message  => " + message);
        if (message.equals("PING")) {
            session.sendTextMessage("PONG");
        }
    }

    public void afterConnectionCloses(WebSocketSession session) {
        System.out.println("【" + session.getId() + "】 closed connection");
    }
}
