package site.pushy.schlaframework.webmvc.util;

import java.util.UUID;

/**
 * @author Pushy
 * @since 2019/3/17 20:01
 */
public class UUIDUtil {

    public static String getString() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
