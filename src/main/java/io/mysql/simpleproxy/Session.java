package io.mysql.simpleproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mysql.simpleproxy.utils.IpUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;

public class Session {
	
	private static Logger logger = LoggerFactory.getLogger(Session.class);
    
    public static final AttributeKey<Session> SESSION_KEY = AttributeKey.valueOf("session");
    private Channel frontend;
    private Channel backend;
    
    private static class ConnectionCloseFutureListener implements GenericFutureListener<ChannelFuture> {

    	private Session session;
    	
    	public ConnectionCloseFutureListener(Session session) {
    		this.session = session;
    	}
    	
		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			Channel ch = future.channel();
			if (ch == session.frontend) {
				logger.info("frontend channel [{}] closed!", IpUtil.getRemoteAddress(session.frontend));
				// frontend connection close but it's backend connection is still active or open, close it!
				if (session.backend.isActive() || session.backend.isOpen()) {
					session.backend.close();
				}
			} else {
				logger.info("backend channel [{}] closed!", IpUtil.getRemoteAddress(session.backend));
				// backend connection close but it's frontend connection is still active or open, close it! 
				if (session.frontend.isActive() || session.frontend.isOpen()) {
					session.frontend.close();
				}
			}
		}
    	
    }
    
    public void bind(Channel frontend, Channel backend) {
        this.frontend = frontend;
        this.backend = backend;
        this.frontend.closeFuture().addListener(new ConnectionCloseFutureListener(this));
        this.backend.closeFuture().addListener(new ConnectionCloseFutureListener(this));
    }
    
    public Channel backend() {
        return this.backend;
    }
    
    public Channel frontend() {
        return this.frontend;
    }

}
