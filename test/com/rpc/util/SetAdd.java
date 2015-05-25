package com.rpc.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SetAdd {

	public static void main(String[] args) {
		Set<String> set = new HashSet<String> ();
		set.add("f");
		set.add("g");
		Iterator it = set.iterator();
		while(it.hasNext()) {
			String s = (String) it.next();
			System.out.println(s);
			set.add("a");
		}
	}

}
