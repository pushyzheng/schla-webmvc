package com.jobless.webmvc.config;

/**
 * @author Pushy
 * @since 2019/3/10 19:38
 */
public class WebSocketHandlerRegistry {

    private String path;

    private WebSocketHandler handler;

    public void setHandler(WebSocketHandler webSocketHandler, String path) {
        this.path = path;
        this.handler = webSocketHandler;
    }

    public WebSocketHandler getHandler() {
        return handler;
    }

    public String getPath() {
        return path;
    }

    public boolean isOpened() {
        return path != null && !path.isEmpty();
    }

    @Override
    public String toString() {
        return "WebSocketHandlerRegistry{" +
                "path='" + path + '\'' +
                ", handler=" + handler +
                '}';
    }
}
