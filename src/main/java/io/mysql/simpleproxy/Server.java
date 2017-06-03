package io.mysql.simpleproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class Server {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private static Server instance;
    
    private SystemConfig systemConf;
    private Acceptor acceptor;
    private Connector connector;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    
    private String host = "127.0.0.1";
    private int port = 8066;
    
    public static Server getInstance() {
        return instance;
    }
    
    public Server() {
        systemConf = new SystemConfig();
        systemConf.load();
        bossGroup = new NioEventLoopGroup(1); // for accepting frontend connection
        workGroup = new NioEventLoopGroup();
        connector = new Connector(workGroup);
        acceptor = new Acceptor(bossGroup, workGroup, host, port);
        instance = this;
    }
    
    public void start() {
        try {
            acceptor.start();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
    
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
    
    public Connector getConnector() {
        return this.connector;
    }
    
    public SystemConfig getSystemConfig() {
        return this.systemConf;
    }

}
