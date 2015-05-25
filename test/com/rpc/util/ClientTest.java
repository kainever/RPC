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
 *  在客户端 调用服务端car中的方法
 *  Movable接口对服务端和客户端都是可见的
 *  client 和 server 的包或子包 彼此 都是不可见的！！
 */
public class ClientTest implements Runnable {

	public static void main(String[] args) {
		for(int i = 0 ; i < 100 ; i++) {
			new Thread(new ClientTest()).start();
		}
	}
	
/*
 *  选择器 ， 每一个 Channel必须注册得到相应的selector
 *  这样线程才能对channel进行管理 ，建立联系！
 *  那是不是每个线程就只有一个selector？？ 还是可以有多个	
 */
	Selector selector;
	
	
	void initClient (String ip , int port) throws IOException {
//		首先得打开 通道
		SocketChannel socketChannel = SocketChannel.open();
//		非阻塞模式
		socketChannel.configureBlocking(false);
//		连接 非阻塞模式返回false表示连接中！！并没有建立连接
		boolean connect = socketChannel.connect(new InetSocketAddress(ip , port));
// 代开选择器		
		selector = Selector.open();
// 通道与选择器关联！		
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
	}
	
	
	/*
	 * 进行监听 
	 */
	
	void Listen() throws IOException {
		/*
		 * This method performs a blocking selection operation. 
		 * It returns only after at least one channel is selected, 
		 * this selector's wakeup method is invoked, or the current thread is interrupted, whichever comes first. 
		   select()方法时阻塞式的，只有至少一个channel被选中时!	
		 */
		/*
		 * 关于select方法 ：
		 * Selector通过select操作，监视所有在该Selector注册过的Channel的对应的动作，
		 * 如果监测到某一对应的动作，则返回selectedKeys，自己手动取到各个SelectionKey进行相应的处理。
		 */
		while(true) {
			/*
			 * 为什么能够这样无线循环下去了？不是select是阻塞的吗？
			 * 下面做了解释....
			 */
//	Selects a set of keys whose corresponding channels are ready for I/O operations
//	核心 直接的一句话
//	这样理解的话，当我们注册某一个感兴趣的事情后，那么该channel对该事件就准备就绪了..
//	开始的时候 ， 注册的时候connect事件，然后注册写事件，那么在下一次外层while循环的时候
//	就会监测到写事件已经准备就绪了！进行相应的写事件处理.... 如此....		
			selector.select();
			Set<SelectionKey> sKeys = selector.selectedKeys();
			Iterator<SelectionKey> it = sKeys.iterator();
//遍历			
			while(it.hasNext()) {
				SelectionKey key = it.next();
				it.remove();
//	key的四种可能取值 ， 安排四种处理事件
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
// 缓冲区 读写 		
		ByteBuffer buffer = ByteBuffer.allocate(500);
		String input = Thread.currentThread().getName() + " 客户端写入到服务端";
		buffer = ByteBuffer.wrap(input.getBytes());
		socketChannel.write(buffer);
//在此注册感兴趣的事件		
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
//		正在连接中！
		if(socketChannel.isConnectionPending()) {
/*
 * A non-blocking connection operation is initiated(开始) by placing a socket 
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
			System.out.println(Thread.currentThread().getName()   + "   成功建立了连接");
		}
//		将感兴趣的事情注册为写
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
