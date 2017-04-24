package com.sealtalk.service.friend.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.DELETE;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.poi.util.ArrayUtil;

import com.sealtalk.common.SysInterface;
import com.sealtalk.common.Tips;
import com.sealtalk.dao.friend.FriendDao;
import com.sealtalk.model.TFriend;
import com.sealtalk.model.TMember;
import com.sealtalk.service.friend.FriendService;
import com.sealtalk.utils.HttpRequest;
import com.sealtalk.utils.JSONUtils;
import com.sealtalk.utils.RongCloudUtils;
import com.sealtalk.utils.StringUtils;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

/**
 * 好友关系
 * 
 * @author hao_dy
 * @since jdk1.7
 */
public class FriendServiceImpl implements FriendService {

	private static final Logger logger = Logger
			.getLogger(FriendServiceImpl.class);

	@Override
	public String addFriend(String account, String friend, int organId) {

		JSONObject jo = new JSONObject();

		try {
			if (!StringUtils.getInstance().isBlank(account)
					&& !StringUtils.getInstance().isBlank(friend)) {
				friend = StringUtils.getInstance()
						.replaceChar(friend, "\"", "");
				friend = StringUtils.getInstance().replaceChar(friend, "[", "");
				friend = StringUtils.getInstance().replaceChar(friend, "]", "");

				String[] friendIds = null;

				if (friend.indexOf(",") != -1) {
					friendIds = friend.split(",");
				} else {
					friendIds = new String[1];
					friendIds[0] = friend;
				}

				// 检查好友及用户是否存在
				String[] mulMemberStr = new String[friendIds.length + 1];
				System.arraycopy(friendIds, 0, mulMemberStr, 0,
						friendIds.length);
				mulMemberStr[friendIds.length] = account;
				JSONObject pa = new JSONObject();
				pa.put("mulMemberStr", mulMemberStr);
				pa.put("organId", organId);
				
				String memberList = HttpRequest.getInstance().sendPost(
						SysInterface.MULTIPLEMEMBER.getName(), pa);

				JSONObject memJson = JSONUtils.getInstance().stringToObj(
						memberList);

				if (memJson.getString("code").equals(0)) {
					jo.put("code", -1);
					jo.put("text", Tips.FAILADDFRIEND.getText());
				} else {
					// 判断是否已存在好友关系
					String idStr = memJson.getString("text");
					idStr = StringUtils.getInstance().replaceChar(idStr, "[",
							"");
					idStr = StringUtils.getInstance().replaceChar(idStr, "]",
							"");
					String[] ids = idStr.split(",");
					int idsLen = ids.length;
					int id = Integer.parseInt(ids[idsLen - 1]);
					Integer[] fIds = new Integer[idsLen - 1];

					for (int i = 0; i < idsLen - 1; i++) {
						fIds[i] = Integer.parseInt(ids[i]);
					}

					List<TFriend> friendRelation = friendDao
							.getFriendRelationForFriendIds(id, fIds);
					String[] targetIds = new String[fIds.length];

					for (int i = 0; i < fIds.length; i++) {
						if (friendRelation == null
								|| (friendRelation != null && friendRelation
										.get(i) == null)) {
							friendDao.addFriend(id, fIds[0]);
						}
						targetIds[i] = fIds[i] + "";
					}
					jo.put("code", 1);
					jo.put("text", Tips.SUCADDFRIEND.getText());
					String msg = "建立好友关系，现在可以开始聊天";
					String extrMsg = "";
					RongCloudUtils.getInstance().sendPrivateMsg(id + "",
							targetIds, msg, extrMsg, "", "4", "0", "0", "0",
							"2");
				}
			} else {
				jo.put("code", 0);
				jo.put("text", Tips.NOTFRIENDID.getText());
			}
		} catch (Exception e) {
			jo.put("code", -1);
			jo.put("text", Tips.FAILADDFRIEND.getText());
			e.printStackTrace();
		}

		return jo.toString();
	}

