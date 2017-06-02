package com.organ.service.auth.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.organ.common.AuthTips;
import com.organ.dao.adm.MemberRoleDao;
import com.organ.dao.appinfoconfig.AppInfoConfigDao;
import com.organ.dao.auth.AppSecretDao;
import com.organ.dao.auth.UserValidDao;
import com.organ.dao.member.MemberDao;
import com.organ.model.AppSecret;
import com.organ.model.SessionUser;
import com.organ.model.TMember;
import com.organ.model.UserValid;
import com.organ.service.auth.AppSecretService;
import com.organ.utils.JSONUtils;
import com.organ.utils.LogUtils;
import com.organ.utils.PasswordGenerator;
import com.organ.utils.PropertiesUtils;
import com.organ.utils.SecretUtils;
import com.organ.utils.StringUtils;
import com.organ.utils.TimeGenerator;

public class AppSecretServiceImpl implements AppSecretService {
	private static final Logger logger = LogManager.getLogger(AppSecretServiceImpl.class);

	@Override
	public JSONObject getTempTokenSceneOne(String appId) {
		JSONObject jo = new JSONObject();
		String code = "500";
		String text = null;

		logger.info("getTempTokenSceneOne appId: " + appId);

		try {
			if (StringUtils.getInstance().isBlank(appId)) {
				text = AuthTips.WORNGPARAM.getText();
			}else {
				long now = TimeGenerator.getInstance().getUnixTime();
				appId = appId.trim();
				AppSecret as = appSecretDao.getAppSecretByAppId(appId);

				if (as == null) {
					text = AuthTips.INVALIDAPPID.getText();
				} else {
					String dbAppId = as.getAppId();

					if (!dbAppId.equals(appId)) {
						text = AuthTips.INVALIDAPPID.getText();
					} else {
						// 生成临时令牌
						long tokenTimeL = 0;
						String tokenTime = PropertiesUtils
								.getStringByKey("auth.unauthtime");

						if (tokenTime != null) {
							tokenTimeL = Long.parseLong(tokenTime);
						}
						long tokenValidTime = now + tokenTimeL;

						appId += tokenValidTime;

						String unAuthToken = makeCode(appId);

						int id = as.getId();
						UserValid uv = userValidDao.getUserValidByAsId(id);
						
						if (uv == null) {
							uv = new UserValid();
							uv.setAsid(as.getId());
						} 
						uv.setUnAuthToken(unAuthToken);
						uv.setUnAuthTokenTime(tokenValidTime);
						uv.setIsDel("1");
						userValidDao.setUnAuthToken(uv);

						code = "200";
						text = unAuthToken;
					}
				}
			}
			jo.put("code", code);
			jo.put("text", text);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return jo;
	}

	@Override
	public JSONObject reqAuthorizeOne(String unAuthToken, String userName,
			String userPwd, String appId, String info) {
		JSONObject jo = new JSONObject();
		String code = "500";
		String text = null;

		try {
			if (StringUtils.getInstance().isBlank(appId)) {
				text = AuthTips.INVALIDAPPID.getText();
			} else if (StringUtils.getInstance().isBlank(unAuthToken)) {
				text = AuthTips.INVALTOKEN.getText();
			} else if (StringUtils.getInstance().isBlank(userName)
					|| StringUtils.getInstance().isBlank(userPwd)) {
				text = AuthTips.INVALUSER.getText();
			} else {
				String appIdCode = coverCode(unAuthToken);
				String appIdc = appIdCode.substring(0, appIdCode.length() - 10);

				if (!appId.equals(appIdc)) {
					text = AuthTips.INVALTOKEN.getText();
				} else {
					AppSecret as = appSecretDao.getAppSecretByAppId(appId);
					long now = TimeGenerator.getInstance().getUnixTime();

					if (as != null) {
						UserValid uv = userValidDao
								.getUserValidByUnAuthToken(unAuthToken);

						if (uv != null) {
							String callBackUrl = as.getCallBackUrl();

							long unAuthTokenTime = uv.getUnAuthTokenTime();

							if (now >= unAuthTokenTime) {
								text = AuthTips.TIMEOUTTOKEN.getText();
							} else {
								int organId = as.getOrganId();
								TMember tm = memberDao.searchSigleUser(
										userName, userPwd, organId);

								if (tm != null) {
									long authTokenTimeL = 0;
									int infoInt = 3;
									String authTokenTime = PropertiesUtils
											.getStringByKey("auth.authtime");
									appId += now;
									String authToken = makeCode(appId);
									authTokenTimeL = authTokenTime != null ? Long
											.parseLong(authTokenTime)
											: 0;
									uv.setAuthToken(authToken);
									uv.setAuthTokenTime(now + authTokenTimeL);
									uv.setUserId(tm.getId());
									uv.setInfo(infoInt);
									userValidDao.setUnAuthToken(uv);
									code = "200";
									text = authToken;
									jo.put("url", callBackUrl);
									jo.put("name", as.getAppName());
									jo.put("accountStatus", this
											.loginAbleStatus(Integer.valueOf(as
													.getId()), Integer
													.valueOf(tm.getId())));
								} else {
									logger.warn("member is null");
									text = AuthTips.INVALUSER.getText();
								}
							}
						} else {
							logger.warn("uservalid is null");
							text = AuthTips.INVALTOKEN.getText();
						}
					} else {
						logger.warn("appsecret is null");
						text = AuthTips.INVALIDAPPID.getText();
					}
				}
			}
			jo.put("code", code);
			jo.put("text", text);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		return jo;
	}

	@Override
	public String getRealToken(String secret, String authToken, String organId) {
		JSONObject jo = new JSONObject();
		String code = "500";
		String text = null;

		try {
			if (StringUtils.getInstance().isBlank(secret)) {
				text = AuthTips.WORNGSECRET.getText();
			} else if (StringUtils.getInstance().isBlank(authToken)) {
				text = AuthTips.INVALTOKEN.getText();
			} else if (StringUtils.getInstance().isBlank(authToken)) {
				text = AuthTips.INVALCOMPANYID.getText();
			} else {
				AppSecret as = appSecretDao.getAppSecretBySecret(secret, Integer.parseInt(organId));
				long now = TimeGenerator.getInstance().getUnixTime();

				if (as != null) {
					String secretDB = as.getSecert();
					if (!secretDB.equals(secret)) {
						text = AuthTips.WORNGSECRET.getText();
					} else {
						UserValid uv = userValidDao.getUserValidByAuthToken(authToken);

						if (uv != null) {
							long authTokenTimeDB = uv.getAuthTokenTime();

							if (now >= authTokenTimeDB) {
								text = AuthTips.TIMEOUTTOKEN.getText();
							} else {
								long realTokenTimeL = 0;
								String realTokenTime = PropertiesUtils
										.getStringByKey("auth.visittime");
								realTokenTimeL = realTokenTime != null ? Long
										.parseLong(realTokenTime) : 0;
								String nowEncry = makeCode(now + "");
								String secretPart = StringUtils.getInstance()
										.getRandomString(nowEncry + secret, 10);
								String realToken = makeCode(secretPart);
								uv.setVisitToken(realToken);
								uv.setVisitTokenTime(now + realTokenTimeL);
								userValidDao.setUnAuthToken(uv);
								code = "200";
								text = realToken;
							}
						} else {
							logger.warn("uservalid is null");
							text = AuthTips.INVALTOKEN.getText();
						}
					}
				} else {
					logger.warn("appsecret is null");
					text = AuthTips.WORNGSECRET.getText();
				}
			}
			jo.put("code", code);
			jo.put("text", text);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return jo.toString();
	}

	@Override
	public String getAuthResource(String visitToken) {
		JSONObject ret = new JSONObject();
		String text = null;
		String code = "500";

		try {
			if (StringUtils.getInstance().isBlank(visitToken)) {
				text = AuthTips.INVALTOKEN.getText();
			} else {
				UserValid as = userValidDao.getUserValidByRealToken(visitToken);

				if (as != null) {
					long now = TimeGenerator.getInstance().getUnixTime();
					long visitTokenTime = as.getVisitTokenTime();

					if (now >= visitTokenTime) {
						text = AuthTips.TIMEOUTTOKEN.getText();
					} else {
						int userId = as.getUserId();
						int info = as.getInfo();

						Object[] member = memberDao.getAuthResouce(userId);
						JSONObject jo = new JSONObject();

						jo.put("userId", isBlank(member[5]));
						jo.put("account", isBlank(member[6]));

						if (info == 1 || info == 3) {
							jo.put("name", isBlank(member[0]));
							jo.put("logo", isBlank(member[1]));
							jo.put("sexname", isBlank(member[7]));
							jo.put("positionname", isBlank(member[8]));
						}
						if (info == 2 || info == 3) {
							jo.put("telephone", isBlank(member[2]));
							jo.put("email", isBlank(member[3]));
							jo.put("mobile", isBlank(member[4]));
							jo.put("organname", isBlank(member[9]));
						}

						code = "200";
						text = jo.toString();
					}
				} else {
					logger.warn("uservalid is null");
				}
			}
			ret.put("code", code);
			ret.put("text", text);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return ret.toString();
	}

	@Override
	public JSONObject reqAuthorizeTwo(Integer id, String appId,
			String unAuthToken) {
		JSONObject ret = new JSONObject();
		String code = "500";
		String text = null;

		try {
			if (id == null) {
				text = AuthTips.NOTLOGIN.getText();
			} else if (StringUtils.getInstance().isBlank(appId)) {
				text = AuthTips.INVALIDAPPID.getText();
			} else if (StringUtils.getInstance().isBlank(unAuthToken)) {
				text = AuthTips.INVALTOKEN.getText();
			} else {
				String appIdCode = coverCode(unAuthToken);
				String appIdc = appIdCode.substring(0, appIdCode.length() - 10);

				if (!appIdc.equals(appId)) {
					text = AuthTips.INVALTOKEN.getText();
				} else {
					AppSecret as = appSecretDao.getAppSecretByAppId(appId);

					if (as != null) {
						UserValid uv = userValidDao
								.getUserValidByUnAuthToken(unAuthToken);
						long now = TimeGenerator.getInstance().getUnixTime();

						if (uv != null) {
							long unAuthTokenTime = uv.getUnAuthTokenTime();

							if (now >= unAuthTokenTime) {
								text = AuthTips.TIMEOUTTOKEN.getText();
							} else {
								long authTokenTimeL = 0;
								String authTokenTime = PropertiesUtils
										.getStringByKey("auth.authtime");
								appId += now;
								String authToken = makeCode(appId);
								authTokenTimeL = authTokenTime != null ? Long
										.parseLong(authTokenTime) : 0;
								uv.setAuthToken(authToken);
								uv.setAuthTokenTime(now + authTokenTimeL);
								uv.setUserId(id);
								uv.setInfo(3);
								userValidDao.setUnAuthToken(uv);
								code = "200";
								text = authToken;
								ret.put("url", as.getCallBackUrl());
								ret.put("name", as.getAppName());
								ret.put("accountStatus", this.loginAbleStatus(
										Integer.valueOf(as.getId()), Integer
												.valueOf(id)));
							}
						} else {
							logger.warn("uservalid is null");
							text = AuthTips.INVALIDAPPID.getText();
						}
					} else {
						logger.warn("appsecret is null");
						text = AuthTips.INVALIDAPPID.getText();
					}
				}
			}
			ret.put("code", code);
			ret.put("text", text);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return ret;
	}

	@Override
	public JSONObject reqAuthorizeTwoForApp(String userId, String appId,
			String unAuthToken) {
		JSONObject ret = new JSONObject();
		String code = "500";
		String text = null;

		try {
			if (StringUtils.getInstance().isBlank(userId)) {
				text = AuthTips.WORNGPARAM.getText();
			} else if (StringUtils.getInstance().isBlank(appId)) {
				text = AuthTips.INVALIDAPPID.getText();
			} else if (StringUtils.getInstance().isBlank(unAuthToken)) {
				text = AuthTips.INVALTOKEN.getText();
			} else {
				String appIdCode = coverCode(unAuthToken);
				String appIdc = appIdCode.substring(0, appIdCode.length() - 10);

				if (!appIdc.equals(appId)) {
					text = AuthTips.INVALTOKEN.getText();
				} else {
					AppSecret as = appSecretDao.getAppSecretByAppId(appId);

					if (as != null) {
						UserValid uv = userValidDao
								.getUserValidByUnAuthToken(unAuthToken);
						long now = TimeGenerator.getInstance().getUnixTime();

						if (uv != null) {
							long unAuthTokenTime = uv.getUnAuthTokenTime();

							if (now >= unAuthTokenTime) {
								text = AuthTips.TIMEOUTTOKEN.getText();
							} else {
								long authTokenTimeL = 0;
								String authTokenTime = PropertiesUtils
										.getStringByKey("auth.authtime");
								appId += now;
								String authToken = makeCode(appId);
								authTokenTimeL = authTokenTime != null ? Long
										.parseLong(authTokenTime) : 0;

								uv.setAuthToken(authToken);
								uv.setAuthTokenTime(now + authTokenTimeL);
								uv.setUserId(Integer.parseInt(userId));
								uv.setInfo(3);
								userValidDao.setUnAuthToken(uv);
								code = "200";
								text = authToken;
								ret.put("url", as.getCallBackUrl());
								ret.put("name", as.getAppName());
								ret.put("accountStatus", this.loginAbleStatus(
										Integer.valueOf(as.getId()), Integer
												.parseInt(userId)));
							}
						} else {
							logger.warn("uservalid is null");
							text = AuthTips.INVALIDAPPID.getText();
						}
					} else {
						logger.warn("appsecret is null");
						text = AuthTips.INVALIDAPPID.getText();
					}
				}
			}
			ret.put("code", code);
			ret.put("text", text);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return ret;
	}

	/**
	 * 获取用户是否有某应用权限
	 * 
	 * @param appName
	 * @param userId
	 * @return
	 */
	private boolean loginAbleStatus(Integer appId, Integer userId) {
		try {
			List roleIds = appinfoconfigDao.getRoleIdsByAppId(appId);
			
			StringBuilder sb = new StringBuilder();
			List exist = new ArrayList();
			List memberIds = null;
			
			if (roleIds != null) {
				int len = roleIds.size();
				for(int i = 0; i < len; i++) {
					if (!exist.contains(roleIds.get(i))) {
						sb.append(roleIds.get(i)).append(",");
						exist.add(roleIds.get(i));
					}
				}
			}
			String sbStr = sb.toString();
			
			if (sbStr.endsWith(",")) {
				sbStr = sbStr.substring(0, sbStr.length() - 1);
			}
			
			if (sbStr.length() > 0) {
				memberIds = memberRoleDao.getMemberIdsByRoleIds(sbStr);
			}

			if (memberIds != null && memberIds.contains(userId)) {
				return true;
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));			
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public String getAppSecretByAppIdAndSecret(String appId, String secret) {
		JSONObject jo = new JSONObject();
		String code = "0";
		String text = null;
		
		try {
			if (!StringUtils.getInstance().isBlank(appId) && !StringUtils.getInstance().isBlank(secret)) {
				AppSecret as = appSecretDao.getAppSecretByAppIdAndSecret(appId, secret);
				if (as != null) {
					code = "1";
					text = JSONUtils.getInstance().modelToJSONObj(as).toString();
				} 
			}
			jo.put("code", code);
			jo.put("text", text);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		return jo.toString();
	}

	private ArrayList<String> makeAppId() {
		ArrayList<String> as = new ArrayList<String>();

		try {
			String id = PasswordGenerator.getInstance().createId(18);
			as.add(id);
			as.add(new SecretUtils().encrypt(id));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return as;
	}

	private String makeCode(String str) {
		String code = null;

		str = str == null ? "" : str;
		try {
			code = new SecretUtils().encrypt(str);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return code;
	}

	private String coverCode(String str) {
		String code = null;

		str = str == null ? "" : str;
		try {
			code = new SecretUtils().decrypt(str);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return code;
	}

	private String isBlank(Object o) {
		return o == null ? "" : o + "";
	}

	private AppSecretDao appSecretDao;
	private UserValidDao userValidDao;
	private MemberDao memberDao;
	private AppInfoConfigDao appinfoconfigDao;
	private MemberRoleDao memberRoleDao;

	public void setMemberRoleDao(MemberRoleDao memberRoleDao) {
		this.memberRoleDao = memberRoleDao;
	}

	public void setAppinfoconfigDao(AppInfoConfigDao appinfoconfigDao) {
		this.appinfoconfigDao = appinfoconfigDao;
	}

	public void setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
	}

	public void setUserValidDao(UserValidDao userValidDao) {
		this.userValidDao = userValidDao;
	}

	public void setAppSecretDao(AppSecretDao appSecretDao) {
		this.appSecretDao = appSecretDao;
	}

}
