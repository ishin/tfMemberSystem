package com.sealtalk.service.map.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sealtalk.common.SysInterface;
import com.sealtalk.common.Tips;
import com.sealtalk.dao.friend.FriendDao;
import com.sealtalk.dao.group.GroupMemberDao;
import com.sealtalk.dao.map.MapDao;
import com.sealtalk.model.TFriend;
import com.sealtalk.model.TGroupMember;
import com.sealtalk.model.TMap;
import com.sealtalk.model.TMember;
import com.sealtalk.service.map.MapService;
import com.sealtalk.utils.HttpRequest;
import com.sealtalk.utils.JSONUtils;
import com.sealtalk.utils.LogUtils;
import com.sealtalk.utils.PropertiesUtils;
import com.sealtalk.utils.StringUtils;
import com.sealtalk.utils.TimeGenerator;

public class MapServiceImpl implements MapService {

	private Logger logger = LogManager.getLogger(MapServiceImpl.class);
	
	@Override
	public String getLocation(String userId, String targetId, String type, String isInit, int organId) {
		JSONObject jo = new JSONObject();

		if (StringUtils.getInstance().isBlank(userId)
				|| StringUtils.getInstance().isBlank(targetId)
				|| StringUtils.getInstance().isBlank(type)) {
			jo.put("code", -1);
			jo.put("text", Tips.WRONGPARAMS.getText());
		} else {
			boolean status = true;
			int targetIdInt = StringUtils.getInstance().strToInt(targetId);
			int userIdInt = StringUtils.getInstance().strToInt(userId);

			try {
				ArrayList<Integer> idStr = new ArrayList<Integer>();

				if (type.equals("1")) { // 群
					List<TGroupMember> memberList = groupMemeberDao
							.listGroupMembers(targetIdInt);

					if (memberList != null) {

						for (int i = 0; i < memberList.size(); i++) {
							idStr.add(memberList.get(i).getMemberId());
						}
					}
				} else if (type.equals("2")) { //单好友
					idStr.add(targetIdInt);
					idStr.add(userIdInt);
				} else if (type.equals("3")) { //所有人
					int mapMax = StringUtils.getInstance().strToInt(
							PropertiesUtils.getStringByKey("map.max"));
					
					JSONObject jp = new JSONObject();
					jp.put("mapMax", mapMax);
					jp.put("organId", organId);
					
					String tpsStr = HttpRequest.getInstance().sendPost(
							SysInterface.LIMITMEMBERIDS.getName(), jp);
					
					JSONObject ret = JSONUtils.getInstance().stringToObj(tpsStr);
					List<TMember> memberIds = new ArrayList<TMember>();
					
					if (ret.getInt("code") == 1) {
						JSONArray jaRet = JSONUtils.getInstance().stringToArrObj(ret.getString("text"));
						
						for(int i = 0; i < jaRet.size(); i++) {
							memberIds.add(JSONUtils.getInstance().jsonObjToBean(jaRet.getJSONObject(i), TMember.class));
						}
					}

					if (memberIds != null) {
						int len = memberIds.size();
						len = len >= mapMax ? mapMax : len;
						for (int i = 0; i < len; i++) {
							TMember tm = memberIds.get(i);
							idStr.add(tm.getId());
						}
					}
				} else {		//所有好友
					int mapMax = StringUtils.getInstance().strToInt(PropertiesUtils.getStringByKey("map.max"));
					List<TFriend> friends = friendDao.getFriendRelationForIdWithLimit(userIdInt, mapMax);
					if (friends != null) {
						int len = friends.size();

						len = len >= mapMax ? mapMax : len;

						for (int i = 0; i < len; i++) {
							TFriend friend = (TFriend) friends.get(i);
							idStr.add(friend.getFriendId());
						}
					}
				}

				logger.info("map->type: " + type + "->ids: " + idStr);

				if (idStr.size() == 0) {
					status = false;
				} else {
					JSONObject param = new JSONObject();
					param.put("id", idStr.toString());
					param.put("params",new String[]{"id", "logo"});
					String tpsStr = HttpRequest.getInstance().sendPost(
							SysInterface.GETMEMBERPARAM.getName(), param);
					
					JSONObject ret = JSONUtils.getInstance().stringToObj(tpsStr);
					
					if (ret.getInt("code") == 1) {
						JSONArray arr = JSONUtils.getInstance().stringToArrObj(ret.getString("text"));
						
						Integer[] ids = new Integer[idStr.size()];
						idStr.toArray(ids);
						List<TMap> map = mapDao.getMapByIds(ids);
		
						if (map != null) {
							long now = TimeGenerator.getInstance().getUnixTime();
							long shareTime = 0;
		
							String mapShareTime = PropertiesUtils
									.getStringByKey("map.sharetime");
		
							if (mapShareTime != null) {
								shareTime = Long.parseLong(mapShareTime);
							}
							ArrayList<JSONObject> aj = new ArrayList<JSONObject>();
							
							for (int i = 0; i < arr.size(); i++) {
								JSONObject tmp = arr.getJSONObject(i);
								if (StringUtils.getInstance().isBlank(tmp.getString("logo"))) {
									tmp.remove("logo");
									tmp.put("logo", PropertiesUtils.getStringByKey("cfg.defaultlogo"));
								}
								for(int j = 0; j < map.size(); j++) {
									TMap tm = map.get(j);
									if (tm.getUserId() == tmp.getInt("userID")) {
										long time = tm.getSubDate();
										long timeVal = now - time;
										if (isInit == null || isInit.equals("0")) {
											if (timeVal >= shareTime) {		
												tmp.put("latitude", 90);
												tmp.put("longtitude", 180);
											} else {
												tmp.put("latitude", tm.getLatitude());
												tmp.put("longtitude", tm.getLongitude());
											}
										} else {
											tmp.put("latitude", tm.getLatitude());
											tmp.put("longtitude", tm.getLongitude());
										}
										aj.add(tmp);
										break;
									}
								}
							}
							jo.put("code", 1);
							jo.put("text", aj.toString());
						} else {
							status = false;
						}
					} else {
						status = false;
					}
				}
				if (!status) {
					jo.put("code", 0);
					jo.put("text", Tips.FAIL.getText());
				}
			} catch (Exception e) {
				logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
				e.printStackTrace();
			}
		}

		logger.info(jo.toString());
		return jo.toString();
	}

