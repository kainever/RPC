package com.rpc.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.rpc.protocal.Invocation;
import com.rpc.protocal.SeriaMethod;
import com.rpc.util.SerializableUtil;

public class NIOServer implements Server{
	
	private int port;
	private Selector selector;
/*
 * 接口与实现类之间的对应关系，要不然怎么能够根据接口来得到具体的实现类了....
 * 问题，如果一个接口多个实现类...????
 */
	private Map<String , Object> implClass = new HashMap<String , Object> ();

	public NIOServer(int port) {
		this.port = port;
	}

/*
 * 启动服务器	
 */
	@Override
	public void start() {
		try {
			initServer(this.port);
			System.out.println("服务端线程" + Thread.currentThread().getName() + " 启动成功...." );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
/*
 * 注册接口的实现类	
 */
	@Override 
	public void Register(Class inter, Class impl) {
		try {
			implClass.put(inter.getName(), impl.newInstance());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
System.out.println("成功注册实现类Car");
	}
	

	private void initServer(int port) throws IOException {
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		ssChannel.configureBlocking(false);
		ssChannel.bind(new InetSocketAddress(port));
		selector = Selector.open();
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);
	}
	
/*
 * 	启动服务器后开始监控
 */
	@Override
	public void Monitor() throws IOException {
System.out.println("正在监听.....");
		while (selector.select() >= 0) {
			Set<SelectionKey> sKeys = selector.selectedKeys();
			Iterator<SelectionKey> it = sKeys.iterator();
			while (it.hasNext()) {
				SelectionKey key = it.next();
				it.remove();
				if (key.isAcceptable()) {
					acceptEvent(key);
				} else if (key.isReadable()) {
					readEvent(key);
				} else if (key.isWritable()) {
					writeEvent(key);
				}
			}
		}
	}
	

	private void writeEvent(SelectionKey key) {
		
	}

/*
 * 读取来自客户端的过程调用	
 */
	private void readEvent(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		socketChannel.configureBlocking(false);
		ByteBuffer buffer = ByteBuffer.allocate(1024);
	    socketChannel.read(buffer);
	    
	    byte[] bytes = buffer.array();
	    Invocation invo = (Invocation) SerializableUtil.toObject(bytes);
System.out.println("调用方法信息..." + invo);
// 		socketChannel.register(selector, SelectionKey.OP_WRITE);
		if(invo != null) {
 // 服务端调用		
			invoke(invo);
// 	结果写会给客户端	
			writeToClient(invo , socketChannel);
		}
	}
	

/*
 * 服务单解析请求
 * 调用服务，返回结果	
 */

	@Override
    public  void invoke(Invocation invo) {
		Class interfaces = invo.getInterfaces();
		SeriaMethod method = invo.getMethod();
		Object[] args = invo.getParameters();
// 得到该接口的实现对象		
		Object obj = implClass.get(interfaces.getName());
// 得到相应的方法		
		Method m = null;
		try {
			m = obj.getClass().getMethod(method.getMethodName(), method.getParams());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
//调用方法
		Object result = null;
		try {
			result = m.invoke(obj, args);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		invo.setResult(result);
System.out.println("返回结果  " + result);
	}
	
	
	private void writeToClient(Invocation invo , SocketChannel socketChannel) {
// 缓冲区 读写 		
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		byte[] bytes = SerializableUtil.toByte(invo);
		try {
			socketChannel.write(ByteBuffer.wrap(bytes));
		} catch (IOException e) {
			e.printStackTrace();
		}
System.out.println("服务端成功返回结果  " + invo + "  " + invo.getResult() );
	}

    
/*
 * 接收连接	
 */
	private void acceptEvent(SelectionKey key) throws IOException {
		ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
		SocketChannel sChannel = ssChannel.accept();
		if(sChannel.isBlocking()) {
			sChannel.configureBlocking(false);
		}
		sChannel.register(selector, SelectionKey.OP_WRITE|SelectionKey.OP_READ);
	}
	
	
}
