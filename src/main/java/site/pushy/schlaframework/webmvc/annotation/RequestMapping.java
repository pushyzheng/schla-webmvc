package site.pushy.schlaframework.webmvc.annotation;

import site.pushy.schlaframework.webmvc.enums.RequestMethod;

import java.lang.annotation.*;

/**
 * @author Pushy
 * @since 2019/3/7 16:43
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    String value() default "";

    RequestMethod method() default RequestMethod.GET;

}
