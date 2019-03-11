package site.pushy.schlaframework.webmvc.config;

import java.util.ArrayList;
import java.util.List;

/**
 * web拦截器注册中心
 *
 * @author Pushy
 * @since 2019/3/10 10:57
 */
public class InterceptorRegistry {

    private final List<InterceptorRegistration> registrations = new ArrayList<>();

    public InterceptorRegistration addInterceptor(HandlerInterceptor interceptor) {
        InterceptorRegistration registration = new InterceptorRegistration(interceptor);
        registrations.add(registration);
        return registration;
    }

    public List<InterceptorRegistration> getRegistrations() {
        return registrations;
    }
}
