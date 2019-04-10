package site.pushy.schlaframework.webmvc.config;

import site.pushy.schlaframework.webmvc.autoconfig.PropertiesConfigReader;
import site.pushy.schlaframework.webmvc.exception.ConfigurationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 存放每个拦截器的信息
 *
 * @author Pushy
 * @since 2019/3/10 10:52
 */
public class InterceptorRegistration {

    // 拦截器实例
    private final HandlerInterceptor interceptor;

    // 拦截器的URL路径
    private final List<String> includePatterns = new ArrayList<>();

    // 不拦截的URL路径
    private final List<String> excludePatterns = new ArrayList<>();

    public InterceptorRegistration(HandlerInterceptor interceptor) {
        if (interceptor == null) {
            throw new ConfigurationException("The interceptor is required");
        }
        this.interceptor = interceptor;
    }

    public InterceptorRegistration addPathPatterns(String... patterns) {
        return addPathPatterns(Arrays.asList(patterns));
    }

    public InterceptorRegistration addPathPatterns(List<String> patterns) {
        this.includePatterns.addAll(patterns);
        return this;
    }

    public InterceptorRegistration excludePathPatterns(String... patterns) {
        return excludePathPatterns(Arrays.asList(patterns));
    }

    public InterceptorRegistration excludePathPatterns(List<String> patterns) {
        this.excludePatterns.addAll(patterns);
        return this;
    }

    public HandlerInterceptor getInterceptor() {
        return interceptor;
    }

    public List<String> getIncludePatterns() {
        return includePatterns;
    }

    public List<String> getExcludePatterns() {
        return excludePatterns;
    }

    @Override
    public String toString() {
        return "InterceptorRegistration{" +
                "interceptor=" + interceptor +
                ", includePatterns=" + includePatterns +
                ", excludePatterns=" + excludePatterns +
                '}';
    }
}
