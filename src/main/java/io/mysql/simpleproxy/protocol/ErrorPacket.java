package io.mysql.simpleproxy.protocol;

import io.mysql.simpleproxy.utils.BufferUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class ErrorPacket extends MySQLPacket {
    
    private static final byte DEFAULT_SQLSTATE_MAKER = '#';
    private static final byte[] DEFAULT_SQLSTATE = "HY000".getBytes();
    
    public byte header = (byte) 0xff;
    public int errorCode;
    public byte sqlStateMaker = DEFAULT_SQLSTATE_MAKER;
    public byte[] sqlState = DEFAULT_SQLSTATE;
    public byte[] errorMessage;
    
    public static ErrorPacket build(int errorCode, String message) {
        ErrorPacket pkg = new ErrorPacket();
        pkg.packetId = 1;
        pkg.errorCode = errorCode;
        pkg.errorMessage = message.getBytes();
        return pkg;
    }
    
    public static ErrorPacket build(int errorCode) {
        ErrorPacket pkg = new ErrorPacket();
        pkg.packetId = 1;
        pkg.errorCode = errorCode;
        return pkg;
    }
    
    @Override
    public void write(Channel channel, boolean needFlush) {
        ByteBuf buffer = channel.alloc().buffer();
        // 写err packet 到 bytebuf 中
        BufferUtil.writeUB3(buffer, calculatePacketLength());
        buffer.writeByte(this.packetId);
        buffer.writeByte(this.header);
        BufferUtil.writeUB2(buffer, this.errorCode);
        buffer.writeByte(this.sqlStateMaker);
        buffer.writeBytes(this.sqlState);
        if (this.errorMessage != null) {
            buffer.writeBytes(this.errorMessage);
        }
        if (needFlush) {
            channel.writeAndFlush(buffer);
        } else {
            channel.write(buffer);
        }
    }

    @Override
    public int calculatePacketLength() {
        int size = 9;
        if (errorMessage != null) {
            size += errorMessage.length;
        }
        return size;
    }

}
