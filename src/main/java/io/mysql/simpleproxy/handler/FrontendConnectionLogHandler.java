package io.mysql.simpleproxy.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mysql.simpleproxy.utils.IpUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class FrontendConnectionLogHandler extends ChannelInboundHandlerAdapter {
	
	private static Logger logger = LoggerFactory.getLogger(FrontendConnectionLogHandler.class);
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		logger.info("on frontend channel [{}] registered", IpUtil.getRemoteAddress(ctx.channel()));
		ctx.fireChannelRegistered();
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		logger.info("on frontend channel [{}] unregistered", IpUtil.getRemoteAddress(ctx.channel()));
		ctx.fireChannelUnregistered();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("on frontend channel [{}] active, will connect real backend mysql database", 
				IpUtil.getRemoteAddress(ctx.channel()));
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("on frontend channel [{}] inactive", IpUtil.getRemoteAddress(ctx.channel()));
		ctx.fireChannelInactive();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		logger.info("on frontend channel [{}] read, will write data directly to backend, data:\r\n {}",
				IpUtil.getRemoteAddress(ctx.channel()), ByteBufUtil.prettyHexDump((ByteBuf) msg));
		ctx.fireChannelRead(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		logger.info("on fronted channel [{}] readComplete", IpUtil.getRemoteAddress(ctx.channel()));
		ctx.fireChannelReadComplete();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.info("on frontend channel [{}] exception", IpUtil.getRemoteAddress(ctx.channel()), cause);
		ctx.fireExceptionCaught(cause);
	}

}
