package site.pushy.schlaframework.webmvc.annotation;

import java.lang.annotation.*;

/**
 * @author Pushy
 * @since 2019/3/7 19:26
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryString {

    /**
     * 如果客户没有指定value值的话，编译必须加上 -parameters 参数
     * @return
     */
    String value() default "";

    boolean required() default false;

}
