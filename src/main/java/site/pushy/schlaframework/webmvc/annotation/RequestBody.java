package site.pushy.schlaframework.webmvc.annotation;

import java.lang.annotation.*;

/**
 * @author Pushy
 * @since 2019/3/7 18:51
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestBody {
}
