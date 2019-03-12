package site.pushy.schlaframework.webmvc.annotation;

import site.pushy.schlaframework.webmvc.enums.ContentType;

import java.lang.annotation.*;

/**
 * @author Pushy
 * @since 2019/3/7 12:54
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {

    String value() default "";

    ContentType contentType() default ContentType.JSON;

}
