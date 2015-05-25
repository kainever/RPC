package com.rpc.protocal;

import java.io.Serializable;

/*
 * ע������ģ�����ʼ���Ƿ������
 * method����!
 *  ��Ϊ����ṩ��method��û�����л�
 *  �������ֱ���������д���Ļ����ͻᱨ
 *  unSerializable�쳣����
 */

public class SeriaMethod implements Serializable {
	private static final long serialVersionUID = 8844633346275718513L;
	
	
	private String methodName;
//	��������
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
