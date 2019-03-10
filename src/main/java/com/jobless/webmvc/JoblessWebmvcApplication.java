package com.jobless.webmvc;

import com.jobless.webmvc.annotation.RestController;
import com.jobless.webmvc.autoconfig.MybatisAutoConfiguration;
import com.jobless.webmvc.autoconfig.AutoConfigRegistry;
import com.jobless.webmvc.config.InterceptorRegistry;
import com.jobless.webmvc.config.JobnessMvcConfigurer;
import com.jobless.webmvc.config.WebSocketConfigurer;
import com.jobless.webmvc.config.WebSocketHandlerRegistry;
import com.jobless.webmvc.core.CustomAnnotationScanner;
import com.jobless.webmvc.autoconfig.PropertiesConfigReader;
import com.jobless.webmvc.handler.MappingHandler;
import com.jobless.webmvc.netty.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.io.IOException;

/**
 * @author Pushy
 * @since 2019/3/7 12:35
 */
public class JoblessWebmvcApplication {

    private static Logger logger = LogManager.getLogger(JoblessWebmvcApplication.class);

    public static void run(Class<?> primarySource) {
        printBanner();

        try {
            // 获取客户配置文件
            PropertiesConfigReader config = new PropertiesConfigReader();
            config.read(primarySource);
            AutoConfigRegistry.setReader(config);

            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
            context.register(MybatisAutoConfiguration.class);
            context.scan(config.getBasePackage());
            context.refresh();

            // 创建 CustomAnnotationScanner 对象，通过Spring扫描自定义注解类
            CustomAnnotationScanner serviceScanner = new CustomAnnotationScanner(context);
            serviceScanner.registerTypeFilter(RestController.class);
            serviceScanner.scan(config.getBasePackage());

            registerInterceptor(context);
            WebSocketHandlerRegistry webSocketRegistry = registerWebSocket(context);

            MappingHandler mappingHandler = new MappingHandler(context);
            mappingHandler.doHandle();

            HttpServer.setAppContext(context);
            HttpServer.setWebSocketRegistry(webSocketRegistry);
            // 启动Netty HTTP服务器
            HttpServer.run(config.getHost(), config.getPort());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static WebSocketHandlerRegistry registerWebSocket(GenericApplicationContext context) {
        WebSocketHandlerRegistry registry = null;
        try {
            WebSocketConfigurer configurer = context.getBean(WebSocketConfigurer.class);
            registry = new WebSocketHandlerRegistry();
            configurer.registerWebSocketHandlers(registry);
        } catch (NoSuchBeanDefinitionException e) {
            logger.debug("No configure webSocket");
        }
        return registry;
    }

    private static void registerInterceptor(GenericApplicationContext context) {
        try {
            // 获取客户的JobnessMvcConfigurer配置类
            JobnessMvcConfigurer webmvcConfigurer = context.getBean(JobnessMvcConfigurer.class);
            // 手动注册 InterceptorRegistry Bean
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(InterceptorRegistry.class);
            context.registerBeanDefinition(InterceptorRegistry.class.getSimpleName(),
                    builder.getBeanDefinition());
            // 调用客户配置类的addInterceptors，配置拦截器
            InterceptorRegistry interceptorRegistry = context.getBean(InterceptorRegistry.class);
            webmvcConfigurer.addInterceptors(interceptorRegistry);

            System.out.println(interceptorRegistry.getRegistrations());

        } catch (NoSuchBeanDefinitionException e) {
            logger.debug("No configure interceptors");
        }
    }

    private static void printBanner() {
        System.out.println("\n" +
                "               _      _                      \n" +
                "     _        ( )    (_ )                    \n" +
                "    (_)   _   | |_    | |    __    ___   ___ \n" +
                "    | | /'_`\\ | '_`\\  | |  /'__`\\/',__)/',__)\n" +
                "    | |( (_) )| |_) ) | | (  ___/\\__, \\\\__, \\\n" +
                " _  | |`\\___/'(_,__/'(___)`\\____)(____/(____/\n" +
                "( )_| |                                      \n" +
                "`\\___/'                                      \n");
        System.out.println(":: Jobless webmvc ::");
    }

}
