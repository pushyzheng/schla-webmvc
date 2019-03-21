package site.pushy.schlaframework.webmvc.registry;

import io.netty.channel.ChannelHandlerContext;
import site.pushy.schlaframework.webmvc.pojo.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Pushy
 * @since 2019/3/21 10:57
 */
public class WebSocketSessionRegistry {

    private static final Map<String, WebSocketSession> sessionMap = new HashMap<>();

    public WebSocketSession register(ChannelHandlerContext ctx) {
        synchronized (sessionMap) {
            WebSocketSession session = new WebSocketSession(ctx.channel());
            sessionMap.put(getChannelId(ctx), session);
            return session;
        }
    }

    public void unRegister(ChannelHandlerContext ctx) {
        synchronized (sessionMap) {
            sessionMap.remove(getChannelId(ctx));
        }
    }

    public WebSocketSession getSession(ChannelHandlerContext ctx) {
        synchronized (sessionMap) {
            return sessionMap.get(getChannelId(ctx));
        }
    }

    private String getChannelId(ChannelHandlerContext ctx) {
        return ctx.channel().id().toString();
    }

}
