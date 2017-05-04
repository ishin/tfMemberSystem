package com.organ.action.abutment;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


import com.googlecode.sslplugin.annotation.Secured;
import com.organ.common.BaseAction;
import com.organ.common.Tips;
import com.organ.model.TMember;
import com.organ.service.adm.BranchService;
import com.organ.service.member.MemberService;
import com.organ.service.upload.UploadService;
import com.organ.utils.JSONUtils;
import com.organ.utils.LogUtils;
import com.organ.utils.StringUtils;

/**
 * 成员action
 * 
 * @author hao_dy
 * @since jdk1.7
 * @date 2017/01/07
 */


public class AbutmentMemberAction extends BaseAction {

	private static final long serialVersionUID = -7324946068454866523L;
	private static final Logger logger = LogManager.getLogger(AbutmentMemberAction.class);
	
	/**
	 * 依据账号获取成员
	 * @return
	 * @throws ServletException
	 */
	public String getMemberByAccountAb() throws ServletException {
		String result = null;
		JSONObject jo = new JSONObject();
		
		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (params != null) {
				JSONObject param = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(param)) {
					s = false;
				} else {
					String account = param.getString("account");
					int organId = param.getInt("organId");
					TMember tm = branchService.getMemberByAccount(account, organId);
					jo.put("code", 1);
					jo.put("text", JSONUtils.getInstance().modelToJSONObj(tm).toString());
				}
			} else {
				s = false;
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		
		result = jo.toString();
		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 获取成员指定参数
	 * 
	 * @return
	 * @throws ServcletException
	 */
	public String getMemberParamAb() throws ServletException {
		String result = null;
		JSONObject ret = new JSONObject();
		
		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (params != null) {
				JSONObject jo = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(jo)) {
					s = false;
				} else {
					String id = jo.getString("id");
					String ps = jo.getString("params");
					result = memberService.getMemberParam(id, ps);
				}
			} else {
				s = false;
			}
			if (!s) {
				ret.put("code", 0);
				ret.put("text", Tips.WRONGPARAMS.getText());
				result = ret.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 根据token获取成员
	 */
	public String getMemberByTokenAb() throws ServletException {
		String result = null;
		JSONObject jo = new JSONObject();
		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (params != null) {
				JSONObject param = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(param)) {
					s = false;
				} else {
					String token = param.getString("token");
					TMember tm = memberService.getMemberByToken(token);
					if (tm != null) {
						result = JSONUtils.getInstance().modelToJSONObj(tm)
								.toString();
					} else {
						s = false;
					}
				}
			} else {
				s = false;
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 获取指定数量的用户id
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getLimitMemberIdsAb() throws ServletException {
		String result = null;
		JSONObject jo = new JSONObject();
		
		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (params != null) {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(p)) {
					s = false;
				} else {
					String mapMax = p.getString("mapMax");
					int organId = p.getInt("organId");
					result = memberService.getLimitMemberIds(mapMax, organId);
				}
			} else {
				s = false;
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 根据id获取单个成员
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getMemberForIdAb() throws ServletException {
		String result = null;
		JSONObject jo = new JSONObject();
		
		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (params != null) {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(p)) {
					s = false;
				} else {
					String userId = p.getString("userId");
					result = memberService.getMemberForId(userId);
				}
			} else {
				s = false;
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 根据id获取多个成员
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getMultipleMemberForIdsAb() throws ServletException {
		String result = null;
		JSONObject jo = new JSONObject();
		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (params != null) {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(p)) {
					s = false;
				} else {
					String ids = p.getString("ids");
					result = memberService.getMultipleMemberForIds(ids);
				}
			} else {
				s = false;
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 根据账号获取id
	 * 
	 * @return
	 */
	public String getMemberIdForAccountAb() throws ServletException {
		String result = null;
		JSONObject jo = new JSONObject();
		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (params != null) {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(p)) {
					s = false;
				} else {
					String account = p.getString("account");
					int organId = p.getInt("organId");
					result = memberService.getMemberIdForAccount(account, organId);
				}
			} else {
				s = false;
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 登陆验证
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String checkAccountAb() throws ServletException {
		JSONObject result = new JSONObject();
		
		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (params != null) {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(p)) {
					s = false;
				} else {
					String account = p.getString("account");
					String pwd = p.getString("password");
					int organId = p.getInt("organId");
					TMember tm = memberService.searchSigleUser(account, pwd, organId);
					result.put("code", 1);
					result.put("text", JSONUtils.getInstance().modelToJSONObj(tm)
							.toString());
				}
			} else {
				s = false;
			}
			if (!s) {
				result.put("code", 0);
				result.put("text", Tips.WRONGPARAMS.getText());
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		logger.info(result.toString());
		returnToClient(result.toString());
		return "text";
	}

	/**
	 * 获取单个成员信息
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String getOneOfMemberAb() throws ServletException, IOException {
		String result = null;
		JSONObject jo = new JSONObject();
		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (params != null) {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(p)) {
					s = false;
				} else {
					String userId = p.getString("userId");
					result = memberService.getOneOfMember(userId);
				}
			} else {
				s = false;
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 搜索用户(账号、拼音)
	 * 
	 * @return
	 * @throws Servlet
	 */
	public String searchUserAb() throws ServletException {
		String result = null;
		JSONArray ja = new JSONArray();
		
		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (params == null) {
				s = false;
			} else {
				JSONObject jo = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(jo)) {
					s = false;
				} else {
					String account = jo.getString("account");
					int organId = jo.getInt("organId");
					result = memberService.searchUser(account, organId);
				}
			}
			if (!s) {
				JSONObject j = new JSONObject();
				j = new JSONObject();
				j.put("code", 0);
				j.put("text", Tips.WRONGPARAMS.getText());
				ja.add(j);
				result = ja.toString();
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}

		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 个人设置保存(web端)
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String updateMemberInfoForWebAb() throws ServletException {
		String result = null;
		
		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (params != null) {
				JSONObject jo = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(jo)) {
					s = false;
				} else {
					String userId = jo.getString("userId");
					String position = jo.containsKey("position") ? jo.getString("position") : null;
					String fullName = jo.containsKey("fullName") ? jo.getString("fullName"): null;
					String sign = jo.containsKey("signature") ? jo.getString("signature") : null;
					result = memberService.updateMemberInfoForWeb(userId, position,
							fullName, sign);
				}
			} else {	
				s = false;
			}
			if (!s) {
				JSONObject j = new JSONObject();
				j = new JSONObject();
				j.put("code", 0);
				j.put("text", Tips.WRONGPARAMS.getText());
				result = j.toString();
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 个人信息保存app端
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String updateMemberInfoForAppAb() throws ServletException {
		String result = null;

		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (params != null) {
				JSONObject jo = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(jo)) {
					s = false;
				} else {
					String userId = jo.getString("userId");
					String email = jo.containsKey("email") ? jo.getString("email") : null;
					String mobile = jo.containsKey("mobile") ? jo.getString("mobile") : null;
					String phone = jo.containsKey("phone") ? jo.getString("phone") : null;
					String address = jo.containsKey("address") ? jo.getString("address") : null;
					result = memberService.updateMemberForApp(userId, email,
							mobile, phone, address);
				}
			} else {
				s = false;
			}
			if (!s) {
				JSONObject j = new JSONObject();
				j = new JSONObject();
				j.put("code", 0);
				j.put("text", Tips.WRONGPARAMS.getText());
				result = j.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 获取所有成员
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getAllMemberInfoAb() throws ServletException {
		String result = null;
		JSONObject jo = new JSONObject();
		
		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (params == null) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(p)) {
					s = false;
				} else {
					int organId = p.getInt("organId");
					result = memberService.getAllMemberInfo(organId);
				}
			}
			if (!s) {
				JSONObject j = new JSONObject();
				j = new JSONObject();
				j.put("code", 0);
				j.put("text", Tips.WRONGPARAMS.getText());
				result = j.toString();
			}
		} catch(Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 获取成员在线状态，如果不传成员id，则表示全部成员
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getAllMemberOnLineStatusAb() throws ServletException {
		String result = null;

		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (!params.equals("")) {
				JSONObject jo = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(jo)) {
					s = false;
				} else {
					String userIds = jo.containsKey("userIds") ? jo.getString("userIds") : null;
					int organId = jo.containsKey("organId") ? jo.getInt("organId") : 0;
					result = memberService.getAllMemberOnLineStatus(organId, userIds);
				}
			}
			if (!s) {
				JSONObject ret = new JSONObject();
				ret.put("code", 0);
				ret.put("text", Tips.WRONGPARAMS.getText());
				result = ret.toString();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		
		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 多账号查询成员
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getMultipleMemberForAccountsAb() throws ServletException {
		String result = null;

		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (params != null) {
				JSONObject jo = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(jo)) {
					s = false;
				} else {
					String mulMemberStr = jo.getString("mulMemberStr");
					int organId = jo.containsKey("organId") ? jo.getInt("organId") : 0;
					result = memberService.getMultipleMemberForAccounts(mulMemberStr, organId);
				}
			} else {
				JSONObject ret = new JSONObject();
				ret.put("code", 0);
				ret.put("text", Tips.WRONGPARAMS.getText());
				result = ret.toString();
			}
			if (!s) {
				JSONObject ret = new JSONObject();
				ret.put("code", 0);
				ret.put("text", Tips.WRONGPARAMS.getText());
				result = ret.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		
		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 统计成员数量
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getMemberCountAb() throws ServletException {
		String result = null;
		JSONObject ret = new JSONObject();
		
		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (params != null) {
				JSONObject jo = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(jo)) {
					s = false;
				} else {
					int organId = jo.getInt("organId");
					int count = memberService.countMember(organId);
					ret.put("code", 1);
					ret.put("text", count);
				}
			} else {
				s = false;
			}
			if (!s) {
				ret.put("code", 0);
				ret.put("text", Tips.WRONGPARAMS.getText());
			}
			result = ret.toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 更新token
	 * 
	 * @return
	 * @throws SevletException
	 */
	public String updateUserTokenForIdAb() throws ServletException {
		String result = null;
		JSONObject ret = new JSONObject();

		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (params != null) {
				JSONObject jo = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(jo)) {
					s = false;
				} else {
					String userId = jo.getString("userId");
					String token = jo.getString("token");
					int count = memberService.updateUserTokenForId(userId, token);
					ret.put("code", 1);
					ret.put("text", count);
				}
			} else {
				s = false;
			}
			if (!s) {
				ret.put("code", 0);
				ret.put("text", Tips.WRONGPARAMS.getText());
			}
			result = ret.toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 根据账号获取id
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getMemberIdsByAccountAb() throws ServletException {
		String result = null;

		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (StringUtils.getInstance().isBlank(params)) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(p)) {
					s = false;
				} else {
					String names = p.getString("accounts");
					int organId = p.getInt("organId");
					result = memberService.getMemberIdsByAccount(names, organId);
				}
			}
			if (!s) {
				JSONObject jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 获取短信码
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getTextCodeAb() throws ServletException {
		String result = null;
		JSONObject jo = new JSONObject();

		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (StringUtils.getInstance().isBlank(params)) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(p)) {
					s = false;
				} else {
					String phone = p.getString("phone");
					result = memberService.getTextCode(phone);
					jo.put("code", 1);
					jo.put("text", result);
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}

		logger.info(jo.toString());
		returnToClient(jo.toString());
		return "text";
	}

	/**
	 * 保存短信验证码记录
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String saveTextCodeAb() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;

		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (StringUtils.getInstance().isBlank(params)) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(p)) {
					s = false;
				} else {
					String phone = p.getString("phone");
					String code = p.getString("code");
					memberService.saveTextCode(phone, code);
					jo.put("code", 1);
					jo.put("text", result);
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}

		logger.info(jo.toString());
		returnToClient(jo.toString());
		return "text";
	}

	/**
	 * 验证旧密码
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String valideOldPwdAb() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;

		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (StringUtils.getInstance().isBlank(params)) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(p)) {
					s = false;
				} else {
					String account = p.getString("account");
					String oldPwd = p.getString("oldPwd");
					int organId = p.getInt("organId");
					boolean status = memberService.valideOldPwd(account, oldPwd, organId);
					jo.put("code", 1);
					jo.put("text", status);
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
			}
			result = jo.toString();
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}

		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 依据账号更新密码
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String updateUserPwdForAccountAb() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;

		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (StringUtils.getInstance().isBlank(params)) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(p)) {
					s = false;
				} else {
					String account = p.getString("account");
					String newPwd = p.getString("newPwd");
					int organId = p.getInt("organId");
					boolean status = memberService.updateUserPwdForAccount(account, newPwd, organId);
					jo.put("code", 1);
					jo.put("text", status);
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
			}
			result = jo.toString();
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}

		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 依据手机号更新密码
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String updateUserPwdForPhoneAb() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;

		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (StringUtils.getInstance().isBlank(params)) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(p)) {
					s = false;
				} else {
					String account = p.getString("phone");
					String newPwd = p.getString("newPwd");
					boolean status = memberService.updateUserPwdForPhone(account,
							newPwd);
					jo.put("code", 1);
					jo.put("text", status);
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
			}
			result = jo.toString();
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}

		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 保存用户选择的头像
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String saveSelectedPicAb() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;

		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (StringUtils.getInstance().isBlank(params)) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(p)) {
					s = false;
				} else {
					String userId = p.getString("userId");
					String picName = p.getString("picName");
					result = uploadService.saveSelectedPic(userId, picName);
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}

		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 保存成员头像
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String saveTempPicAb() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;

		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (StringUtils.getInstance().isBlank(params)) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(p)) {
					s = false;
				} else {
					String logName = p.getString("logName");
					String userId = p.getString("userId");
					result = uploadService.saveTempPic(userId, logName);
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}

		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 删除成员头像
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String delUserLogosAb() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;

		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (StringUtils.getInstance().isBlank(params)) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(p)) {
					s = false;
				} else {
					String picName = p.getString("picName");
					String userId = p.getString("userId");
					result = uploadService.delUserLogos(userId, picName);
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}

		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 检测 是否正在使用头像
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String isUsedPicAb() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;

		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (StringUtils.getInstance().isBlank(params)) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(p)) {
					s = false;
				} else {
					String picName = p.getString("picName");
					String userId = p.getString("userId");
					result = memberService.isUsedPic(userId, picName);
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}

		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 获取头像列表
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getUserLogosAb() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;

		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			logger.info(params);
			if (StringUtils.getInstance().isBlank(params)) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(p)) {
					s = false;
				} else {
					String userId = p.getString("userId");
					result = uploadService.getUserLogos(userId);
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}

		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 通过http方式上传的图片
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException 
	 */
	public String httpUploadAb() throws ServletException, IOException {
		String fileName = request.getParameter("fileName");
		InputStream input = request.getInputStream();
		String realPath = request.getSession().getServletContext().getRealPath("/");  
		String result = uploadService.httpUpload(fileName, input, realPath);
		logger.info(result);
		returnToClient(result);
		return "text";
	}

	private MemberService memberService;
	private UploadService uploadService;
	private BranchService branchService;
	
	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}

	public void setUploadService(UploadService uploadService) {
		this.uploadService = uploadService;
	}

	public void setMemberService(MemberService ms) {
		this.memberService = ms;
	}

}
