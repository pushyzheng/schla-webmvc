package site.pushy.schlaframework.webmvc.util;

import java.util.HashMap;
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
        Map<String, String> result = new HashMap<>();

        String[] arr = uri.split("\\?");
        if (arr.length == 2) {
            String[] items = arr[1].split("&");
            for (String item : items) {
                String[] queries = item.split("=");
                result.put(queries[0], queries[1]);
            }
        }
        return result;
    }

    public static void parsePathVariable(String integralUri) {
        String uri = trimUri(integralUri);

    }



    public static void main(String[] args) {
//        parseQueryString("https://localhost/users?name=Pushy");
        parsePathVariable("/users/2");
    }

}
