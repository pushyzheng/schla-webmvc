package site.pushy.schlaframework.webmvc.netty;

import site.pushy.schlaframework.webmvc.config.WebSocketHandler;
import site.pushy.schlaframework.webmvc.registry.WebSocketHandlerRegistry;
import site.pushy.schlaframework.webmvc.registry.WebSocketSessionRegistry;
import site.pushy.schlaframework.webmvc.pojo.WebSocketSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Pushy
 * @since 2019/3/10 18:56
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final Map<String, WebSocketSession> sessionMap = new HashMap<>();

    private final WebSocketSessionRegistry registry = new WebSocketSessionRegistry();

    private WebSocketHandler webSocketHandler;

    TextWebSocketFrameHandler(WebSocketHandlerRegistry registry) {
        webSocketHandler = registry.getHandler();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        if (evt instanceof HandshakeComplete) {
//            HandshakeComplete handshakeComplete = (HandshakeComplete) evt;
//            System.out.println(handshakeComplete.requestHeaders());
//        }
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            // 握手成功，从该ChannelHandler中移除HttpRequestHandler，因此将不会接受任何HTTP消息了
            ctx.pipeline().remove(NettyHttpRequestHandler.class);

            processConnectionEstablished(ctx);
            if (getSession(ctx) != null) {
                webSocketHandler.afterConnectionEstablished(getSession(ctx));
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        if (getSession(ctx) != null) {
            webSocketHandler.processMessage(getSession(ctx), msg.text());
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (getSession(ctx) != null) {
            webSocketHandler.afterConnectionCloses(getSession(ctx));
            registry.unRegister(ctx);
        }
    }

    private void processConnectionEstablished(ChannelHandlerContext ctx) {
        if (registry.getSession(ctx) == null) {
            registry.register(ctx);
        }
    }

    private WebSocketSession getSession(ChannelHandlerContext ctx) {
        return registry.getSession(ctx);
    }

}
