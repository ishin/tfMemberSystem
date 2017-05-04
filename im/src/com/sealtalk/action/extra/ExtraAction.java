package com.sealtalk.action.extra;

import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.json.JSONObject;

import com.sealtalk.common.BaseAction;
import com.sealtalk.common.Tips;
import com.sealtalk.service.extra.ExtraService;
import com.sealtalk.utils.JSONUtils;
import com.sealtalk.utils.LogUtils;

public class ExtraAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ExtraAction.class);
	
	public String delByMemberIds() throws ServletException {
		String result = null;
		
		try {
			String params = getRequestDataByStream();
			JSONObject jo = new JSONObject();
			boolean s = true;
			
			if (params == null) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(p)) {
					s = false;
				} else {
					String ids = p.getString("userIds");
					logger.info("params: " + ids);
					result = extraService.delByMemberIds(ids);
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		logger.info(result);
		returnToClient(result);
		return "text";
	}

	private ExtraService extraService;

	public void setExtraService(ExtraService extraService) {
		this.extraService = extraService;
	}
}
