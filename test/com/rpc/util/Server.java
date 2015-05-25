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
		// ���ȵô� ͨ��
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		// ������ģʽ
		ssChannel.configureBlocking(false);
		// ����
		ssChannel.bind(new InetSocketAddress(port));
		// ��ѡ����
		selector = Selector.open();
		// ͨ����ѡ����������
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);
	}

/*
 * ���������ô����ͬʱ���Զ���ͻ��˵�����
 * 	ͬһʱ�̣��ж��ٸ�ͨ����selector����Ѿ�׼�������ˣ��ͻᱻ
 * ץס��Ȼ��ͨ��selectedKeys()�������ǵ�key-set
 * �ڱ������set ,��ÿһ����key��ص�channel������Ӧ�Ĵ���
 */
	
	private static void Listen() throws IOException {
System.out.println("������߳�" + Thread.currentThread().getName() + " �����ɹ�...." );
		// ����ʽ�ķ���
		while (selector.select() >= 0) {
			Set<SelectionKey> sKeys = selector.selectedKeys();
			Iterator<SelectionKey> it = sKeys.iterator();
			// ����
			while (it.hasNext()) {
				SelectionKey key = it.next();
				it.remove();
				// key�����ֿ���ȡֵ �� �������ִ����¼�
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
System.out.println(Thread.currentThread().getName() +  "  ����˽������ӳɹ�.....");
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
		String input = "�����д�뵽�ͻ���";
		buffer = ByteBuffer.wrap(input.getBytes());
		socketChannel.write(buffer);
		socketChannel.register(selector, SelectionKey.OP_READ);
	}

	

}
