package com.rpc.server;

import java.io.IOException;

import com.rpc.protocal.Invocation;


public interface Server {

	void start();

	void Register(Class inter, Class impl);

	void Monitor() throws IOException;

	void invoke(Invocation invo);

	
}
