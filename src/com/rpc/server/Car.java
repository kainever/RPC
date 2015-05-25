package com.rpc.server;

import com.rpc.common.Movable;

public class Car implements Movable {
	private int style;
	
	@Override
	public void move() {
		System.out.println("the car can move");
	}

	@Override
	public int loadMax(int max) {
		return max;
	}

}
