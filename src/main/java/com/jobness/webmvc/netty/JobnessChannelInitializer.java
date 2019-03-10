package com.jobness.webmvc.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.context.ApplicationContext;

/**
 * @author Pushy
 * @since 2019/3/7 12:38
 */
public class JobnessChannelInitializer extends ChannelInitializer<SocketChannel> {

    private ApplicationContext appContext;

    JobnessChannelInitializer(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());                                   // HTTP解编码器
        pipeline.addLast(new HttpObjectAggregator(512 * 1024));    // 聚合HTTP消息
        pipeline.addLast(new ChunkedWriteHandler());                               // 写文件
        pipeline.addLast(new NettyHttpRequestHandler(appContext));
    }
}
