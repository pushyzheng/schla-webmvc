package com.jobless.webmvc.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.context.ApplicationContext;

/**
 * @author Pushy
 * @since 2019/3/7 12:40
 */
public class HttpServer {

    private static ApplicationContext appContext;

    private static void start(final String host, final int port) {
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new JobnessChannelInitializer(appContext));
            ChannelFuture f = b.bind(host, port).sync();
            f.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        System.out.println("Running on http://" + host + ":" + port);
                    } else {
                        System.out.println("Failed to run the application");
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

    public static void run(String host, int port) {
        start(host, port);
    }

}