	@Override
	public String delFriend(String account, String friend, int organId) {
		JSONObject jo = new JSONObject();

		// 检查好友及用户是否存在
		String[] mulMemberStr = { account, friend };
		JSONObject pa = new JSONObject();
		pa.put("mulMemberStr", mulMemberStr);
		pa.put("organId", organId);
		String memberList = HttpRequest.getInstance().sendPost(
				SysInterface.MULTIPLEMEMBER.getName(), pa);

		JSONObject memJson = JSONUtils.getInstance().stringToObj(memberList);

		if (memJson.getString("code").equals(0)) {
			jo.put("code", -1);
			jo.put("text", Tips.FAILADDFRIEND.getText());
		} else {
			// 判断是否已存在好友关系
			String idStr = memJson.getString("text");
			idStr = StringUtils.getInstance().replaceChar(idStr, "[", "");
			idStr = StringUtils.getInstance().replaceChar(idStr, "]", "");
			String[] ids = idStr.split(",");
			int accountId = Integer.parseInt(ids[0]);
			int friendId = Integer.parseInt(ids[1]);

			// 判断是否已存在好友关系
			TFriend friendRelation = friendDao.getFriendRelation(accountId,
					friendId);

			if (friendRelation != null) {
				// 删除好友关系
				friendDao.delFriend(accountId, friendId);
				String[] targetIds = { friend };
				String msg = "解除好友关系";
				RongCloudUtils.getInstance().sendPrivateMsg(accountId + "",
						targetIds, msg, "", "", "4", "0", "0", "0", "2");
				jo.put("code", 1);
				jo.put("text", Tips.SUCDELFRIEND.getText());
			} else {
				jo.put("code", 0);
				jo.put("text", Tips.NOHAVEFRIENDRELATION.getText());
			}
		}

		return jo.toString();
	}

