package site.pushy.schlaframework.webmvc.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import site.pushy.schlaframework.webmvc.annotation.*;
import site.pushy.schlaframework.webmvc.exception.*;
import site.pushy.schlaframework.webmvc.pojo.HttpRequest;
import site.pushy.schlaframework.webmvc.pojo.HttpResponse;
import site.pushy.schlaframework.webmvc.pojo.HttpSession;
import site.pushy.schlaframework.webmvc.util.HttpUrlUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import site.pushy.schlaframework.webmvc.util.StringUtil;

import java.lang.reflect.Field;
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

    // 注入给客户项目的Request对象（如果需要的话）
    private HttpRequest httpRequest;

    // 注入给客户项目的Response对象（如果需要的话）
    private HttpResponse httpResponse;

    public HandleMethodArgumentResolver(Method method, FullHttpRequest request,
                                        HttpRequest httpRequest, HttpResponse httpResponse) {
        this.request = request;
        this.method = method;
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
    }

    public List<Object> resolveParams() {
        // 获取到POST/DELETE/PUT 提交的body内容数据
        String body = request.content().toString(CharsetUtil.UTF_8);
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            Class<?> paramType = parameter.getType();
            if (paramType == HttpResponse.class) {
                params.add(httpResponse);
            }
            else if (paramType == HttpRequest.class) {
                params.add(httpRequest);
            }
            // 当参数被@RequestBody注解时，使用fastJSON解析表单内容，并转换成相应的实体类
            else if (parameter.isAnnotationPresent(RequestBody.class)) {
                processRequestBody(parameter, body);
            }
            // 当参数被@QueryString注解时，检测参数是否不为空，然后转换成相应类型后存入参数列表
            else if (parameter.isAnnotationPresent(QueryString.class)) {
                processQueryString(parameter);
            }
            // 当参数被@PathVariable注解时，解析路径的变量并存入参数列表中
            else if (parameter.isAnnotationPresent(PathVariable.class)) {
                processPathVariable(parameter, request.uri());
            }
            // 当参数被@SessionAttribute注解时，获取session中的相应的属性值并注入
            else if (parameter.isAnnotationPresent(SessionAttribute.class)) {
                processSessionAttribute(parameter);
            }
        }
        return params;
    }

    private void processRequestBody(Parameter parameter, String body) {
        if (body == null || body.isEmpty()) {
            throw new MissingRequestBodyException("Required request body is missing",
                    HttpResponseStatus.BAD_REQUEST);
        }
        try {
            JSONObject jsonObject = JSON.parseObject(body);
            for (Field field : parameter.getType().getDeclaredFields()) {
                if (field.isAnnotationPresent(FieldRequired.class)) {
                    FieldRequired anno = field.getAnnotation(FieldRequired.class);
                    Object value = jsonObject.get(field.getName());
                    if (field.getType() == String.class && anno.notEmpty()) {
                        String str = String.valueOf(value);
                        if (StringUtil.isEmpty(str)) {
                            String message = String.format("Required field %s cannot be empty", field.getName());
                            throw new MissingBodyFieldException(message, HttpResponseStatus.BAD_REQUEST);
                        }
                    }
                    if (value == null) {
                        String message = String.format("Required field %s is not present", field.getName());
                        throw new MissingBodyFieldException(message, HttpResponseStatus.BAD_REQUEST);
                    }
                }
            }
            Object object = JSON.parseObject(body, parameter.getType());
            params.add(object);
        } catch (JSONException e) {  // JSON解析异常
            throw new JSONParseErrorException("The request json body parse error：" + e.getMessage(),
                    HttpResponseStatus.BAD_REQUEST);
        }
    }

    private void processQueryString(Parameter parameter) {
        // 解析出请求的URL查询字符串参数和值
        Map<String, String> queries = HttpUrlUtil.parseQueryString(request.uri());

        Class<?> paramType = parameter.getType();
        QueryString anno = parameter.getAnnotation(QueryString.class);
        String name = parameter.getName();
        if (!StringUtil.isEmpty(anno.value())) {
            name = anno.value();
        }
        // java compiler 必须添加 -parameters 参数，否则获取到的参数名如 arg0 这样
        String value = queries.get(name);
        if (value == null && anno.required()) {
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

    private void processSessionAttribute(Parameter parameter) {
        SessionAttribute anno = parameter.getAnnotation(SessionAttribute.class);
        String name = parameter.getName();
        HttpSession session = httpRequest.getSession();

        if (!StringUtil.isEmpty(anno.value())) {
            name = anno.value();
        }
        Object attr = session.getAttribute(name);

        if (attr == null) {
            String message = String.format("The attribute %s of session is not present.", name);
            throw new MissingSessionAttributeException(message);
        }
        params.add(attr);
    }

}
