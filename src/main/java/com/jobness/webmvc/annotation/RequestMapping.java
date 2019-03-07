package com.jobness.webmvc.annotation;

import com.jobness.webmvc.enums.RequestMethod;

import java.lang.annotation.*;

/**
 * @author Pushy
 * @since 2019/3/7 16:43
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    String value();

    RequestMethod method() default RequestMethod.GET;

}
