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
public class BackendConnectionLogHandler extends ChannelInboundHandlerAdapter {
	
	private static Logger logger = LoggerFactory.getLogger(BackendConnectionLogHandler.class);
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		logger.info("on backend channel [{}] registered", IpUtil.getAddress(ctx.channel()));
		ctx.fireChannelRegistered();
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		logger.info("on backend channel [{}] unregistered", IpUtil.getAddress(ctx.channel()));
		ctx.fireChannelUnregistered();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("on backend channel [{}] active", IpUtil.getAddress(ctx.channel()));
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("on backend channel [{}] inactive", IpUtil.getAddress(ctx.channel()));
		ctx.fireChannelInactive();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		logger.info("on backend channel [{}] read, will write data directly to frontend, data:\r\n {}",
                IpUtil.getAddress(ctx.channel()), ByteBufUtil.prettyHexDump((ByteBuf) msg));
		ctx.fireChannelRead(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		logger.info("on backend channel [{}] readComplete", IpUtil.getAddress(ctx.channel()));
		ctx.fireChannelReadComplete();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.info("on backend channel [{}] exception", IpUtil.getAddress(ctx.channel()), cause);
		ctx.fireExceptionCaught(cause);
	}

}
