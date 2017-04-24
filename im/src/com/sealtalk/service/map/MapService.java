package com.sealtalk.service.map;

public interface MapService {
	
	/**
	 * 获取联系人位置坐标
	 * @param userid
	 * @param targetid
	 * @param type
	 * @param isInit  显示原始坐标(1)还是初始90,180(0)
	 * @param organId 
	 * @return
	 */
	public String getLocation(String userid, String targetid, String type, String isInit, int organId);

	/**
	 * 提交个人位置 
	 * @param userid
	 * @param latitude
	 * @param longtitude
	 * @return
	 */
	public String subLocation(String userid, String latitude, String longtitude);

}
