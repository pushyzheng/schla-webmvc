package com.jobness.webmvc.netty;

import com.alibaba.fastjson.JSON;
import com.jobness.webmvc.MappingRegistry;
import com.jobness.webmvc.annotation.QueryString;
import com.jobness.webmvc.annotation.RequestBody;
import com.jobness.webmvc.enums.ContentType;
import com.jobness.webmvc.enums.RequestMethod;
import com.jobness.webmvc.exception.MissQueryStringException;
import com.jobness.webmvc.pojo.HttpRequest;
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
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Pushy
 * @since 2019/3/7 12:42
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (HttpUtil.is100ContinueExpected(request)) {
            send100Continue(ctx);
        }

        String uri = HttpUrlUtil.trimUri(request.uri());
        Method method = MappingRegistry.getUrlMethod(uri);
        Object data;
        HttpResponseStatus status;
        // 构造Response对象，注入到客户视图函数中
        HttpResponse resp = new HttpResponse();

        if (method == null) {
            status = HttpResponseStatus.NOT_FOUND;
            data = RespUtil.error(status, status.reasonPhrase());
        }
        else {
            method.setAccessible(true);

            Object controller = MappingRegistry.getMethodController(method);
            List<Object> params = getMethodParams(method, request);
            System.out.println("view method params => " + params);
            data = method.invoke(controller, params.toArray());
            status = resp.getStatus();
        }

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                status, Unpooled.copiedBuffer(String.valueOf(data), CharsetUtil.UTF_8));
        response.headers().set("Content-Type", resp.getContentType().value());
        response.headers().set("Date", new Date());
        response.headers().set("Server", "jobness-webmvc/0.0.1");
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

    private List<Object> getMethodParams(Method method, FullHttpRequest request) {
        // 获取到POST/DELETE/PUT 提交的body内容数据
        String body = request.content().toString(CharsetUtil.UTF_8);
        // 注入视图函数的参数列表
        List<Object> result = new ArrayList<>();
        // 解析出请求的URL查询字符串参数和值
        Map<String, String> queries = HttpUrlUtil.parseQueryString(request.uri());

        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            Class<?> paramType = parameter.getType();
            if (paramType == HttpResponse.class) {
                result.add(new HttpResponse());
            } else if (paramType == HttpRequest.class) {
                result.add(getHttpRequest(request));
            }
            // 当请求方法不为GET时，使用fastJSON解析表单内容，并转换成相应的实体类
            if (!request.method().equals(HttpMethod.GET)) {
                if (parameter.isAnnotationPresent(RequestBody.class)) {
                    Object object = JSON.parseObject(body, parameter.getType());
                    result.add(object);
                }
            }
            if (parameter.isAnnotationPresent(QueryString.class)) {
                QueryString queryString = parameter.getAnnotation(QueryString.class);
                // java compiler 必须添加-parameters参数，否则获取到的参数名如 arg0 这样
                String value = queries.get(parameter.getName());
                if (value == null && queryString.required()) {
                    String message = String.format("查询字符串参数值%s不能为空", parameter.getName());
                    throw new MissQueryStringException(message, HttpResponseStatus.BAD_REQUEST);
                }
                // 将 String 参数值value转换为视图函数参数的相应类型
                if (value != null && (paramType == Integer.class || paramType == int.class)) {
                    result.add(Integer.parseInt(value));
                } else if (value != null && (paramType == Double.class || paramType == double.class)) {
                    result.add(Double.parseDouble(value));
                } else if (value != null && (paramType == Long.class || paramType == long.class)) {
                    result.add(Long.parseLong(value));
                } else if (value != null && (paramType == Float.class || paramType == float.class)) {
                    result.add(Float.parseFloat(value));
                } else if (value != null && (paramType == Short.class || paramType == short.class)) {
                    result.add(Short.parseShort(value));
                } else {
                    result.add(value);
                }
            }
        }
        return result;
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
