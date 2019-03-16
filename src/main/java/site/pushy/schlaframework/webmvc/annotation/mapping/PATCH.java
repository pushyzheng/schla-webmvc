package site.pushy.schlaframework.webmvc.annotation.mapping;

import java.lang.annotation.*;

/**
 * @author Pushy
 * @since 2019/3/7 16:41
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PATCH {

    String value() default "";

}
