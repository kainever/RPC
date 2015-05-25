package com.rpc.util;

import org.junit.Test;

import com.rpc.protocal.Invocation;

public class SerializableUtilTest {

	@Test
	public void test() {
		Invocation invo = new Invocation();
		byte[] bytes = SerializableUtil.toByte(invo);
	System.out.println(bytes);
		Object obj = SerializableUtil.toObject(bytes);
	System.out.println(obj.getClass());
	}

}
