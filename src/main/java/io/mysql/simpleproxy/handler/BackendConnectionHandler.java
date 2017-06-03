package io.mysql.simpleproxy.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mysql.simpleproxy.Session;
import io.mysql.simpleproxy.protocol.ErrorPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class BackendConnectionHandler extends ChannelInboundHandlerAdapter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendConnectionHandler.class);
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.info("on frontend channel [{}] execption : {}", ctx.channel().id(), cause);
        // write error packet to frontend 
        Session session = ctx.channel().attr(Session.SESSION_KEY).get();
        if (session != null) {
            Channel frontend = session.frontend();
            ErrorPacket.build(3000, cause.getMessage()).write(frontend, true);
        }
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("on backend channel [{}] active", ctx.channel().id());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Session session = ctx.channel().attr(Session.SESSION_KEY).get();
//        assert (session != null);
        Channel frontend = session.frontend();
//        assert (frontend != null);
        LOGGER.info("on backend channel [{}] read, wirte data directly to frontend [{}], data:\r\n{}",
                ctx.channel().id(), frontend.id(), ByteBufUtil.prettyHexDump((ByteBuf) msg));
        frontend.write(msg);
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("on backend channel [{}] read complete", ctx.channel().id());
        Session session = ctx.channel().attr(Session.SESSION_KEY).get();
        Channel frontend = session.frontend();
        frontend.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("on backend channel [{}] registered", ctx.channel().id());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("on backend channel [{}] unregistered", ctx.channel().id());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("on backend channel [{}] inactive", ctx.channel().id());
    }
    
}
