package com.sealtalk.action.filter;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.sealtalk.common.Constants;
import com.sealtalk.model.SessionUser;

public class ValideFilter extends MethodFilterInterceptor {

	private static final long serialVersionUID = 8043686729851109226L;
	private static final Logger logger = LogManager.getLogger(ValideFilter.class);
	/*
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		Map session = invocation.getInvocationContext().getSession();

		SessionUser su = (SessionUser) session.get(Constants.ATTRIBUTE_NAME_OF_SESSIONUSER);
		if (su != null) {
			System.out.println("sessionuser is not null");
			return invocation.invoke();
		} else {
			System.out.println("sessionuser is null");
			return "loginPage";
		}
	}*/

	@Override
	protected String doIntercept(ActionInvocation invocation) throws Exception {
		Map session = invocation.getInvocationContext().getSession();
		SessionUser su = (SessionUser) session.get(Constants.ATTRIBUTE_NAME_OF_SESSIONUSER);
		String action = invocation.getAction().getClass().getName();
		if (su != null) {
			logger.info("Im sessionuser is not null: " + action);
			return invocation.invoke();
		} else {
			logger.info("Im sessionuser is null " + action);
			return "loginPage";
		}
	}

}
