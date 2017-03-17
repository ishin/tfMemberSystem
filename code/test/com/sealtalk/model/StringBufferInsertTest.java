package com.sealtalk.model;

public class StringBufferInsertTest {

	public static void main(String[] args) {
		String test = "[{{{123455}}},{{{{abcdefg}}}}]";
		StringBuffer sb = new StringBuffer(test);
	
		int position_1 = 0;
		int position_2 = 0;
		for(int i = 0;i < test.length();i++) {
			position_1 = sb.indexOf("{{");
			position_2 = sb.indexOf("},{");
			if(position_1 > 0) {
				sb.insert(position_1 + 1, "[");
			}
			if(position_2 > 0) {
				sb.insert(position_2 - 1, "]");
			}
		}
		System.out.println(sb.length());
		System.out.println(sb.toString());
	}

}
