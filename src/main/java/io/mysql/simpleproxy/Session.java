package io.mysql.simpleproxy;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class Session {
    
    public static final AttributeKey<Session> SESSION_KEY = AttributeKey.valueOf("session");
    private Channel frontend;
    private Channel backend;
    
    public void bind(Channel frontend, Channel backend) {
        this.frontend = frontend;
        this.backend = backend;
    }
    
    public Channel backend() {
        return this.backend;
    }
    
    public Channel frontend() {
        return this.frontend;
    }

}
