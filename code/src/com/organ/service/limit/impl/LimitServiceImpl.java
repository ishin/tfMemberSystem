package com.organ.service.limit.impl;

import java.util.List;

import net.sf.json.JSONArray;

import org.json.JSONObject;

import com.organ.dao.limit.LimitDao;
import com.organ.service.limit.LimitService;

/**
 * 实现接口
 * 
 * @author Lmy
 * 
 */
public class LimitServiceImpl implements LimitService {

	private LimitDao limitDao;

	public LimitDao getLimitDao() {
		return limitDao;
	}

	public void setLimitDao(LimitDao limitDao) {
		this.limitDao = limitDao;
	}

	@Override
	public String AddLimit(int parentId, String name, String category,
			String app) {
		// TODO Auto-generated method stub
		return limitDao.updatePriv(parentId, name, category, app) + "";
	}

	@Override
	public String DelLimit(int privId) {
		// TODO Auto-generated method stub
		return limitDao.DeletePriv(privId) + "";
	}

	@Override
	public String EditLimit(int priv_id, String pid, String name,
			String category, String app) {
		// TODO Auto-generated method stub
		return limitDao.editPriv(priv_id, pid, name, category, app) + "";
	}

	@Override
	public String searchPriv(String Name) {
		// TODO Auto-generated method stub
		JSONArray jsonObject = new JSONArray();
		try {
			List privlist = limitDao.searchPriv(Name);
			if(privlist == null){
				JSONObject jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", "权限名称为空");
			}else {
				for (int i = 0; i < privlist.size(); i++) {
					Object[] priv = (Object[]) privlist.get(i);
					JSONObject jo = new JSONObject();
					jo.put("id", isBlank(priv[0]));
					jo.put("parent_id", isBlank(priv[1]));
					jo.put("name", isBlank(priv[2]));
					jo.put("category", isBlank(priv[3]));
					jo.put("url", isBlank(priv[4]));
					jo.put("app", isBlank(priv[5]));
					jsonObject.add(jo);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
	
	private String isBlank(Object o) {
		return o == null ? "" : o + "";
	}
	

}
