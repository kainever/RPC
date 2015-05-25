package com.rpc.protocal;

import java.io.Serializable;

/*
 * 注意这里模拟的起始就是反射包中
 * method方法!
 *  因为如果提供的method并没有序列化
 *  这样如果直接在网络中传输的话，就会报
 *  unSerializable异常！！
 */

public class SeriaMethod implements Serializable {
	private static final long serialVersionUID = 8844633346275718513L;
	
	
	private String methodName;
//	参数类型
	private Class[] paramsType;
	
	public SeriaMethod(String name, Class<?>[] parameterTypes) {
		this.methodName = name;
		this.paramsType = parameterTypes;
	}
	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}
	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	/**
	 * @return the params
	 */
	public Class[] getParams() {
		return paramsType;
	}
	/**
	 * @param params the params to set
	 */
	public void setParams(Class[] params) {
		this.paramsType = params;
	}
	
	
}
