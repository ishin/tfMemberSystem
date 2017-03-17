package com.organ.action.test;

import javax.servlet.ServletException;

import com.googlecode.sslplugin.annotation.Secured;
import com.organ.common.BaseAction;

public class TestAction extends BaseAction {

	private static final long serialVersionUID = -3827421291421868917L;

	public String test() throws ServletException {
		System.out.println("------------------ " + getSessionUser().getAccount());
		return "test";
	}
}
