package com.rpc.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.rpc.common.Movable;
import com.rpc.util.ProxyUtil;
/*
 *  在客户端 调用服务端car中的方法
 *  Movable接口对服务端和客户端都是可见的
 *  client 和 server 的包或子包 彼此 都是不可见的！！
 */
public class RPCClient{

	public static void main(String[] args) {
		Movable m = (Movable) ProxyUtil.getProxy(Movable.class, "127.0.0.1", 65534);
System.out.println(m.getClass());
		System.out.println(m.loadMax(3));
	}
	
}
