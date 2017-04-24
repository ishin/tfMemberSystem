package com.organ.action.abutment;

import java.util.List;

import javax.servlet.ServletException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.googlecode.sslplugin.annotation.Secured;
import com.organ.common.BaseAction;
import com.organ.common.Tips;
import com.organ.service.adm.PrivService;
import com.organ.service.member.MemberService;
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

@Secured
public class AbutmentPrivilegeAction extends BaseAction {

	private static final long serialVersionUID = -140819709379846247L;
	private static final Logger logger = Logger
			.getLogger(AbutmentPrivilegeAction.class);
	
	
	public String getPrivStringByMemberAb() throws ServletException {
		String result = null;
		JSONObject jo = new JSONObject();
		
		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			if (params != null) {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(p)) {
					s = false;
				} else {
					String id = p.getString("id");
					String privList = privService.getPrivStringByMember(Integer.parseInt(id));
					
					if (privList != null) {
						jo.put("code", 1);
						jo.put("text", privList);
					} else {
						jo.put("code", 0);
						jo.put("text", Tips.FAIL.getText());
					}
				}
			} else {
				s = false;
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
			}
			result = jo.toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		returnToClient(result);
		return "text";
	}
	
	public String getMemberRolesByRoleIdsAb() throws ServletException {
		String result = null;
		JSONObject jo = new JSONObject();
		
		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			if (params != null) {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(p)) {
					s = false;
				} else {
					String ids = p.getString("ids");
					String privList = privService.getMemberRolesByRoleIds(StringUtils.getInstance().strToArray(ids));
					
					if (privList != null) {
						jo.put("code", 1);
						jo.put("text", privList);
					} else {
						JSONArray ja = new JSONArray();
						ja.add(Tips.FAIL.getText());
						jo.put("code", 0);
						jo.put("text", ja.toString());
						
					}
				}
			} else {
				s = false;
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
			}
			result = jo.toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 根据权限id获取角色
	 * @return
	 * @throws ServletException
	 */
	public String getRolePrivsByPrivsAb() throws ServletException {
		String result = null;
		JSONObject jo = new JSONObject();
		
		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			if (params != null) {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(p)) {
					s = false;
				} else {
					String privIds = p.getString("privIds");
					String privList = privService.getRolePrivsByPrivs(StringUtils.getInstance().strToArray(privIds));
					
					if (privList != null) {
						jo.put("code", 1);
						jo.put("text", privList);
					} else {
						JSONArray ja = new JSONArray();
						ja.add(Tips.FAIL.getText());
						jo.put("code", 0);
						jo.put("text", ja.toString());
					}
				}
			} else {
				s = false;
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
			}
			result = jo.toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 根据权限标识获取权限
	 * @return
	 * @throws ServletException
	 */
	public String getPrivByUrlAb() throws ServletException {
		String result = null;
		JSONObject jo = new JSONObject();
		
		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			if (params != null) {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(p)) {
					s = false;
				} else {
					String groupManager = p.getString("groupManager");
					int organId = p.getInt("organId");
					String privList = privService.getPrivByUrl(StringUtils.getInstance().strToArray(groupManager), organId);
					if (privList != null) {
						jo.put("code", 1);
						jo.put("text", privList);
					} else {
						jo.put("code", 0);
						jo.put("text", Tips.FAIL.getText());
					}
				}
			} else {
				s = false;
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
			}
			result = jo.toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 获取权限
	 * @return
	 * @throws ServletException
	 */
	public String getRoleIdForIdAb() throws ServletException {
		String result = null;
		JSONObject jo = new JSONObject();
		
		try {
			String params = getRequestDataByStream();
			boolean s = true;
			
			if (params != null) {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				
				if (!validParams(p)) {
					s = false;
				} else {
					String userId = p.getString("userId");
					List privList = privService.getRoleIdForId(Integer.parseInt(userId));
					if (privList != null) {
						JSONArray ja = JSONUtils.getInstance().objToJSONArray(privList);
						jo.put("code", 1);
						jo.put("text", ja.toString());
					} else {
						jo.put("code", 0);
						jo.put("text", Tips.FAIL.getText());
					}
				}
			} else {
				s = false;
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
			}
			result = jo.toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		returnToClient(result);
		return "text";
	}
	
	@Deprecated
	public String getInitLoginPrivAb() throws ServletException {
		String result = null;
		
		try {
			List<JSONObject> privList = privService.getInitLoginPriv();
			result = privList.toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		returnToClient(result);
		return "text";
	}
	
	private PrivService privService;

	public void setPrivService(PrivService privService) {
		this.privService = privService;
	}
	
}
