package com.jobness.webmvc.annotation;

import java.lang.annotation.*;

/**
 * @author Pushy
 * @since 2019/3/7 19:26
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryString {

    boolean required() default false;

}
