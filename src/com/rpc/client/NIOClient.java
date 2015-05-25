package com.rpc.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.rpc.protocal.Invocation;
import com.rpc.util.SerializableUtil;

public class NIOClient implements Client {
	private String ip;
	private int port;
	private Selector selector;
	

 // 启动一个客户端
	public NIOClient(String ip, int port) throws IOException {
		initClient(ip , port);
	}
	
	@Override
	public void initClient (String ip , int port) throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		socketChannel.connect(new InetSocketAddress(ip , port));
		selector = Selector.open();
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
	}
	
	
	/*
	 *将客户端的方法调用信息传递给服务端
	 *服务端调用 并返回结果
	 */
	@Override
	public void transformAndCall(Invocation invocation) {
		try {
			Listen(invocation);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	boolean isWrite;
	private void Listen(Invocation invocation) throws IOException {
		while(invocation != null) {
			selector.select();
			Set<SelectionKey> sKeys = selector.selectedKeys();
			Iterator<SelectionKey> it = sKeys.iterator();
			while(it.hasNext()) {
				SelectionKey key = it.next();
				it.remove();
				if(key.isConnectable()) {
					connectEvent(key);
				} else if(key.isReadable()) {
					readEvent(key , invocation);
				}  else if(key.isWritable() && ! isWrite) {
					writeEvent(key , invocation);
				}
			}
System.out.println("fskfksji  " + invocation.getResult());
		   if(invocation.getResult() != null) {
			   System.out.println("客户端得到结果 " + invocation.getResult());
			   return ;
		   }
		}
	}

/*
 * 写事件
 */
	private void writeEvent(SelectionKey key, Invocation invocation) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		socketChannel.configureBlocking(false);
// 缓冲区 读写 		
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		byte[] bytes = SerializableUtil.toByte(invocation);
		socketChannel.write(ByteBuffer.wrap(bytes));
System.out.println("client 写入请求 成功");
//在此注册感兴趣的事件		
		socketChannel.register(selector, SelectionKey.OP_READ);
	}

/*
 * 读事件
 */
	private  void readEvent(SelectionKey key, Invocation invocation) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		socketChannel.configureBlocking(false);
		ByteBuffer buffer = ByteBuffer.allocate(1024);
	    socketChannel.read(buffer);
	    byte[] bytes = buffer.array();
//	 千万注意这个地方 不要直接把值赋给 invocation   
	    Invocation invoTmp = (Invocation) SerializableUtil.toObject(bytes);
	    invocation.setResult(invoTmp.getResult());
System.out.println("客户端接收到结果.." + invocation.getResult());
	 	socketChannel.register(selector, SelectionKey.OP_WRITE);
	}
	

/*
 * 连接事件
 */
	private void connectEvent(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
//		正在连接中！
		if(socketChannel.isConnectionPending()) {
			socketChannel.finishConnect();
		}
		
		if(socketChannel.isBlocking()) {
			socketChannel.configureBlocking(false);
		}
		
		if(socketChannel.isConnected()) {
			System.out.println(Thread.currentThread().getName()   + "   成功建立了连接");
		}
// 连接后将感兴趣的事情注册为读 和写
		socketChannel.register(selector, SelectionKey.OP_WRITE);
	}
	
}
