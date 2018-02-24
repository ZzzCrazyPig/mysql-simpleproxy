package io.mysql.simpleproxy.utils;

import java.net.InetSocketAddress;

import io.netty.channel.Channel;

public class IpUtil {
	
	public static String getRemoteAddress(Channel channel) {
		if (channel == null) {
			return null;
		}
		InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
		if (address == null) {
			return null;
		}
		String hostname = address.getHostName();
		int port = address.getPort();
		return hostname + ":" + port;
	}
	
	public static String getLocalAddress(Channel channel) {
		if (channel == null) {
			return null;
		}
		InetSocketAddress address = (InetSocketAddress) channel.localAddress();
		if (address == null) {
			return null;
		}
		String hostname = address.getHostName();
		int port = address.getPort();
		return hostname + ":" + port;
	}
	
	public static String getAddress(Channel channel) {
		String localAddress = getLocalAddress(channel);
		String remoteAddress = getRemoteAddress(channel);
		return "LA:" + localAddress + ", RA:" + remoteAddress;
	}
	
}
