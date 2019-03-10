package com.jobless.webmvc.netty;

import com.jobless.webmvc.config.WebSocketHandler;
import com.jobless.webmvc.config.WebSocketHandlerRegistry;
import com.jobless.webmvc.pojo.WebSocketSession;
import io.netty.channel.Channel;
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

    private WebSocketHandler webSocketHandler;

    TextWebSocketFrameHandler(WebSocketHandlerRegistry registry) {
        webSocketHandler = registry.getHandler();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            // 握手成功，从该ChannelHandler中移除HttpRequestHandler，因此将不会接受任何HTTP消息了
            ctx.pipeline().remove(NettyHttpRequestHandler.class);

            processConnectionEstablished(ctx);
            webSocketHandler.afterConnectionEstablished(getSession(ctx.channel()));
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        webSocketHandler.processMessage(getSession(ctx.channel()), msg.text());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        webSocketHandler.afterConnectionCloses(getSession(ctx.channel()));
    }

    private void processConnectionEstablished(ChannelHandlerContext ctx) {
        String channelId = ctx.channel().id().toString();
        sessionMap.put(channelId, new WebSocketSession(ctx.channel()));
    }

    private WebSocketSession getSession(Channel channel) {
        return sessionMap.get(channel.id().toString());
    }

}
