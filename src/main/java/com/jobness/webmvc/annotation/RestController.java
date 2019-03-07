package com.jobness.webmvc.annotation;

import com.jobness.webmvc.enums.ContentType;

import java.lang.annotation.*;

/**
 * @author Pushy
 * @since 2019/3/7 12:54
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestController {

    String value() default "";

    ContentType contentType() default ContentType.JSON;

}
