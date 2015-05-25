package com.rpc.client;

import java.io.IOException;

import com.rpc.protocal.Invocation;

/*
 * 定义客户端接口
 */
public interface Client {
	public void initClient (String ip , int port) throws IOException;
	public void transformAndCall(Invocation invocation);
}
