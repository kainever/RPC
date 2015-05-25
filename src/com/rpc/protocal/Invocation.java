package com.rpc.protocal;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.omg.Dynamic.Parameter;

/*
 * 把要传递给服务端的方法信息抽象成Invocation对象
 */
@SuppressWarnings("serial")
public class Invocation implements Serializable {
// 该方法属于哪个接口 中， 这样服务端才能调用	
	private Class interfaces;
	private SeriaMethod method;
	private Object[] parameters;
	private Object result;
	
	
	public Invocation() {
		super();
	}
	
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	
	public Class getInterfaces() {
		return interfaces;
	}
	public void setInterfaces(Class interfaces) {
		this.interfaces = interfaces;
	}
	public Object[] getParameters() {
		return parameters;
	}
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	
	public SeriaMethod getMethod() {
		return method;
	}

	public void setMethod(SeriaMethod method) {
		this.method = method;
	}
	
	@Override
	public String toString() {
		return interfaces.getName() + " " +  method.getMethodName()
				+ " " + Arrays.toString(parameters);
	}
}
