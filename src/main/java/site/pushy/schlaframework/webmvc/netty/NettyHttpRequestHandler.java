package site.pushy.schlaframework.webmvc.netty;

import site.pushy.schlaframework.webmvc.config.HandlerInterceptor;
import site.pushy.schlaframework.webmvc.config.InterceptorRegistration;
import site.pushy.schlaframework.webmvc.config.InterceptorRegistry;
import site.pushy.schlaframework.webmvc.config.WebSocketHandlerRegistry;
import site.pushy.schlaframework.webmvc.core.MappingRegistry;
import site.pushy.schlaframework.webmvc.enums.ContentType;
import site.pushy.schlaframework.webmvc.enums.RequestMethod;
import site.pushy.schlaframework.webmvc.handler.HandleMethodArgumentResolver;
import site.pushy.schlaframework.webmvc.pojo.HttpRequest;
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
        if (webSocketRegistry != null && webSocketRegistry.getPath().equalsIgnoreCase(request.uri())) {
            if (webSocketRegistry.isAvailable()) {
                ctx.fireChannelRead(request.retain());
                return;
            }
        }
        if (HttpUtil.is100ContinueExpected(request)) {
            send100Continue();
        }
        processRequest(request);
    }

    private void processRequest(FullHttpRequest request) throws Exception {
        String uri = HttpUrlUtil.trimUri(request.uri());
        Method method = MappingRegistry.getUrlMethod(uri, request.method());
        Object controller = MappingRegistry.getMethodController(method);
        Object data;
        // 构造Response对象，注入到客户视图函数中
        final HttpResponse httpResponse = new HttpResponse(controller);
        final HttpRequest httpRequest = convertHttpRequest(request);

        boolean interceptResult = processInterceptor(httpRequest, httpResponse);
        if (!interceptResult) {
            data = "拦截失败";
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
        // 生成Response对象并冲刷返回到客户端
        FullHttpResponse response = generateFullHttpResponse(data, httpResponse);
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 执行用户配置的拦截器
     */
    private boolean processInterceptor(site.pushy.schlaframework.webmvc.pojo.HttpRequest request, HttpResponse response) throws Exception {
        String uri = HttpUrlUtil.trimUri(request.getUri());
        if (interceptorRegistry != null) {
            List<InterceptorRegistration> registrations = interceptorRegistry.getRegistrations();
            for (InterceptorRegistration registration : registrations) {
                List<String> includePatterns = registration.getIncludePatterns();
                List<String> excludePatterns = registration.getExcludePatterns();
                // Todo 优化拦截规则算法
                if (includePatterns.contains("/**")) {
                    if (excludePatterns.contains(uri)) {
                        return true;
                    }
                }
                else if (!includePatterns.contains(uri)) {
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
                                    site.pushy.schlaframework.webmvc.pojo.HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        method.setAccessible(true);
        HandleMethodArgumentResolver resolver =
                new HandleMethodArgumentResolver(method, request, httpRequest, httpResponse);
        List<Object> params = resolver.resolveParams();

        Object controller = MappingRegistry.getMethodController(method);
        System.out.println("view method params => " + params);
        return method.invoke(controller, params.toArray());
    }

    private FullHttpResponse generateFullHttpResponse(Object data, HttpResponse resp) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                resp.getStatus(), Unpooled.copiedBuffer(String.valueOf(data), CharsetUtil.UTF_8));
        response.headers().set("Content-Type", resp.getContentType().value());
        response.headers().set("Date", new Date());
        response.headers().set("Server", "example-webmvc/0.0.1");
        return response;
    }

    /**
     * 获取jobness-webmvc封装的HttpRequest对象
     * 因为不能将Netty内置的 FullHttpRequest 暴露给客户使用
     */
    private HttpRequest convertHttpRequest(FullHttpRequest request) {
        HttpRequest result = new HttpRequest();
        RequestMethod requestMethod = RequestMethod.convertHttpMethod(request.method());

        result.setMethod(requestMethod);
        result.setUri(request.uri());
        result.setVersion(request.protocolVersion().toString());
        result.setHeaders(request.headers());
        return result;
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
        response.headers().set("Content-Type", ContentType.JSON.value());
        response.headers().set("Date", new Date());
        response.headers().set("Server", "example-webmvc/0.0.1");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
