package site.pushy.schlaframework.webmvc.netty;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import site.pushy.schlaframework.webmvc.config.*;
import site.pushy.schlaframework.webmvc.registry.InterceptorRegistry;
import site.pushy.schlaframework.webmvc.registry.MappingRegistry;
import site.pushy.schlaframework.webmvc.enums.ContentType;
import site.pushy.schlaframework.webmvc.exception.HttpBaseException;
import site.pushy.schlaframework.webmvc.handler.HandleMethodArgumentResolver;
import site.pushy.schlaframework.webmvc.handler.SessionHandler;
import site.pushy.schlaframework.webmvc.handler.WebSocketHandshakeHandler;
import site.pushy.schlaframework.webmvc.pojo.HttpRequest;
import site.pushy.schlaframework.webmvc.registry.WebSocketHandlerRegistry;
import site.pushy.schlaframework.webmvc.registry.WebSocketSessionRegistry;
import site.pushy.schlaframework.webmvc.util.HttpUrlUtil;
import site.pushy.schlaframework.webmvc.util.RespUtil;
import site.pushy.schlaframework.webmvc.pojo.HttpResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**
 * @author Pushy
 * @since 2019/3/7 12:42
 */
public class NettyHttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private ChannelHandlerContext context;

    private ApplicationContext appContext;

    private InterceptorRegistry interceptorRegistry;

    private WebSocketHandlerRegistry webSocketRegistry;

    private static Logger logger = LogManager.getLogger(NettyHttpRequestHandler.class);

    NettyHttpRequestHandler(ApplicationContext applicationContext) {
        this(applicationContext, null);
    }

    NettyHttpRequestHandler(ApplicationContext applicationContext, WebSocketHandlerRegistry registry) {
        appContext = applicationContext;
        if (appContext.containsBean(InterceptorRegistry.class.getSimpleName())) {
            interceptorRegistry = appContext.getBean(InterceptorRegistry.class);
        }
        this.webSocketRegistry = registry;
    }

    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        this.context = ctx;
        String uri = request.uri();
        String wsUri = webSocketRegistry.getPath();

        if (webSocketRegistry != null && webSocketRegistry.isAvailable()
                && HttpUrlUtil.trimUri(uri).equalsIgnoreCase(wsUri)) {
            processWebSocketHandShake(request);  // 处理webSocket拦截和握手
        } else {
            processHttpRequest(request);         // 处理正常的HTTP请求
        }
    }

    /**
     * process webSocket handShake
     */
    private void processWebSocketHandShake(FullHttpRequest request) throws Exception {
        WebSocketHandshakeHandler handler = new WebSocketHandshakeHandler(webSocketRegistry);
        boolean res = handler.doHandle(context, new HttpRequest(request));

        // 开始握手
        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(
                request.uri(), null, false
        );
        WebSocketServerHandshaker handshaker = factory.newHandshaker(request);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(context.channel());
        }
        else {
            ChannelFuture future = handshaker.handshake(context.channel(), request);
            if (future.isSuccess()) {
                logger.debug("webSocket handshake succeed");
                // 发出 HANDSHAKE_COMPLETE 事件
                context.fireUserEventTriggered(WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
            }
            else {
                context.fireExceptionCaught(future.cause());
            }
        }
    }

    private void processHttpRequest(FullHttpRequest request) throws Exception {
        if (HttpUtil.is100ContinueExpected(request)) {
            send100Continue();
        }
        doProcessHttpRequest(request);
    }

    private void doProcessHttpRequest(FullHttpRequest request) throws Exception {
        String message = String.format("[%s] %s", request.method(), request.uri());
        System.out.println(message);

        String uri = HttpUrlUtil.trimUri(request.uri());
        Method method = MappingRegistry.getUrlMethod(uri, request.method());
        Object controller = MappingRegistry.getMethodController(method);
        Object data;
        // 构造Response对象，注入到客户视图函数中
        final HttpResponse httpResponse = new HttpResponse(controller);
        final HttpRequest httpRequest = new HttpRequest(request);

        SessionHandler sessionHandler = new SessionHandler(request, httpRequest);
        String sessionId = sessionHandler.doHandle();

        try {
            if (!processInterceptor(httpRequest, httpResponse)) {
                data = "";
            }
            else {
                if (method == null) {
                    httpResponse.setStatus(HttpResponseStatus.NOT_FOUND);
                    data = RespUtil.error(HttpResponseStatus.NOT_FOUND,
                            HttpResponseStatus.NOT_FOUND.reasonPhrase());
                }
                else {
                    data = invokeViewMethod(method, request, httpRequest, httpResponse);
                }
            }
        } catch (HttpBaseException e) {
            data = RespUtil.error(e.getStatus(), e.getMessage());
            httpResponse.setStatus(e.getStatus());
        } catch (InvocationTargetException invocationException) {
            Throwable t = invocationException.getTargetException();
            if (t instanceof HttpBaseException) {
                HttpBaseException e = (HttpBaseException) t;
                data = RespUtil.error(e.getStatus(), e.getMessage());
                httpResponse.setStatus(e.getStatus());
            } else {
                t.printStackTrace();
                data = RespUtil.error(HttpResponseStatus.INTERNAL_SERVER_ERROR, t.getMessage());
                httpResponse.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            data = RespUtil.error(HttpResponseStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            httpResponse.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
        // 生成Response对象并冲刷返回到客户端
        FullHttpResponse response = generateFullHttpResponse(data, httpResponse, sessionId);
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 执行用户配置的拦截器
     */
    private boolean processInterceptor(HttpRequest request, HttpResponse response) throws Exception {
        String uri = HttpUrlUtil.trimUri(request.getUri());
        if (interceptorRegistry != null) {
            List<InterceptorRegistration> registrations = interceptorRegistry.getRegistrations();
            for (InterceptorRegistration registration : registrations) {
                List<String> includePatterns = registration.getIncludePatterns();
                List<String> excludePatterns = registration.getExcludePatterns();
                if (includePatterns.contains("/**")) {
                    if (excludePatterns.contains(uri)) {
                        return true;
                    }
                } else if (!includePatterns.contains(uri)) {
                    return true;
                }
                HandlerInterceptor interceptor = registration.getInterceptor();
                boolean result = interceptor.preHandle(request, response);
                if (!result) return false;
            }
        }
        return true;
    }

    /**
     * 调用视图函数，获取函数的返回值
     */
    private Object invokeViewMethod(Method method, FullHttpRequest request,
                                    HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        method.setAccessible(true);
        HandleMethodArgumentResolver resolver =
                new HandleMethodArgumentResolver(method, request, httpRequest, httpResponse);
        List<Object> params = resolver.resolveParams();

        Object controller = MappingRegistry.getMethodController(method);
        return method.invoke(controller, params.toArray());
    }

    private FullHttpResponse generateFullHttpResponse(Object data, HttpResponse resp, String sessionId) {
        String body;
        if (data.getClass() != String.class)
            body = JSON.toJSONString(data);
        else
            body = String.valueOf(data);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                resp.getStatus(), Unpooled.copiedBuffer(body, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, resp.getContentType().value());
        response.headers().set(HttpHeaderNames.DATE, new Date());
        response.headers().set(HttpHeaderNames.SERVER, "schla-webmvc/0.0.1");
        if (sessionId != null) {
            // 当 sessionId 为空时，说明该客户端是首次访问服务器（或者手动清除缓存和过期）
            // 需要给客户端设置set-cookie头
            Cookie cookie = new DefaultCookie("JSESSIONID", sessionId);
            cookie.setPath("/");
            String cookieStr = ServerCookieEncoder.STRICT.encode(cookie);
            response.headers().set(HttpHeaderNames.SET_COOKIE, cookieStr);
        }
        return response;
    }

    /**
     * 处理100 Continue请求以符合HTTP1.1规范
     */
    private void send100Continue() {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        context.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();

        String content = RespUtil.error(HttpResponseStatus.INTERNAL_SERVER_ERROR, cause.getMessage());
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR,
                Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, ContentType.JSON.value());
        response.headers().set(HttpHeaderNames.DATE, new Date());
        response.headers().set(HttpHeaderNames.SERVER, "schla-webmvc/0.0.1");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
