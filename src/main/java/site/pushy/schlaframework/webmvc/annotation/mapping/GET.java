package site.pushy.schlaframework.webmvc.annotation.mapping;

import org.springframework.core.annotation.AliasFor;
import site.pushy.schlaframework.webmvc.annotation.RequestMapping;
import site.pushy.schlaframework.webmvc.enums.RequestMethod;

import java.lang.annotation.*;

/**
 * @author Pushy
 * @since 2019/3/7 16:41
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GET {

    String value() default "";

}
