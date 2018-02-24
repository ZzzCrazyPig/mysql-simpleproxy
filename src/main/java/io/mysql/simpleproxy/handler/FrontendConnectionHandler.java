package io.mysql.simpleproxy.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mysql.simpleproxy.Connector;
import io.mysql.simpleproxy.ProxyServer;
import io.mysql.simpleproxy.Session;
import io.mysql.simpleproxy.conf.SystemConfig;
import io.mysql.simpleproxy.protocol.ErrorPacket;
import io.mysql.simpleproxy.utils.IpUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class FrontendConnectionHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(FrontendConnectionHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
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
        Connector connector = ProxyServer.getInstance().getConnector();
        SystemConfig systemConf = ProxyServer.getInstance().getSystemConfig();
        connector.connect(systemConf.getMysqlHost(), systemConf.getMysqlPort(), frontend);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // directly write frontend data to backend real mysql connection
        Session session = ctx.channel().attr(Session.SESSION_KEY).get();
        if (session != null) {
            session.backend().write(msg);
        } else {
            logger.warn("can not found session, so close frontend channel [{}]", IpUtil.getRemoteAddress(ctx.channel()));
            ctx.channel().close();
        }
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        Session session = ctx.channel().attr(Session.SESSION_KEY).get();
        if (session != null) {
            session.backend().writeAndFlush(Unpooled.EMPTY_BUFFER);
        } else {
            logger.warn("can not found session, so close frontend channel [{}]", IpUtil.getRemoteAddress(ctx.channel()));
            ctx.channel().close();
        }
    }

}
