package com.sealtalk.model;

import org.junit.Test;

public class StringBuildertest {
	@Test
	public void test() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("1");
		sb.append(",");
		sb.append("2");
		sb.append(",");
		sb.append("3");
		
		System.out.println(sb.toString());
	}

}
