package com.example.demo;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Annotated;
import org.springframework.core.io.Resource;
import site.pushy.schlaframework.webmvc.annotation.RequestMapping;
import site.pushy.schlaframework.webmvc.annotation.mapping.GET;

import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;

/**
 * @author Pushy
 * @since 2019/3/12 19:57
 */
public class Test {
    public static void main(String[] args) {
        Class<?> clazz = TestController.class;
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(GET.class)) {
                GET anno = method.getAnnotation(GET.class);
                System.out.println(anno.getClass().getAnnotatedSuperclass());
                String value = anno.value();
                System.out.println(value);
            }
/*            if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping anno = method.getAnnotation(RequestMapping.class);
                String value = anno.value();
                System.out.println(value);
            }*/
        }


        URL url = TestController.class.getClassLoader().getResource("schla-webmvc.properties");
        System.out.println(url);
    }
}
