package com.sealtalk.action.map;

import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.googlecode.sslplugin.annotation.Secured;
import com.sealtalk.common.BaseAction;
import com.sealtalk.service.map.MapService;
import com.sealtalk.utils.LogUtils;

public class MapAction extends BaseAction {

	private static final long serialVersionUID = -1765255265459599929L;
	private final static Logger logger = LogManager.getLogger("MapAction.class");
	
	public String subLocation() throws ServletException {
		String result = null;
		
		try {
			result = mapService.subLocation(clearChar(userid), clearChar(latitude), clearChar(longitude));
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 获取好友或群组成员位置 
	 * type:1群id,2好友id，3所有人，4所有好友
	 * @return
	 * @throws Exception
	 */
	public String getLocation() throws ServletException {
		String result = null;
		
		try {
			int organId = getSessionUserOrganId();
			result = mapService.getLocation(clearChar(userid), clearChar(targetid), clearChar(type), clearChar(isInit), organId);
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	private MapService mapService;
	
	private String userid;
	private String targetid;
	private String type;
	private String latitude;
	private String longitude;
	private String isInit;
	

	public String getIsInit() {
		return isInit;
	}

	public void setIsInit(String isInit) {
		this.isInit = isInit;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public MapService getMapService() {
		return mapService;
	}
	public void setMapService(MapService mapService) {
		this.mapService = mapService;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getTargetid() {
		return targetid;
	}
	public void setTargetid(String targetid) {
		this.targetid = targetid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
