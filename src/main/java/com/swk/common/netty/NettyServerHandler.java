package com.swk.common.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by fuyuwei on 2017/9/15.
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("server channelRead...");
        System.out.println(channelHandlerContext.channel().remoteAddress()+"--Server:"+o.toString());
        channelHandlerContext.write("server write:"+o);
        channelHandlerContext.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
        throwable.printStackTrace();
        channelHandlerContext.close();
    }
}
