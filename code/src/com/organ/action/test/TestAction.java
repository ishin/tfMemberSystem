package com.organ.action.test;


import javax.servlet.ServletException;

import com.opensymphony.xwork2.Action;
import com.organ.common.BaseAction;

public class TestAction extends BaseAction {
	private static final long serialVersionUID = -3827421291421868917L;
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String test() throws ServletException {
		String result ="q111111111";
		returnToClient(result);
		return "text";
	}
	
}
