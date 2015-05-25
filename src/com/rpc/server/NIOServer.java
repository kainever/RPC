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
 * �ӿ���ʵ����֮��Ķ�Ӧ��ϵ��Ҫ��Ȼ��ô�ܹ����ݽӿ����õ������ʵ������....
 * ���⣬���һ���ӿڶ��ʵ����...????
 */
	private Map<String , Object> implClass = new HashMap<String , Object> ();

	public NIOServer(int port) {
		this.port = port;
	}

/*
 * ����������	
 */
	@Override
	public void start() {
		try {
			initServer(this.port);
			System.out.println("������߳�" + Thread.currentThread().getName() + " �����ɹ�...." );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
/*
 * ע��ӿڵ�ʵ����	
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
System.out.println("�ɹ�ע��ʵ����Car");
	}
	

	private void initServer(int port) throws IOException {
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		ssChannel.configureBlocking(false);
		ssChannel.bind(new InetSocketAddress(port));
		selector = Selector.open();
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);
	}
	
/*
 * 	������������ʼ���
 */
	@Override
	public void Monitor() throws IOException {
System.out.println("���ڼ���.....");
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
 * ��ȡ���Կͻ��˵Ĺ��̵���	
 */
	private void readEvent(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		socketChannel.configureBlocking(false);
		ByteBuffer buffer = ByteBuffer.allocate(1024);
	    socketChannel.read(buffer);
	    
	    byte[] bytes = buffer.array();
	    Invocation invo = (Invocation) SerializableUtil.toObject(bytes);
System.out.println("���÷�����Ϣ..." + invo);
// 		socketChannel.register(selector, SelectionKey.OP_WRITE);
		if(invo != null) {
 // ����˵���		
			invoke(invo);
// 	���д����ͻ���	
			writeToClient(invo , socketChannel);
		}
	}
	

/*
 * ���񵥽�������
 * ���÷��񣬷��ؽ��	
 */

	@Override
    public  void invoke(Invocation invo) {
		Class interfaces = invo.getInterfaces();
		SeriaMethod method = invo.getMethod();
		Object[] args = invo.getParameters();
// �õ��ýӿڵ�ʵ�ֶ���		
		Object obj = implClass.get(interfaces.getName());
// �õ���Ӧ�ķ���		
		Method m = null;
		try {
			m = obj.getClass().getMethod(method.getMethodName(), method.getParams());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
//���÷���
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
System.out.println("���ؽ��  " + result);
	}
	
	
	private void writeToClient(Invocation invo , SocketChannel socketChannel) {
// ������ ��д 		
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		byte[] bytes = SerializableUtil.toByte(invo);
		try {
			socketChannel.write(ByteBuffer.wrap(bytes));
		} catch (IOException e) {
			e.printStackTrace();
		}
System.out.println("����˳ɹ����ؽ��  " + invo + "  " + invo.getResult() );
	}

    
/*
 * ��������	
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
