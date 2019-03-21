package site.pushy.schlaframework.webmvc.registry;

import site.pushy.schlaframework.webmvc.config.HandshakeInterceptor;
import site.pushy.schlaframework.webmvc.config.WebSocketHandler;

/**
 * @author Pushy
 * @since 2019/3/10 19:38
 */
public class WebSocketHandlerRegistry {

    private String path;

    private WebSocketHandler handler;

    private HandshakeInterceptor handshakeInterceptor;

    public WebSocketHandlerRegistry setHandler(WebSocketHandler webSocketHandler, String path) {
        this.path = path;
        this.handler = webSocketHandler;
        return this;
    }

    public WebSocketHandlerRegistry setInterceptors(HandshakeInterceptor interceptor) {
        this.handshakeInterceptor = interceptor;
        return this;
    }

    public WebSocketHandler getHandler() {
        return handler;
    }

    public String getPath() {
        return path;
    }

    public HandshakeInterceptor getHandshakeInterceptor() {
        return handshakeInterceptor;
    }

    public boolean isAvailable() {
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
