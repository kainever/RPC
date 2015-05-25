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
 *  �ڿͻ��� ���÷����car�еķ���
 *  Movable�ӿڶԷ���˺Ϳͻ��˶��ǿɼ���
 *  client �� server �İ����Ӱ� �˴� ���ǲ��ɼ��ģ���
 */
public class RPCClient{

	public static void main(String[] args) {
		Movable m = (Movable) ProxyUtil.getProxy(Movable.class, "127.0.0.1", 65534);
System.out.println(m.getClass());
		System.out.println(m.loadMax(3));
	}
	
}
