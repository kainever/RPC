package com.rpc.util;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.rpc.client.Client;
import com.rpc.client.NIOClient;
import com.rpc.protocal.Invocation;
import com.rpc.protocal.SeriaMethod;

/*
 *  客户端生成代理类
 */
public class ProxyUtil {
	
	public static Object getProxy(Class clazz , String ip , int port) {
		Client client = null;
		try {
			client = new NIOClient(ip , port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ClientRPCHandler handler = new ClientRPCHandler(client , clazz);
		return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),new Class[]{clazz}, handler);
	}
	
	
	public static class ClientRPCHandler implements InvocationHandler {
		Client client ;
		Class clazz;
		
		public ClientRPCHandler(Client client, Class clazz) {
			super();
			this.client = client;
			this.clazz = clazz;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			// 必须把这些方法调用信息传递给服务端..
			Invocation invocation = new Invocation();
			invocation.setInterfaces(this.clazz);
			invocation.setMethod(new SeriaMethod(method.getName() , method.getParameterTypes()));
			invocation.setParameters(args);
// 调用信息传递给服务端			
			client.transformAndCall(invocation);
			return invocation.getResult();
		}
		
	}
}
