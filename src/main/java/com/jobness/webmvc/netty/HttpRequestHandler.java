package com.jobness.webmvc.netty;

import com.jobness.webmvc.MappingRegistry;
import com.jobness.webmvc.enums.RequestMethod;
import com.jobness.webmvc.pojo.HttpRequest;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import com.jobness.webmvc.pojo.HttpResponse;
import io.netty.util.CharsetUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author Pushy
 * @since 2019/3/7 12:42
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (HttpUtil.is100ContinueExpected(request)) {
            send100Continue(ctx);
        }

        String requestUrl = request.uri();
        Method method = MappingRegistry.getUrlMethod(requestUrl);
        Object data;
        HttpResponseStatus status;

        if (method == null) {
            data = "404 not found";
            status = HttpResponseStatus.NOT_FOUND;
        }
        else {
            method.setAccessible(true);
            Object controller = MappingRegistry.getMethodController(method);
            // 构造Response对象，注入到客户视图函数中
            HttpResponse resp = new HttpResponse();
            List<Object> params = Arrays.asList(getHttpRequest(request), resp);
            data = method.invoke(controller, params.toArray());
            status = resp.getStatus();
        }

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                status, Unpooled.copiedBuffer(String.valueOf(data), CharsetUtil.UTF_8));
        response.headers().set("Content-Type", "application/json; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 处理100 Continue请求以符合HTTP1.1规范
     */
    private void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        ctx.writeAndFlush(response);
    }

    private HttpRequest getHttpRequest(FullHttpRequest request) {
        HttpRequest result = new HttpRequest();

        RequestMethod requestMethod;
        HttpMethod httpMethod = request.method();
        if (httpMethod.equals(HttpMethod.GET)) {
            requestMethod = RequestMethod.GET;
        } else if (httpMethod.equals(HttpMethod.POST)) {
            requestMethod = RequestMethod.POST;
        } else if (httpMethod.equals(HttpMethod.DELETE)) {
            requestMethod = RequestMethod.DELETE;
        } else if (httpMethod.equals(HttpMethod.PUT)) {
            requestMethod = RequestMethod.PUT;
        } else {
            requestMethod = RequestMethod.GET;
        }

        result.setMethod(requestMethod);
        result.setUri(request.uri());
        result.setVersion(request.protocolVersion().toString());
        result.setHeaders(request.headers());
        return result;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

}
