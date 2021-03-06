package site.pushy.schlaframework.webmvc;

import site.pushy.schlaframework.webmvc.annotation.Controller;
import site.pushy.schlaframework.webmvc.annotation.EnableMongoComponent;
import site.pushy.schlaframework.webmvc.annotation.EnableRedisComponent;
import site.pushy.schlaframework.webmvc.annotation.RestController;
import site.pushy.schlaframework.webmvc.autoconfig.MybatisAutoConfiguration;
import site.pushy.schlaframework.webmvc.autoconfig.AutoConfigRegistry;
import site.pushy.schlaframework.webmvc.exception.ConfigPropertiesException;
import site.pushy.schlaframework.webmvc.registry.InterceptorRegistry;
import site.pushy.schlaframework.webmvc.config.SchlaMvcConfigurer;
import site.pushy.schlaframework.webmvc.config.WebSocketConfigurer;
import site.pushy.schlaframework.webmvc.registry.WebSocketHandlerRegistry;
import site.pushy.schlaframework.webmvc.core.CustomAnnotationScanner;
import site.pushy.schlaframework.webmvc.autoconfig.PropertiesConfigReader;
import site.pushy.schlaframework.webmvc.component.MongoComponent;
import site.pushy.schlaframework.webmvc.component.RedisComponent;
import site.pushy.schlaframework.webmvc.handler.MappingHandler;
import site.pushy.schlaframework.webmvc.netty.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import site.pushy.schlaframework.webmvc.util.StringUtil;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

/**
 * @author Pushy
 * @since 2019/3/7 12:35
 */
public class SchlaWebmvcApplication {

    private static Logger logger = LogManager.getLogger(SchlaWebmvcApplication.class);

    private static AnnotationConfigApplicationContext context;

    private static PropertiesConfigReader config;

    public static void run(Class<?> primarySource) {
        if (primarySource == null) {
            throw new ConfigPropertiesException("The primarySource cannot be null.");
        }
        printBanner();

        try {
            // 获取客户配置文件
            config = new PropertiesConfigReader();
            config.read(primarySource);
            AutoConfigRegistry.setReader(config);

            context = new AnnotationConfigApplicationContext();
            context.register(MybatisAutoConfiguration.class);
            context.scan(config.getBasePackage());
            registerMongoComponent(primarySource);
            registerRedisComponent(primarySource);
            context.refresh();

            List<Class<? extends Annotation>> customAnnotation =
                    Arrays.asList(Controller.class, RestController.class);
            // 创建 CustomAnnotationScanner 对象，通过Spring扫描自定义注解类
            CustomAnnotationScanner serviceScanner = new CustomAnnotationScanner(context);
            serviceScanner.registerTypeFilter(customAnnotation);
            serviceScanner.scan(config.getBasePackage());

            registerInterceptor();
            WebSocketHandlerRegistry webSocketRegistry = registerWebSocket();

            MappingHandler mappingHandler = new MappingHandler(context, config);
            mappingHandler.doHandle();

            HttpServer.setAppContext(context);
            HttpServer.setWebSocketRegistry(webSocketRegistry);
            // 启动Netty HTTP服务器
            HttpServer.run(config.getHost(), config.getPort());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static WebSocketHandlerRegistry registerWebSocket() {
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

    private static void registerInterceptor() {
        try {
            // 获取客户的JobnessMvcConfigurer配置类
            SchlaMvcConfigurer webmvcConfigurer = context.getBean(SchlaMvcConfigurer.class);
            // 手动注册 InterceptorRegistry Bean
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(InterceptorRegistry.class);
            context.registerBeanDefinition(InterceptorRegistry.class.getSimpleName(),
                    builder.getBeanDefinition());
            // 调用客户配置类的addInterceptors，配置拦截器
            InterceptorRegistry interceptorRegistry = context.getBean(InterceptorRegistry.class);
            webmvcConfigurer.addInterceptors(interceptorRegistry);
        } catch (NoSuchBeanDefinitionException e) {
            logger.debug("No configure interceptors");
        }
    }

    private static void registerMongoComponent(Class<?> primarySource) {
        if (primarySource.isAnnotationPresent(EnableMongoComponent.class)) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(MongoComponent.class);
            builder.addConstructorArgValue(config.getMongoDatabaseName());
            context.registerBeanDefinition(MongoComponent.class.getSimpleName(),
                    builder.getBeanDefinition());
        }
    }

    private static void registerRedisComponent(Class<?> primarySource) {
        if (primarySource.isAnnotationPresent(EnableRedisComponent.class)) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(RedisComponent.class);
            context.registerBeanDefinition(RedisComponent.class.getSimpleName(),
                    builder.getBeanDefinition());
        }
    }

    private static void printBanner() {
        System.out.println("\n" +
                "  ______         _      _         \n" +
                " / _____)       | |    | |        \n" +
                "( (____    ____ | |__  | |  _____ \n" +
                " \\____ \\  / ___)|  _ \\ | | (____ |\n" +
                " _____) )( (___ | | | || | / ___ |\n" +
                "(______/  \\____)|_| |_| \\_)\\_____|\n" +
                "                                  \n");
        System.out.println(":: schla-framework webmvc ::         powered by Pushy see https://pushy.site");
    }

}
