package site.pushy.schlaframework.webmvc.util;

import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pushy
 * @since 2019/3/7 19:31
 */
public class HttpUrlUtil {

    /**
     * 修剪uri，去除查询字符串参数值部分
     */
    public static String trimUri(String uri) {
        String[] arr = uri.split("\\?");
        if (arr.length != 0) {
            return arr[0];
        }
        return "";
    }

    public static Map<String, String> parseQueryString(String uri) {
        Map<String, String> res = new HashMap<>();
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        Map<String, List<String>> params = queryStringDecoder.parameters();
        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            res.put(entry.getKey(), entry.getValue().get(0));
        }
        return res;
    }

    public static void parsePathVariable(String integralUri) {
        String uri = trimUri(integralUri);
    }

}
