package com.jobness.webmvc.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.jobness.webmvc.annotation.PathVariable;
import com.jobness.webmvc.annotation.QueryString;
import com.jobness.webmvc.annotation.RequestBody;
import com.jobness.webmvc.enums.RequestMethod;
import com.jobness.webmvc.exception.JSONParseErrorException;
import com.jobness.webmvc.exception.MissingQueryStringException;
import com.jobness.webmvc.exception.MissingRequestBodyException;
import com.jobness.webmvc.pojo.HttpRequest;
import com.jobness.webmvc.pojo.HttpResponse;
import com.jobness.webmvc.util.HttpUrlUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Pushy
 * @since 2019/3/9 21:49
 */
public class HandleMethodArgumentResolver {

    // 注入视图函数的参数列表
    private final List<Object> params = new ArrayList<>();

    // Netty 自带的Request对象
    private FullHttpRequest request;

    // 视图函数反射Method对象
    private Method method;

    // 注入给客户项目的Response对象（如果需要的话）
    private HttpResponse resp;

    public HandleMethodArgumentResolver(FullHttpRequest request, Method method, HttpResponse resp) {
        this.request = request;
        this.method = method;
        this.resp = resp;
    }

    public List<Object> getParams() {
        return params;
    }

    public void doHandle() {
        // 获取到POST/DELETE/PUT 提交的body内容数据
        String body = request.content().toString(CharsetUtil.UTF_8);
        // 解析出请求的URL查询字符串参数和值
        Map<String, String> queries = HttpUrlUtil.parseQueryString(request.uri());

        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            Class<?> paramType = parameter.getType();
            if (paramType == HttpResponse.class) {
                params.add(resp);
            }
            else if (paramType == HttpRequest.class) {
                params.add(getHttpRequest(request));
            }
            // 当参数被@RequestBody注解时，使用fastJSON解析表单内容，并转换成相应的实体类
            else if (parameter.isAnnotationPresent(RequestBody.class)) {
                processRequestBody(parameter, body);
            }
            // 当参数被@QueryString注解时，检测参数是否不为空，然后转换成相应类型后存入参数列表
            else if (parameter.isAnnotationPresent(QueryString.class)) {
                processQueryString(parameter, queries);
            }
            // 当参数被@PathVariable注解时，解析路径的变量并存入参数列表中
            else if (parameter.isAnnotationPresent(PathVariable.class)) {
                processPathVariable(parameter, request.uri());
            }
        }
    }

    private void processRequestBody(Parameter parameter, String body) {
        // Todo 对@FieldRequired注解功能的实现
        if (body == null || body.isEmpty()) {
            throw new MissingRequestBodyException("Required request body is missing",
                    HttpResponseStatus.BAD_REQUEST);
        }
        try {
            Object object = JSON.parseObject(body, parameter.getType());
            params.add(object);
        } catch (JSONException e) {  // JSON解析异常
            throw new JSONParseErrorException("The request json body parse error：" + e.getMessage(),
                    HttpResponseStatus.BAD_REQUEST);
        }
    }

    private void processQueryString(Parameter parameter, Map<String, String> queries) {
        Class<?> paramType = parameter.getType();
        QueryString queryString = parameter.getAnnotation(QueryString.class);
        // java compiler 必须添加-parameters参数，否则获取到的参数名如 arg0 这样
        // Todo 客户端如果不加该编译参数还是无法识别，需要进行修改
        String value = queries.get(parameter.getName());
        if (value == null && queryString.required()) {
            String message = String.format("查询字符串参数值%s不能为空", parameter.getName());
            throw new MissingQueryStringException(message, HttpResponseStatus.BAD_REQUEST);
        }
        // 将 String 参数值value转换为视图函数参数的相应类型
        if (value != null && (paramType == Integer.class || paramType == int.class)) {
            params.add(Integer.parseInt(value));
        } else if (value != null && (paramType == Double.class || paramType == double.class)) {
            params.add(Double.parseDouble(value));
        } else if (value != null && (paramType == Long.class || paramType == long.class)) {
            params.add(Long.parseLong(value));
        } else if (value != null && (paramType == Float.class || paramType == float.class)) {
            params.add(Float.parseFloat(value));
        } else if (value != null && (paramType == Short.class || paramType == short.class)) {
            params.add(Short.parseShort(value));
        } else {
            params.add(value);
        }
    }

    private void processPathVariable(Parameter parameter, String uri) {

    }

    /**
     * 获取jobness-webmvc封装的HttpRequest对象
     * 因为不能将Netty内置的 FullHttpRequest 暴露给客户使用
     */
    private HttpRequest getHttpRequest(FullHttpRequest request) {
        HttpRequest result = new HttpRequest();
        RequestMethod requestMethod = RequestMethod.convertHttpMethod(request.method());

        result.setMethod(requestMethod);
        result.setUri(request.uri());
        result.setVersion(request.protocolVersion().toString());
        result.setHeaders(request.headers());
        return result;
    }

}
