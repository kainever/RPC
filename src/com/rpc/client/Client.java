package com.rpc.client;

import java.io.IOException;

import com.rpc.protocal.Invocation;

/*
 * ����ͻ��˽ӿ�
 */
public interface Client {
	public void initClient (String ip , int port) throws IOException;
	public void transformAndCall(Invocation invocation);
}
