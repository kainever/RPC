package com.rpc.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.rpc.common.Movable;

public class RPCServer {

	public static void main(String[] args) {
		int port = 65534;
		NIOServer server = new NIOServer(port);
		server.start();
//		зЂВс
		server.Register(Movable.class, Car.class);
		try {
			server.Monitor();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
