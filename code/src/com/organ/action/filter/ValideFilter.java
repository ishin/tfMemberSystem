package com.organ.action.filter;

import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.organ.common.Constants;
import com.organ.model.SessionUser;

public class ValideFilter extends AbstractInterceptor {

	private static final long serialVersionUID = 8043686729851109226L;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		Map session = invocation.getInvocationContext().getSession();

		SessionUser su = (SessionUser) session.get(Constants.ATTRIBUTE_NAME_OF_SESSIONUSER);
		/*if (su != null) {
			return invocation.invoke();
		} else {
			return Action.LOGIN;
		}*/
		System.out.println(invocation.invoke());
		return invocation.invoke();
	
	}

}
