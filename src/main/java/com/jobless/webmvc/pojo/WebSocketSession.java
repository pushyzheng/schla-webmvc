package com.jobless.webmvc.pojo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.*;

/**
 * @author Pushy
 * @since 2019/3/10 19:29
 */
public class WebSocketSession {

    private Channel channel;

    public WebSocketSession() {
    }

    public WebSocketSession(Channel channel) {
        this.channel = channel;
    }

    public String getId() {
        return channel.id().toString();
    }

    public String getRemoteAddress() {
        return channel.remoteAddress().toString();
    }

    public String localAddress() {
        return channel.localAddress().toString();
    }

    public void sendTextMessage(String message) {
        channel.writeAndFlush(new TextWebSocketFrame(message));
    }

    public void sendBinaryMessage(ByteBuf byteBuf) {
        channel.writeAndFlush(new BinaryWebSocketFrame(byteBuf));
    }

    public void sendPongMessage() {
        channel.writeAndFlush(new PongWebSocketFrame());
    }

    public void sendPingMessage() {
        channel.writeAndFlush(new PingWebSocketFrame());
    }

    public void closeSession() {
        channel.writeAndFlush(new CloseWebSocketFrame());
    }

}
