package com.jobless.webmvc.annotation;

import java.lang.annotation.*;

/**
 * @author Pushy
 * @since 2019/3/10 10:10
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PathVariable {
}
