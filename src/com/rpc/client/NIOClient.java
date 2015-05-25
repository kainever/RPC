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
	

 // ����һ���ͻ���
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
	 *���ͻ��˵ķ���������Ϣ���ݸ������
	 *����˵��� �����ؽ��
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
			   System.out.println("�ͻ��˵õ���� " + invocation.getResult());
			   return ;
		   }
		}
	}

/*
 * д�¼�
 */
	private void writeEvent(SelectionKey key, Invocation invocation) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		socketChannel.configureBlocking(false);
// ������ ��д 		
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		byte[] bytes = SerializableUtil.toByte(invocation);
		socketChannel.write(ByteBuffer.wrap(bytes));
System.out.println("client д������ �ɹ�");
//�ڴ�ע�����Ȥ���¼�		
		socketChannel.register(selector, SelectionKey.OP_READ);
	}

/*
 * ���¼�
 */
	private  void readEvent(SelectionKey key, Invocation invocation) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		socketChannel.configureBlocking(false);
		ByteBuffer buffer = ByteBuffer.allocate(1024);
	    socketChannel.read(buffer);
	    byte[] bytes = buffer.array();
//	 ǧ��ע������ط� ��Ҫֱ�Ӱ�ֵ���� invocation   
	    Invocation invoTmp = (Invocation) SerializableUtil.toObject(bytes);
	    invocation.setResult(invoTmp.getResult());
System.out.println("�ͻ��˽��յ����.." + invocation.getResult());
	 	socketChannel.register(selector, SelectionKey.OP_WRITE);
	}
	

/*
 * �����¼�
 */
	private void connectEvent(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
//		���������У�
		if(socketChannel.isConnectionPending()) {
			socketChannel.finishConnect();
		}
		
		if(socketChannel.isBlocking()) {
			socketChannel.configureBlocking(false);
		}
		
		if(socketChannel.isConnected()) {
			System.out.println(Thread.currentThread().getName()   + "   �ɹ�����������");
		}
// ���Ӻ󽫸���Ȥ������ע��Ϊ�� ��д
		socketChannel.register(selector, SelectionKey.OP_WRITE);
	}
	
}
