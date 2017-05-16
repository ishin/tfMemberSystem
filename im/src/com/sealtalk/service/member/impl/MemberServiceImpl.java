package com.sealtalk.service.member.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.sealtalk.common.SysInterface;
import com.sealtalk.common.Tips;
import com.sealtalk.model.TMember;
import com.sealtalk.service.member.MemberService;
import com.sealtalk.utils.HttpRequest;
import com.sealtalk.utils.JSONUtils;
import com.sealtalk.utils.LogUtils;
import com.sealtalk.utils.PropertiesUtils;
import com.sealtalk.utils.StringUtils;
import com.sealtalk.utils.TimeGenerator;

public class MemberServiceImpl implements MemberService {
	private static final Logger logger = LogManager.getLogger(MemberServiceImpl.class);

	@Override
	public TMember searchSigleUser(String name, String password, int organId) {
		TMember memeber = null;

		try {
			JSONObject jo = new JSONObject();
			jo.put("account", name);
			jo.put("password", password);
			jo.put("organId", organId);

			String result = HttpRequest.getInstance().sendPost(
					SysInterface.CHECKACCOUNT.getName(), jo);
			JSONObject jm = JSONUtils.getInstance().stringToObj(result);
			
			if (jm != null && jm.getString("code").equals("1")) {
				JSONObject m = JSONUtils.getInstance().stringToObj(jm.getString("text"));
				memeber = JSONUtils.getInstance().jsonObjToBean(m, TMember.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		return memeber;
	}

	@Override
	public boolean updateUserPwdForAccount(String account, String newPwd, int organId) {
		boolean status = false;

		try {
			JSONObject p = new JSONObject();
			p.put("account", account);
			p.put("newPwd", newPwd);
			p.put("organId", organId);
			String result = HttpRequest.getInstance().sendPost(
					SysInterface.UPDATEPWDACCOUT.getName(), p);
			
			JSONObject jo = JSONUtils.getInstance().stringToObj(result);
			
			if (jo.getInt("code") == 1) {
				status = jo.getBoolean("text");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return status;
	}

	@Override
	public boolean updateUserPwdForPhone(String phone, String newPwd) {
		boolean status = false;

		try {
			JSONObject p = new JSONObject();
			p.put("phone", phone);
			p.put("newPwd", newPwd);
			String result = HttpRequest.getInstance().sendPost(
					SysInterface.UPDATEPWDPHONE.getName(), p);
			
			JSONObject jo = JSONUtils.getInstance().stringToObj(result);
			
			if (jo.getInt("code") == 1) {
				status = jo.getBoolean("text");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return status;
	}

	@Override
	public String getOneOfMember(String userId) {
		String result = null;
		JSONObject jo = new JSONObject();
		try {
			if (StringUtils.getInstance().isBlank(userId)) {
				jo.put("code", 0);
				jo.put("text", Tips.NULLUSER.getText());
				result = jo.toString();
			} else {
				jo.put("userId", userId);
				result = HttpRequest.getInstance().sendPost(
						SysInterface.GETONEOFMEMBER.getName(), jo);
			}
			logger.info(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		return result;
	}

	@Override
	public int updateUserTokenForId(String userId, String token) {
		int row = 0;

		try {
			JSONObject jo = new JSONObject();
			jo.put("userId", userId);
			jo.put("token", token);
			
			String result = HttpRequest.getInstance().sendPost(
					SysInterface.UPDATETOKEN.getName(), jo);
			JSONObject ret = JSONUtils.getInstance().stringToObj(result);
			row = ret.getInt("text");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return row;
	}

	@Override
	public String searchUser(String account, int organId) {
		JSONArray ja = new JSONArray();
		String result = null;

		try {
			JSONObject jo = new JSONObject();

			if (StringUtils.getInstance().isBlank(account)) {
				jo.put("code", 0);
				jo.put("text", Tips.NULLUSER.getText());
				ja.add(jo);
				result = ja.toString();
			} else {
				jo.put("account", account);
				jo.put("organId", organId);
				result = HttpRequest.getInstance().sendPost(
						SysInterface.SEARCHUSER.getName(), jo);
			}
			logger.info(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		return result;
	}

	@Override
	public boolean valideOldPwd(String account, String oldPwd, int organId) {
		boolean b = false;

		try {
			JSONObject p = new JSONObject();
			p.put("account", account);
			p.put("oldPwd", oldPwd);
			p.put("organId", organId);
			String result = HttpRequest.getInstance().sendPost(
					SysInterface.VALIDEOLDPWD.getName(), p);
			JSONObject jo =JSONUtils.getInstance().stringToObj(result);
			if (jo.getInt("code") == 1) {
				b = jo.getBoolean("text");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		return b;
	}

	@Override
	public String getTextCode(String phone) {
		String result = "-1";

		try {
			JSONObject p = new JSONObject();
			p.put("phone", phone);
			String ret = HttpRequest.getInstance().sendPost(
					SysInterface.GETTEXTCODE.getName(), p);
			
			JSONObject jo = JSONUtils.getInstance().stringToObj(ret);
			
			if(jo.getInt("code") == 1) {
				result = jo.getString("text");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		logger.info(result);
		return result;
	}

	@Override
	public void saveTextCode(String phone, String code) {
		try {
			JSONObject p = new JSONObject();
			p.put("phone", phone);
			p.put("code", code);
		
			HttpRequest.getInstance().sendPost(
					SysInterface.SAVETEXTCODE.getName(), p);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
	}

	@Override
	public String updateMemberInfoForWeb(String userId, String position,
			String fullName, String sign) {

		JSONObject jo = new JSONObject();
		String result = null;

		try {
			if (StringUtils.getInstance().isBlank(userId)) {
				jo.put("code", -1);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			} else {
				jo.put("userId", userId);
				jo.put("position", position);
				jo.put("fullName", fullName);
				jo.put("signature", sign);

				result = HttpRequest.getInstance().sendPost(
						SysInterface.UPDATEMEMWEB.getName(), jo);
			}
			logger.info(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		return result;
	}

	@Override
	public boolean updateUserPwd(String account, String newPwd) {
		boolean status = false;

		try {
			//String md5Pwd = PasswordGenerator.getInstance().getMD5Str(newPwd);

			//status = memberDao.updateUserPwd(account, md5Pwd);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return status;
	}

	@Override
	public String updateMemberForApp(String userId, String email,
			String mobile, String phone, String address) {

		JSONObject jo = new JSONObject();
		String result = null;

		try {
			if (StringUtils.getInstance().isBlank(userId)) {
				jo.put("code", -1);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			} else {
				jo.put("userId", userId);
				jo.put("email", email);
				jo.put("mobile", mobile);
				jo.put("phone", phone);
				jo.put("address", address);

				result = HttpRequest.getInstance().sendPost(
						SysInterface.UPDATEMEMAPP.getName(), jo);
			}
			logger.info(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		return result;
	}

	@Override
	public TMember getMemberByToken(String token) {

		try {
			if (!StringUtils.getInstance().isBlank(token)) {
				JSONObject jo = new JSONObject();
				jo.put("token", token);
				String result = HttpRequest.getInstance().sendPost(
						SysInterface.MEMBERBYTOKEN.getName(), jo);
				JSONObject ret = JSONUtils.getInstance().stringToObj(result);
				TMember member = null;
				
				if (ret.getInt("code") == 1) {
					JSONObject j = JSONUtils.getInstance().stringToObj(ret.getString("text"));
					member = JSONUtils.getInstance().jsonObjToBean(j, TMember.class);
				}
				
				if (member != null) {
					String tokenMaxAge = PropertiesUtils
							.getStringByKey("db.tokenMaxAge");

					long tokenMaxAgeLong = 0;
					long now = TimeGenerator.getInstance().getUnixTime();
					long firstTokenDate = member.getCreatetokendate();

					if (tokenMaxAge != null && !"".equals(tokenMaxAge)) {
						tokenMaxAgeLong = Long.valueOf(tokenMaxAge);
					}

					if ((now - firstTokenDate) <= tokenMaxAgeLong
							|| tokenMaxAgeLong == 0) {
						return member;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		return null;
	}

	@Override
	public String getAllMemberInfo(int organId) {
		String result = null;

		try {
			JSONObject jo = new JSONObject();
			jo.put("organId", organId);
			result = HttpRequest.getInstance().sendPost(
					SysInterface.ALLMEMBER.getName(), jo);
			logger.info(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return result;
	}

	@Override
	public String getAllMemberOnLineStatus(int organId, String userIds) {
		String result = null;

		try {
			JSONObject jo = new JSONObject();
			jo.put("userIds", userIds == null ? "" : userIds);
			jo.put("organId", organId);
			result = HttpRequest.getInstance().sendPost(
					SysInterface.MEMBERONLINE.getName(), jo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		return result;
	}

	@Override
	public int countMember(int organId) {
		int count = 0;

		try {
			JSONObject p = new JSONObject();
			p.put("organId", organId);
			String result = HttpRequest.getInstance().sendPost(
					SysInterface.COUNTMEMBER.getName(), p);
			JSONObject jo = JSONUtils.getInstance().stringToObj(result);

			if (jo != null && jo.getString("code").equals("1")) {
				count = jo.getInt("text");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return count;
	}
}
