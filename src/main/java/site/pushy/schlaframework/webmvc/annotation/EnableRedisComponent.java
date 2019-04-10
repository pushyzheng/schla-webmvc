package site.pushy.schlaframework.webmvc.annotation;

import java.lang.annotation.*;

/**
 * @author Pushy
 * @since 2019/3/23 10:13
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableRedisComponent {
}
