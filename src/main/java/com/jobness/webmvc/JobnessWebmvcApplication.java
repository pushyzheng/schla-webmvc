package com.jobness.webmvc;

import com.jobness.webmvc.annotation.RequestMapping;
import com.jobness.webmvc.annotation.RestController;
import com.jobness.webmvc.autoconfig.MybatisAutoConfiguration;
import com.jobness.webmvc.autoconfig.AutoConfigRegistry;
import com.jobness.webmvc.core.CustomAnnotationScanner;
import com.jobness.webmvc.core.MappingRegistry;
import com.jobness.webmvc.autoconfig.PropertiesConfigReader;
import com.jobness.webmvc.handler.MappingHandler;
import com.jobness.webmvc.netty.HttpServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Pushy
 * @since 2019/3/7 12:35
 */
public class JobnessWebmvcApplication {

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

            MappingHandler mappingHandler = new MappingHandler(context);
            mappingHandler.doHandleMapping();

            // 启动Netty HTTP服务器
            HttpServer.run(config.getHost(), config.getPort());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printBanner() {
        System.out.println("\n" +
                "   _____         _                               \n" +
                "  (_____)       | |                              \n" +
                "     _     ___  | | _   ____    ____   ___   ___ \n" +
                "    | |   / _ \\ | || \\ |  _ \\  / _  ) /___) /___)\n" +
                " ___| |  | |_| || |_) )| | | |( (/ / |___ ||___ |\n" +
                "(____/    \\___/ |____/ |_| |_| \\____)(___/ (___/ \n" +
                "                                                 \n");
        System.out.println(":: Jobness webmvc ::");
    }

}
