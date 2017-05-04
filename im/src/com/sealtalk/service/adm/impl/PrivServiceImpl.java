package com.sealtalk.service.adm.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sealtalk.common.SysInterface;
import com.sealtalk.service.adm.PrivService;
import com.sealtalk.utils.HttpRequest;
import com.sealtalk.utils.JSONUtils;
import com.sealtalk.utils.LogUtils;

public class PrivServiceImpl implements PrivService {

	private static final Logger logger = LogManager.getLogger(PrivServiceImpl.class);
	
	/**
	 * 根据用户id获取权限
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List getRoleIdForId(int id) {
		List list = new ArrayList();
		
		try {
			JSONObject jo = new JSONObject();
			jo.put("userId", id);
			String result = HttpRequest.getInstance().sendPost(
						SysInterface.GETPRIVILEGE.getName(), jo);
			JSONObject ret = JSONUtils.getInstance().stringToObj(result);
			
			if (ret != null && ret.getInt("code") == 1) {
				JSONArray ja = JSONUtils.getInstance().stringToArrObj(ret.getString("text"));
				list = JSONUtils.getInstance().JSONArrayToList(ja);
			}
		} catch (Exception e) { 
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		logger.info(list);
		return list;
		
	}

	@Override
	public String getPrivStringByMember(Integer id) {
		JSONObject p = new JSONObject();
		p.put("id", id);
		String result = HttpRequest.getInstance().sendPost(
				SysInterface.PRIVBYMEMBER.getName(), p);
		logger.info(result);
		return result;
	}

}
