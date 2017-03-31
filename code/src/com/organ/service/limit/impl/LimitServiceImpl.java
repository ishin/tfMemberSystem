package com.organ.service.limit.impl;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.google.gson.JsonObject;
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
	public String AddLimit(int parentId, String name, String app) {
		// TODO Auto-generated method stub
		return limitDao.updatePriv(parentId, name, app) + "";
	}

	@Override
	public String DelLimit(int privId) {
		// TODO Auto-generated method stub
		return limitDao.DeletePriv(privId) + "";
	}

	@Override
	public String EditLimit(int priv_id, String pid, String name, String app) {
		// TODO Auto-generated method stub
		return limitDao.editPriv(priv_id, pid, name, app) + "";
	}

	@SuppressWarnings("unchecked")
	@Override
	public String searchPriv(String Name, int pagesize, int pageindex) {
		JSONArray ja = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {

			List privlist = limitDao.searchPriv(Name, pagesize, pageindex);
			int count =limitDao.getSearchCount(Name);
			if (privlist == null) {
				JSONObject jo = new JSONObject();

				jo.put("code", 0);
				jo.put("text", "权限名称为空");
			} else {
				for (int i = 0; i < privlist.size(); i++) {
					Object[] priv = (Object[]) privlist.get(i);
					JSONObject jo = new JSONObject();
					jo.put("id", isBlank(priv[0]));
					jo.put("parent_id", isBlank(priv[1]));
					jo.put("name", isBlank(priv[2]));
					jo.put("category", isBlank(priv[3]));
					jo.put("url", isBlank(priv[4]));
					jo.put("app", isBlank(priv[5]));
					ja.add(jo);
					jsonObject.put("count", count+"");
					jsonObject.put("content", ja);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return jsonObject.toString();
	}

	private String isBlank(Object o) {
		return o == null ? "" : o + "";
	}

	@Override
	public int getCount() {
		try {
			int count =limitDao.getCount();
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

}
