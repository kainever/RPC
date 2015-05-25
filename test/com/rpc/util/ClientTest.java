package com.rpc.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
/*
 *  �ڿͻ��� ���÷����car�еķ���
 *  Movable�ӿڶԷ���˺Ϳͻ��˶��ǿɼ���
 *  client �� server �İ����Ӱ� �˴� ���ǲ��ɼ��ģ���
 */
public class ClientTest implements Runnable {

	public static void main(String[] args) {
		for(int i = 0 ; i < 100 ; i++) {
			new Thread(new ClientTest()).start();
		}
	}
	
/*
 *  ѡ���� �� ÿһ�� Channel����ע��õ���Ӧ��selector
 *  �����̲߳��ܶ�channel���й��� ��������ϵ��
 *  ���ǲ���ÿ���߳̾�ֻ��һ��selector���� ���ǿ����ж��	
 */
	Selector selector;
	
	
	void initClient (String ip , int port) throws IOException {
//		���ȵô� ͨ��
		SocketChannel socketChannel = SocketChannel.open();
//		������ģʽ
		socketChannel.configureBlocking(false);
//		���� ������ģʽ����false��ʾ�����У�����û�н�������
		boolean connect = socketChannel.connect(new InetSocketAddress(ip , port));
// ����ѡ����		
		selector = Selector.open();
// ͨ����ѡ����������		
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
	}
	
	
	/*
	 * ���м��� 
	 */
	
	void Listen() throws IOException {
		/*
		 * This method performs a blocking selection operation. 
		 * It returns only after at least one channel is selected, 
		 * this selector's wakeup method is invoked, or the current thread is interrupted, whichever comes first. 
		   select()����ʱ����ʽ�ģ�ֻ������һ��channel��ѡ��ʱ!	
		 */
		/*
		 * ����select���� ��
		 * Selectorͨ��select���������������ڸ�Selectorע�����Channel�Ķ�Ӧ�Ķ�����
		 * �����⵽ĳһ��Ӧ�Ķ������򷵻�selectedKeys���Լ��ֶ�ȡ������SelectionKey������Ӧ�Ĵ���
		 */
		while(true) {
			/*
			 * Ϊʲô�ܹ���������ѭ����ȥ�ˣ�����select����������
			 * �������˽���....
			 */
//	Selects a set of keys whose corresponding channels are ready for I/O operations
//	���� ֱ�ӵ�һ�仰
//	�������Ļ���������ע��ĳһ������Ȥ���������ô��channel�Ը��¼���׼��������..
//	��ʼ��ʱ�� �� ע���ʱ��connect�¼���Ȼ��ע��д�¼�����ô����һ�����whileѭ����ʱ��
//	�ͻ��⵽д�¼��Ѿ�׼�������ˣ�������Ӧ��д�¼�����.... ���....		
			selector.select();
			Set<SelectionKey> sKeys = selector.selectedKeys();
			Iterator<SelectionKey> it = sKeys.iterator();
//����			
			while(it.hasNext()) {
				SelectionKey key = it.next();
				it.remove();
//	key�����ֿ���ȡֵ �� �������ִ����¼�
				if(key.isAcceptable()) {
					acceptEvent(key);
				} else if(key.isConnectable()) {
					connectEvent(key);
				} else if(key.isReadable()) {
					readEvent(key);
				}  else if(key.isWritable()) {
					writeEvent(key);
				}
			}
		}
	}


	private void writeEvent(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		socketChannel.configureBlocking(false);
// ������ ��д 		
		ByteBuffer buffer = ByteBuffer.allocate(500);
		String input = Thread.currentThread().getName() + " �ͻ���д�뵽�����";
		buffer = ByteBuffer.wrap(input.getBytes());
		socketChannel.write(buffer);
//�ڴ�ע�����Ȥ���¼�		
		socketChannel.register(selector, SelectionKey.OP_READ);
	}


	private  void readEvent(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		socketChannel.configureBlocking(false);
		ByteBuffer buffer = ByteBuffer.allocate(20);
	    socketChannel.read(buffer);
	    String readInfo = new String(buffer.array());
 System.out.println(Thread.currentThread().getName() + " " + readInfo);
	 	socketChannel.register(selector, SelectionKey.OP_WRITE);
	}


	private void connectEvent(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
//		���������У�
		if(socketChannel.isConnectionPending()) {
/*
 * A non-blocking connection operation is initiated(��ʼ) by placing a socket 
 * channel in non-blocking mode and then invoking its connect method. Once 
 * the connection is established, or the attempt has failed, the socket channel 
 * will become connectable and this method may be invoked to complete the connection sequence.
 */
			socketChannel.finishConnect();
		}
		
		if(socketChannel.isBlocking()) {
			socketChannel.configureBlocking(false);
		}
		
		if(socketChannel.isConnected()) {
			System.out.println(Thread.currentThread().getName()   + "   �ɹ�����������");
		}
//		������Ȥ������ע��Ϊд
		socketChannel.register(selector, SelectionKey.OP_WRITE);
	}

	private void acceptEvent(SelectionKey key) {
		
	}


	@Override
	public void run() {
		try {
			initClient("localhost" , 65534);
			Listen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	

}
