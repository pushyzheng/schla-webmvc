package site.pushy.schlaframework.webmvc.netty;

import site.pushy.schlaframework.webmvc.config.WebSocketHandlerRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 * @author Pushy
 * @since 2019/3/7 12:40
 */
public class HttpServer {

    private static Logger logger = LogManager.getLogger(HttpServer.class);

    private static ApplicationContext appContext;

    private static WebSocketHandlerRegistry webSocketRegistry;

    private static void start(final String host, final int port) {
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new JobnessChannelInitializer(appContext, webSocketRegistry));
            ChannelFuture f = b.bind(host, port).sync();
            f.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        logger.info("Running on http://" + host + ":" + port);
                    } else {
                        logger.error("Failed to run the application");
                    }
                }
            });
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }

    public static void setAppContext(ApplicationContext applicationContext) {
        appContext = applicationContext;
    }

    public static void setWebSocketRegistry(WebSocketHandlerRegistry registry) {
        webSocketRegistry = registry;
    }

    public static void run(String host, int port) {
        start(host, port);
    }

}
