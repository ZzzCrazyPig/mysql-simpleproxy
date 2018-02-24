package io.mysql.simpleproxy;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mysql.simpleproxy.handler.BackendConnectionHandler;
import io.mysql.simpleproxy.handler.BackendConnectionLogHandler;
import io.mysql.simpleproxy.protocol.ErrorPacket;
import io.mysql.simpleproxy.utils.IpUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Connector {

    private static final Logger LOGGER = LoggerFactory.getLogger(Connector.class);
    private Bootstrap bootstrap;
    
    public Connector(EventLoopGroup group) {
    	final BackendConnectionLogHandler logHandler = new BackendConnectionLogHandler();
    	final BackendConnectionHandler mainHandler = new BackendConnectionHandler();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
        	.channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
            	
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(logHandler, mainHandler);
                }
                
            });
    }
    
    public void connect(String host, int port, final Channel frontend) {
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
        ChannelFutureListener listener = new ChannelFutureListener() {
            
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    Channel backend = future.channel();
                    Session session = new Session();
                    LOGGER.info("on channel connect future operationComplete, bind channel, frontend : [{}], backend : [{}]",
                            IpUtil.getRemoteAddress(frontend), IpUtil.getAddress(backend));
                    session.bind(frontend, backend);
                    backend.attr(Session.SESSION_KEY).set(session);
                    frontend.attr(Session.SESSION_KEY).set(session);
                } else {
                    LOGGER.error("channel connect fail", future.cause());
                    ErrorPacket.build(2003, future.cause().getMessage()).write(frontend, true);
                }
            }
        };
        future.addListener(listener);
    }
    
}
