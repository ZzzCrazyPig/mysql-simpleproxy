package io.mysql.simpleproxy;

public class SystemConfig {
    
    private String mysqlHost = "localhost";
    private int mysqlPort = 3306;
    
    public void load() {
        // TODO load system properties
    }
    
    public String getMysqlHost() {
        return mysqlHost;
    }
    public void setMysqlHost(String mysqlHost) {
        this.mysqlHost = mysqlHost;
    }
    public int getMysqlPort() {
        return mysqlPort;
    }
    public void setMysqlPort(int mysqlPort) {
        this.mysqlPort = mysqlPort;
    }
    
}
