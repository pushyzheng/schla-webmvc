package site.pushy.schlaframework.webmvc.util;

import com.alibaba.fastjson.JSON;
import site.pushy.schlaframework.webmvc.pojo.BaseResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author Pushy
 * @since 2019/3/6 13:13
 */
public class RespUtil {

    public static <T> String success(T data) {
        BaseResponse<T> response = new BaseResponse<>(data, "", HttpResponseStatus.OK.code());
        return JSON.toJSONString(response);
    }

    public static String error(HttpResponseStatus status, String message) {
        return error(status.code(), message);
    }

    public static String error(int code, String message) {
        BaseResponse<String> response = new BaseResponse<>("", message, code);
        return JSON.toJSONString(response);
    }

}
