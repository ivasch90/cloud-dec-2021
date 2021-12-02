package com.geekbrains.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client connected...");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client disconnected...");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        log.debug("Received: {}", s);
        ctx.writeAndFlush("Hello " + s);
    }
}
