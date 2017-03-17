package com.sealtalk.model;

import com.organ.utils.PropertiesUtils;

public enum EumTest {
	API("http://api.cn.ronghub.com");
	
	private String str;
	
	private EumTest(String str){
		this.str = str;
	}
}
