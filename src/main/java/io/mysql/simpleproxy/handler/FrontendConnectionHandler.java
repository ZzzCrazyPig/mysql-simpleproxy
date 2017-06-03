package io.mysql.simpleproxy.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mysql.simpleproxy.Connector;
import io.mysql.simpleproxy.Server;
import io.mysql.simpleproxy.Session;
import io.mysql.simpleproxy.SystemConfig;
import io.mysql.simpleproxy.protocol.ErrorPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class FrontendConnectionHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontendConnectionHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.info("on frontend channel [{}] exception", ctx.channel().id(), cause);
        Session session = ctx.channel().attr(Session.SESSION_KEY).get();
        if (session == null) {
            ctx.channel().close();
        } else {
            ErrorPacket.build(3000, cause.getMessage()).write(ctx.channel(), true);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // connect real backend mysql database
        Channel frontend = ctx.channel();
        LOGGER.info("frontend channel [{}] active, will connect real backend mysql database", frontend.id());
        Connector connector = Server.getInstance().getConnector();
        SystemConfig systemConf = Server.getInstance().getSystemConfig();
        connector.connect(systemConf.getMysqlHost(), systemConf.getMysqlPort(), frontend);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info(
                "on frontend channel [{}] read, will write data directly to backend, data:\r\n {}",
                ctx.channel().id(), ByteBufUtil.prettyHexDump((ByteBuf) msg));
        // directly write frontend data to backend real mysql connection
        Session session = ctx.channel().attr(Session.SESSION_KEY).get();
        if (session != null) {
            session.backend().write(msg);
        }
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("on frontend channel [{}] read complete", ctx.channel().id());
        Session session = ctx.channel().attr(Session.SESSION_KEY).get();
        if (session != null) {
            session.backend().writeAndFlush(Unpooled.EMPTY_BUFFER);
        } else {
            LOGGER.warn("can not found session, so close frontend channel");;
            ctx.channel().close();
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("on frontend channel [{}] registered", ctx.channel().id());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("on frontend channel [{}] unregistered", ctx.channel().id());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("on frontend channel [{}] inactive", ctx.channel().id());
    }

}
