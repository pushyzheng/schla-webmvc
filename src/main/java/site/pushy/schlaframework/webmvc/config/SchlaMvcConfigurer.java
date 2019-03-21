package site.pushy.schlaframework.webmvc.config;

import site.pushy.schlaframework.webmvc.registry.InterceptorRegistry;

/**
 * 客户继承该配置类，并重写配置方法，用于配置web mvc的一些组件和功能
 *
 * @author Pushy
 * @since 2019/3/10 10:51
 */
public interface SchlaMvcConfigurer {

    default void addInterceptors(InterceptorRegistry registry) {
    }

}
