package com.rpc.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {

	public static void main(String[] args) {
		try {
			initServer(65534);
			Listen();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static Selector selector;

	static void initServer(int port) throws IOException {
		// 首先得打开 通道
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		// 非阻塞模式
		ssChannel.configureBlocking(false);
		// 连接
		ssChannel.bind(new InetSocketAddress(port));
		// 打开选择器
		selector = Selector.open();
		// 通道与选择器关联！
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);
	}

/*
 * 服务端是怎么处理同时来自多个客户端的请求？
 * 	同一时刻，有多少个通道被selector监测已经准备就绪了，就会被
 * 抓住，然后通过selectedKeys()返回他们的key-set
 * 在遍历这个set ,对每一个与key相关的channel进行相应的处理
 */
	
	private static void Listen() throws IOException {
System.out.println("服务端线程" + Thread.currentThread().getName() + " 启动成功...." );
		// 阻塞式的方法
		while (selector.select() >= 0) {
			Set<SelectionKey> sKeys = selector.selectedKeys();
			Iterator<SelectionKey> it = sKeys.iterator();
			// 遍历
			while (it.hasNext()) {
				SelectionKey key = it.next();
				it.remove();
				// key的四种可能取值 ， 安排四种处理事件
				if (key.isAcceptable()) {
					acceptEvent(key);
				} else if (key.isConnectable()) {
					connectEvent(key);
				} else if (key.isReadable()) {
					readEvent(key);
				} else if (key.isWritable()) {
					writeEvent(key);
				}
			}
		}
	}
	

	private static void acceptEvent(SelectionKey key) throws IOException {
		ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
		SocketChannel sChannel = ssChannel.accept();
		if(sChannel.isBlocking()) {
			sChannel.configureBlocking(false);
		}
System.out.println(Thread.currentThread().getName() +  "  服务端接收连接成功.....");
		sChannel.register(selector, SelectionKey.OP_WRITE);
	}

	private static void connectEvent(SelectionKey key) {
		// TODO Auto-generated method stub
		
	}

	private static  void readEvent(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		socketChannel.configureBlocking(false);
		ByteBuffer buffer = ByteBuffer.allocate(500);
	    socketChannel.read(buffer);
	    String readInfo = new String(buffer.array());
 System.out.println(readInfo);
 		socketChannel.register(selector, SelectionKey.OP_WRITE);
	}

	private static void writeEvent(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		socketChannel.configureBlocking(false);
		ByteBuffer buffer = ByteBuffer.allocate(500);
		String input = "服务端写入到客户端";
		buffer = ByteBuffer.wrap(input.getBytes());
		socketChannel.write(buffer);
		socketChannel.register(selector, SelectionKey.OP_READ);
	}

	

}