	@Override
	public String getMemberFriends(String account, int organId) {

		boolean status = true;
		String result = null;
		JSONObject jo = new JSONObject();

		logger.info(account);

		try {
			JSONObject pa = new JSONObject();
			pa.put("account", account);
			pa.put("organId", organId);
			String memberIdList = HttpRequest.getInstance().sendPost(
					SysInterface.GETMIDFORAC.getName(), pa);
			
			JSONObject ret = JSONUtils.getInstance().stringToObj(memberIdList);
			
			if (ret.getInt("code") == 0) {
				status = false;
			} else {
				Integer id = ret.getInt("text");
				List<TFriend> friendList = friendDao.getFriendRelationForId(id);

				if (friendList == null) {
					status = false;
				} else {
					int len = friendList.size();
					Integer[] accounts = new Integer[len];

					for (int i = 0; i < len; i++) {
						accounts[i] = friendList.get(i).getFriendId();
					}

					// 获取多个用户
					JSONObject p = new JSONObject();
					p.put("ids", accounts);
					String memberStr = HttpRequest.getInstance().sendPost(
							SysInterface.MULTIPLEMEMBERFORID.getName(), p);
					
					JSONObject r = JSONUtils.getInstance().stringToObj(memberStr);
					List<TMember> memberList = new ArrayList<TMember>();
					
					if (r.getInt("code") == 1) {
						JSONArray ja = JSONUtils.getInstance().stringToArrObj(r.getString("text"));
						for(int i = 0; i < ja.size(); i++) {
							JSONObject t = ja.getJSONObject(i);
							memberList.add(JSONUtils.getInstance().jsonObjToBean(t, TMember.class));
						}
					}
					
					if (memberList.size() > 0) {
						int memberLen = memberList.size();
						// 获取用户职务id
						StringBuilder sb = new StringBuilder();
						StringBuilder so = new StringBuilder();
						ArrayList<Integer> organIds = new ArrayList<Integer>();

						for (int i = 0; i < memberLen; i++) {
							TMember t = memberList.get(i);
							sb.append(t.getId());
							if (i < memberLen - 1) {
								sb.append(",");
							}
							if (!organIds.contains(t.getOrganId())) {
								so.append(t.getOrganId()).append(",");
								organIds.add(t.getOrganId());
							}
						}
						
						String sbStr = sb.toString();
						String soStr = so.toString();
						
						if (soStr.endsWith(",")) {
							soStr = soStr.substring(0, soStr.length() - 1);
						}

						JSONObject bp = new JSONObject();
						bp.put("ids", sbStr);
						String memberPositionStr = HttpRequest.getInstance().sendPost(
								SysInterface.BRANCHMEMBERIDS.getName(), bp);
						
						JSONObject mjo = JSONUtils.getInstance().stringToObj(memberPositionStr);
						
						List memberPosition = null;
						
						if (mjo.getInt("code") == 1) {
							JSONArray jt = JSONUtils.getInstance().stringToArrObj(mjo.getString("text"));
							memberPosition = JSONUtils.getInstance().JSONArrayToList(jt);
						}
						
						JSONObject bo = new JSONObject();
						bp.put("ids", soStr);
						String memberOrganStr = HttpRequest.getInstance().sendPost(
								SysInterface.GETINFOS.getName(), bp);
						
						JSONObject mo = JSONUtils.getInstance().stringToObj(memberOrganStr);
						
						List memberOrgan = null;
						if (mo.getInt("code") == 1) {
							JSONArray jt = JSONUtils.getInstance().stringToArrObj(mo.getString("text"));
							memberOrgan = JSONUtils.getInstance().JSONArrayToList(jt);
						}
						if (memberList.size() == 0) {
							status = false;
						} else {
							JSONArray ja = new JSONArray();

							for (int i = 0; i < memberLen; i++) {
								TMember tms = memberList.get(i);
								JSONObject text = JSONUtils.getInstance()
										.modelToJSONObj(tms);
								boolean f = false;
								boolean g = false;

								for (int j = 0; j < memberPosition.size(); j++) {
									if (memberPosition.get(j) != null) {
										ArrayList o = (ArrayList) memberPosition.get(j);
										
										if ((tms.getId() + "").equals(String
												.valueOf(o.get(0)))) {
											text.put("position", o.get(1));
											text.put("branch", o.get(2));
											f = true;
											break;
										}
									}
								}
								
								if (memberOrgan != null) {
									for (int k = 0; k < memberOrgan.size(); k++) {
										if (memberOrgan.get(k) != null) {
											ArrayList organ = (ArrayList) memberOrgan.get(k);
											
											if ((tms.getOrganId() + "").equals(String.valueOf(organ.get(0)))) {
												text.put("organName", organ.get(1));
												g = true;
												break;
											}
										}
									}
								}

								if (!f) {
									text.put("position", "");
									text.put("branch", "");
								}

								if (!g) {
									text.put("organName", "");
								}

								ja.add(text);
							}

							result = ja.toString();
						}
					} else {
						status = false;
					}
				}
			}
			if (!status) {
				jo.put("code", 0);
				jo.put("text", Tips.HAVEZEROFRIEND.getText());
			} else {
				jo.put("code", 1);
				jo.put("text", result);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jo.toString();
	}

	@Override
	public String getFriendsRelation(String userId, String friendId) {
		JSONObject j = new JSONObject();

		if (!StringUtils.getInstance().isBlank(userId)
				&& !StringUtils.getInstance().isBlank(friendId)) {
			int userIdInt = StringUtils.getInstance().strToInt(userId);
			int friendIdInt = StringUtils.getInstance().strToInt(friendId);
			TFriend friendRelation = friendDao.getFriendRelation(userIdInt,
					friendIdInt);

			if (friendRelation != null) {
				j.put("code", 1);
				j.put("text", "true");
			} else {
				j.put("code", 0);
				j.put("text", "false");
			}
		} else {
			j.put("code", -1);
			j.put("text", Tips.WRONGPARAMS.getText());
		}

		return j.toString();
	}

	private FriendDao friendDao;

	public void setFriendDao(FriendDao fd) {
		this.friendDao = fd;
	}
}
