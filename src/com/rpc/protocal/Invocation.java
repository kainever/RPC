package com.rpc.protocal;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.omg.Dynamic.Parameter;

/*
 * ��Ҫ���ݸ�����˵ķ�����Ϣ�����Invocation����
 */
@SuppressWarnings("serial")
public class Invocation implements Serializable {
// �÷��������ĸ��ӿ� �У� ��������˲��ܵ���	
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
