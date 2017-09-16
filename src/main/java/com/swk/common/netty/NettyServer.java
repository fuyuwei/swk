package com.swk.common.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Created by fuyuwei on 2017/9/15.
 */
public class NettyServer {
    private int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public  void start(){
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap().group(bossGroup,workerGroup).
                    channel(NioServerSocketChannel.class).
                    localAddress(port).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast("decoder",new StringDecoder());
                    socketChannel.pipeline().addLast("encoder",new StringEncoder());
                    socketChannel.pipeline().addLast(new NettyServerHandler());
                }
            }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE,true);
            // 绑定端口，开始接收进来的连接
            ChannelFuture future = serverBootstrap.bind(port).sync();
            System.out.println("Server start listen at:"+port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        int port;
        if(args.length > 0){
            port = Integer.parseInt(args[0]);
        }else{
            port = 8088;
        }
        new NettyServer(port).start();

    }
}
