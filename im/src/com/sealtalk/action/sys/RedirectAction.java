package com.sealtalk.action.sys;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import com.sealtalk.common.BaseAction;

public class RedirectAction extends BaseAction {
	
	private static final long serialVersionUID = 1L;
	
	public String login() throws ServletException {
		String ret = "<!DOCTYPE html";
		returnToClient(ret);
		return "text";
	}
}