	@Override
	public String subLocation(String userId, String latitude, String longtitude) { 
		JSONObject jo = new JSONObject();

		try {
			if (StringUtils.getInstance().isBlank(userId)
					|| StringUtils.getInstance().isBlank(latitude)
					|| StringUtils.getInstance().isBlank(longtitude)) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
			} else {
				int userIdInt = StringUtils.getInstance().strToInt(userId);
				long now = TimeGenerator.getInstance().getUnixTime();

				TMap t = mapDao.getLaLongtitudeForUserId(userIdInt);

				if (t != null) {
					//String la = t.getLatitude();
					//String longt = t.getLongitude();

					//if (!la.equals(latitude) || !longt.equals(longtitude)) {
					//必须更新，因为要更新时间，做超时检测
						mapDao.updateLocation(userIdInt, latitude, longtitude, now);
					//}
				} else {
					TMap tm = new TMap();
					tm.setUserId(userIdInt);
					tm.setLatitude(latitude);
					tm.setLongitude(longtitude);
					tm.setIsDel("1");
					tm.setSubDate(now);

					mapDao.saveLocation(tm);
				}

				jo.put("code", 1);
				jo.put("text", Tips.OK.getText());
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}

		logger.info(jo.toString());
		return jo.toString();
	}

	private FriendDao friendDao;
	private MapDao mapDao;
	private GroupMemberDao groupMemeberDao;


	public FriendDao getFriendDao() {
		return friendDao;
	}

	public void setFriendDao(FriendDao friendDao) {
		this.friendDao = friendDao;
	}

	public void setGroupMemeberDao(GroupMemberDao groupMemeberDao) {
		this.groupMemeberDao = groupMemeberDao;
	}

	public void setMapDao(MapDao mapDao) {
		this.mapDao = mapDao;
	}

}
