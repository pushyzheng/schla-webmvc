package site.pushy.schlaframework.webmvc.handler;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import site.pushy.schlaframework.webmvc.registry.HttpSessionRegistry;
import site.pushy.schlaframework.webmvc.pojo.HttpRequest;
import site.pushy.schlaframework.webmvc.pojo.HttpSession;

import java.util.Set;

/**
 * @author Pushy
 * @since 2019/3/17 20:06
 */
public class SessionHandler {

    public static final String COOKIE_NAME = "JSESSIONID";

    private FullHttpRequest request;

    private HttpRequest httpRequest;

    public SessionHandler(FullHttpRequest request, HttpRequest httpRequest) {
        this.request = request;
        this.httpRequest = httpRequest;
    }

    public String doHandle() {
        if (!hasSessionId()) {
            HttpSession session = HttpSessionRegistry.addSession();
            httpRequest.setSession(session);
            return session.getId();
        }
        return null;
    }

    private boolean hasSessionId() {
        String cookieStr = request.headers().get("Cookie");
        if (cookieStr != null) {
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieStr);
            for (Cookie cookie : cookies) {
                if (cookie.name().equals(COOKIE_NAME) &&
                        HttpSessionRegistry.containsSession(cookie.value())) {
                    HttpSession session = HttpSessionRegistry.getSession(cookie.value());
                    httpRequest.setSession(session);
                    return true;
                }
            }
        }
        return false;
    }
}
