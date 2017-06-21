package com.sealtalk.service.msg.impl;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sealtalk.common.Tips;
import com.sealtalk.model.TMember;
import com.sealtalk.service.msg.UserServiceService;
import com.sealtalk.utils.LogUtils;
import com.sealtalk.utils.PropertiesUtils;
import com.sealtalk.utils.RongCloudUtils;
import com.sealtalk.utils.StringUtils;
import com.sealtalk.utils.TimeGenerator;

public class UserServiceServiceImpl implements UserServiceService {
	private static final Logger logger = LogManager.getLogger(UserServiceServiceImpl.class);
	
	@Override
	public String getToken(String id) {
		JSONObject jo = new JSONObject();
		
		try {
			//TMember tm = memberDao.getMemberForId(Integer.valueOf(id));
			TMember tm = null;
			
			long tokenMaxAgeLong = 0;
			long firstTokenDate = tm.getCreatetokendate();
			long now = TimeGenerator.getInstance().getUnixTime();
			
			String tokenMaxAge = PropertiesUtils.getStringByKey("db.tokenMaxAge");
			
			if (tokenMaxAge != null && !"".equals(tokenMaxAge)) {
				tokenMaxAgeLong = Long.valueOf(tokenMaxAge);
			}
			
			String token = tm.getToken();
			
			if ((now - firstTokenDate) > tokenMaxAgeLong) {
				String name = null;
				String logo = null;
				if (tm != null) {
					name = tm.getFullname();
					logo = tm.getLogo();
				}
				String domain = PropertiesUtils.getDomain();
				String uploadDir = PropertiesUtils.getUploadDir();
				String url = domain + uploadDir + logo;
				token = RongCloudUtils.getInstance().getToken(id, name, url);
			}
			
			if (StringUtils.getInstance().isBlank(token)) {
				jo.put("code", 0);
				jo.put("text", "fail");
			} else {
				jo.put("code", 1);
				jo.put("text", token);
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		logger.info(jo.toString());
		return jo.toString();
	}
	
	@Override
	public String refreshUser(String userId) {
		JSONObject jo = new JSONObject();
		
		try {
			//TMember tm = memberDao.getMemberForId(Integer.valueOf(userId));
			TMember tm = null;
			
			String name = null;
			String logo = null;
			
			if (tm != null) {
				name = tm.getFullname();
				logo = tm.getLogo();
			}
			
			String domain = PropertiesUtils.getDomain();
			String uploadDir = PropertiesUtils.getUploadDir();
			String url = domain + uploadDir + logo;
			int code = RongCloudUtils.getInstance().refreshUser(userId, name, url);
			
			jo.put("code", code);
			jo.put("text", "ok");
			
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		logger.info(jo.toString());
		return jo.toString();
	}
	
	@Override
	public String checkOnline(String userId) {
		JSONObject jo = new JSONObject();
		if (StringUtils.getInstance().isBlank(userId)) {
			jo.put("code", -1);
			jo.put("text", Tips.NULLID.getName());
		} else {
			String status = RongCloudUtils.getInstance().checkOnLine(userId);
			jo.put("code", 1);
			jo.put("text", status);
		}
		logger.info(jo.toString());
		return jo.toString();
	}

}
