package com.jobless.webmvc.annotation;

import java.lang.annotation.*;

/**
 * @author Pushy
 * @since 2019/3/7 21:12
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldRequired {
}
