package io.mysql.simpleproxy.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mysql.simpleproxy.Session;
import io.mysql.simpleproxy.protocol.ErrorPacket;
import io.mysql.simpleproxy.utils.IpUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class BackendConnectionHandler extends ChannelInboundHandlerAdapter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendConnectionHandler.class);
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // write error packet to frontend 
        Session session = ctx.channel().attr(Session.SESSION_KEY).get();
        if (session != null) {
            Channel frontend = session.frontend();
            ErrorPacket.build(3000, cause.getMessage()).write(frontend, true);
        } else {
        	LOGGER.warn("can not find frontend connection of backend [{}]", IpUtil.getAddress(ctx.channel()));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Session session = ctx.channel().attr(Session.SESSION_KEY).get();
        Channel frontend = session.frontend();
        frontend.write(msg);
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        Session session = ctx.channel().attr(Session.SESSION_KEY).get();
        Channel frontend = session.frontend();
        frontend.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

}
