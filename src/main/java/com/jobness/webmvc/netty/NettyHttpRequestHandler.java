package com.jobness.webmvc.netty;

import com.jobness.webmvc.core.MappingRegistry;
import com.jobness.webmvc.enums.ContentType;
import com.jobness.webmvc.handler.HandleMethodArgumentResolver;
import com.jobness.webmvc.util.HttpUrlUtil;
import com.jobness.webmvc.util.RespUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import com.jobness.webmvc.pojo.HttpResponse;
import io.netty.util.CharsetUtil;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**
 * @author Pushy
 * @since 2019/3/7 12:42
 */
public class NettyHttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private ChannelHandlerContext context;

    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        this.context = ctx;
        if (HttpUtil.is100ContinueExpected(request)) {
            send100Continue();
        }

        String uri = HttpUrlUtil.trimUri(request.uri());
        Method method = MappingRegistry.getUrlMethod(uri, request.method());
        Object data;
        // 构造Response对象，注入到客户视图函数中
        final HttpResponse response = new HttpResponse();

        if (method == null) {
            data = RespUtil.error(HttpResponseStatus.NOT_FOUND, HttpResponseStatus.NOT_FOUND.reasonPhrase());
        }
        else {
            data = invokeViewMethod(method, request, response);
        }

        // 生成Response对象并冲刷返回到客户端
        FullHttpResponse fullResponse = generateFullHttpResponse(data, response);
        context.writeAndFlush(fullResponse).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 调用视图函数，获取函数的返回值
     */
    private Object invokeViewMethod(Method method, FullHttpRequest request,
                                    HttpResponse resp) throws Exception {
        method.setAccessible(true);
        HandleMethodArgumentResolver resolver = new HandleMethodArgumentResolver(request, method, resp);
        resolver.doHandle();

        Object controller = MappingRegistry.getMethodController(method);
        List<Object> params = resolver.getParams();
        System.out.println("view method params => " + params);
        return method.invoke(controller, params.toArray());
    }

    private FullHttpResponse generateFullHttpResponse(Object data, HttpResponse resp) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                resp.getStatus(), Unpooled.copiedBuffer(String.valueOf(data), CharsetUtil.UTF_8));
        response.headers().set("Content-Type", resp.getContentType().value());
        response.headers().set("Date", new Date());
        response.headers().set("Server", "jobness-webmvc/0.0.1");
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
        response.headers().set("Content-Type", ContentType.JSON.value());
        response.headers().set("Date", new Date());
        response.headers().set("Server", "jobness-webmvc/0.0.1");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
