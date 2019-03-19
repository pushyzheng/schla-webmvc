package site.pushy.schlaframework.webmvc.annotation;

import java.lang.annotation.*;

/**
 * @author Pushy
 * @since 2019/3/7 21:12
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldRequired {

    /**
     * 如果是字符串的字段的话，当notEmpty为true时，要求字符串不能为空字符串，即""
     * @return
     */
    boolean notEmpty() default false;

}
