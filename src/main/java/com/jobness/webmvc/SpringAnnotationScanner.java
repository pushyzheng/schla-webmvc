package com.jobness.webmvc;

import com.jobness.webmvc.annotation.RestController;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Pushy
 * @since 2019/3/7 12:57
 */
public class SpringAnnotationScanner extends ClassPathBeanDefinitionScanner {

    public SpringAnnotationScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    /**
     * 添加扫描过滤器，让Spring扫描带自定义注解的类
     */
    public void registerTypeFilter(Class<? extends Annotation> annotation){
        addIncludeFilter(new AnnotationTypeFilter(annotation));
    }

    public void registerTypeFilter(List<Class<? extends Annotation>> annoList) {
        for (int i = 0; i < annoList.size(); i++) {
            registerTypeFilter(annoList.get(i));
        }
    }

}
