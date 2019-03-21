package site.pushy.schlaframework.webmvc.handler;

import io.netty.channel.ChannelHandlerContext;
import site.pushy.schlaframework.webmvc.config.HandshakeInterceptor;
import site.pushy.schlaframework.webmvc.pojo.HttpRequest;
import site.pushy.schlaframework.webmvc.registry.WebSocketHandlerRegistry;
import site.pushy.schlaframework.webmvc.registry.WebSocketSessionRegistry;
import site.pushy.schlaframework.webmvc.pojo.WebSocketSession;

/**
 * @author Pushy
 * @since 2019/3/21 11:10
 */
public class WebSocketHandshakeHandler {

    private WebSocketHandlerRegistry webSocketRegistry;

    private WebSocketSessionRegistry sessionRegistry = new WebSocketSessionRegistry();

    public WebSocketHandshakeHandler(WebSocketHandlerRegistry webSocketRegistry) {
        this.webSocketRegistry = webSocketRegistry;
    }

    public boolean doHandle(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
        WebSocketSession session = sessionRegistry.register(ctx);
        HandshakeInterceptor handshakeInterceptor = webSocketRegistry.getHandshakeInterceptor();
        if (handshakeInterceptor != null) {
            return handshakeInterceptor.beforeHandshake(request, session);
        }
        return true;
    }


}
