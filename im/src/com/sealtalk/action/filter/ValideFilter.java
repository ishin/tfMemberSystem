package com.sealtalk.action.filter;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.sealtalk.common.Constants;
import com.sealtalk.model.SessionUser;
import com.sealtalk.utils.TimeGenerator;

public class ValideFilter extends MethodFilterInterceptor {

	private static final long serialVersionUID = 8043686729851109226L;
	private static final Logger logger = LogManager.getLogger(ValideFilter.class);

	@Override
	protected String doIntercept(ActionInvocation invocation) throws Exception {
		Map session = invocation.getInvocationContext().getSession();
		SessionUser su = (SessionUser) session.get(Constants.ATTRIBUTE_NAME_OF_SESSIONUSER);
		//String action = invocation.getAction().getClass().getName();
		ActionProxy ap = invocation.getProxy();
		String name = ap.getActionName() + ap.getMethod();
		
		if (su != null) {
			//同客户端同接口访问频率控制
			Map<String, Long> am = su.getApMap();
			Long now = TimeGenerator.getInstance().getUnixTimeMills();
			
			
			if (am.size() > 0) {
				if (am.containsKey(name)) {
					Long lastedTime = am.get(name);
					
					if ((now.longValue() - lastedTime.longValue()) < 500) {
						logger.info("frequency access!");
						return null;
					}
					am.put(name, now);
				} else {
					am.put(name, now);
				}
			} else {
				am.put(name, now);
			}
			
			logger.info("Im sessionuser is not null: " + name);
			return invocation.invoke();
		} else {
			logger.info("Im sessionuser is null " + name);
			return "loginPage";
		}
	}

}
