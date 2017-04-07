package com.organ.service.member.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.util.ArrayUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.organ.common.Tips;
import com.organ.dao.adm.BranchMemberDao;
import com.organ.dao.adm.PositionDao;
import com.organ.dao.member.MemberDao;
import com.organ.dao.member.TextCodeDao;
import com.organ.model.TMember;
import com.organ.model.TextCode;
import com.organ.service.member.MemberService;
import com.organ.utils.JSONUtils;
import com.organ.utils.LogUtils;
import com.organ.utils.PasswordGenerator;
import com.organ.utils.PinyinGenerator;
import com.organ.utils.PropertiesUtils;
import com.organ.utils.RongCloudUtils;
import com.organ.utils.StringUtils;
import com.organ.utils.TimeGenerator;

public class MemberServiceImpl implements MemberService {

	private static final Logger logger = Logger.getLogger(MemberServiceImpl.class);
	
	@Override
	public TMember searchSigleUser(String name, String password) {
		TMember memeber = null;

		try {
			// password = PasswordGenerator.getInstance().getMD5Str(password);
			// //前端加密
			memeber = memberDao.searchSigleUser(name, password);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		return memeber;
	}

	@Override
	public boolean updateUserPwdForAccount(String account, String newPwd) {
		boolean status = false;

		try {
			// String md5Pwd =
			// PasswordGenerator.getInstance().getMD5Str(newPwd);

			status = memberDao.updateUserPwdForAccount(account, newPwd);
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
			status = memberDao.updateUserPwdForPhone(phone, newPwd);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return status;
	}

	@Override
	public String getOneOfMember(String userId) {

		JSONObject jo = new JSONObject();

		try {
			int userIdInt = StringUtils.getInstance().strToInt(userId);

			Object[] member = memberDao.getOneOfMember(userIdInt);

			if (member == null) {
				jo.put("code", 0);
				jo.put("text", Tips.NULLUSER.getText());
			} else {
				for (int i = 0; i < member.length; i++) {
					jo.put("id", isBlank(member[0]));
					jo.put("account", isBlank(member[1]));
					jo.put("name", isBlank(member[2]));
					jo.put("logo", isBlank(member[3]));
					jo.put("telephone", isBlank(member[4]));
					jo.put("email", isBlank(member[5]));
					jo.put("address", isBlank(member[6]));
					jo.put("token", isBlank(member[7]));
					jo.put("sex", isBlank(member[8]));
					jo.put("birthday", isBlank(member[9]));
					jo.put("workno", isBlank(member[10]));
					jo.put("mobile", isBlank(member[11]));
					jo.put("intro", isBlank(member[12]));
					jo.put("branchid", isBlank(member[13]));
					jo.put("branchname", isBlank(member[14]));
					jo.put("positionid", isBlank(member[15]));
					jo.put("positionname", isBlank(member[16]));
					jo.put("organid", isBlank(member[17]));
					jo.put("organname", isBlank(member[18]));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		return jo.toString();
	}

	@Override
	public int updateUserTokenForId(String userId, String token) {
		int row = 0;

		try {
			row = memberDao.updateUserTokenForId(userId, token);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return row;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String searchUser(String account) {
		JSONArray ja = new JSONArray();

		try {
			List members = memberDao.searchUser(account);

			if (members == null) {
				JSONObject jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", Tips.NULLUSER.getText());
			} else {
				for (int i = 0; i < members.size(); i++) {
					Object[] member = (Object[]) members.get(i);
					JSONObject jo = new JSONObject();
					jo.put("id", isBlank(member[0]));
					jo.put("account", isBlank(member[1]));
					jo.put("name", isBlank(member[2]));
					jo.put("logo", isBlank(member[3]));
					jo.put("telephone", isBlank(member[4]));
					jo.put("email", isBlank(member[5]));
					jo.put("address", isBlank(member[6]));
					jo.put("birthday", isBlank(member[7]));
					jo.put("workno", isBlank(member[8]));
					jo.put("mobile", isBlank(member[9]));
					jo.put("groupmax", isBlank(member[10]));
					jo.put("groupuse", isBlank(member[11]));
					jo.put("intro", isBlank(member[12]));
					jo.put("branchname", isBlank(member[13]));
					jo.put("positionname", isBlank(member[14]));
					jo.put("organname", isBlank(member[15]));
					jo.put("sex", isBlank(member[16]));
					ja.add(jo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		return ja.toString();
	}

	@Override
	public boolean valideOldPwd(String account, String oldPwd) {
		boolean b = false;

		try {
			b = memberDao.valideOldPwd(account, oldPwd);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		return b;
	}

	@Override
	public String getTextCode(String phone) {
		String code = null;

		try {
			TextCode tc = textCodeDao.getTextCode(phone);

			if (tc != null) {
				long now = TimeGenerator.getInstance().getUnixTime();
				long createTime = tc.getCreateTime();
				long valideTime = StringUtils.getInstance().strToLong(
						PropertiesUtils.getStringByKey("code.validetime"));

				if ((now - createTime) >= valideTime) {
					code = "-1";
				} else {
					code = tc.getTextCode();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return code;
	}

	@Override
	public void saveTextCode(String phone, String code) {
		try {
			textCodeDao.deleteTextCode(phone);

			TextCode stc = new TextCode();
			stc.setPhoneNum(phone);
			stc.setTextCode(code);
			stc.setCreateTime(TimeGenerator.getInstance().getUnixTime());
			textCodeDao.saveTextCode(stc);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
	}

	@Override
	public String updateMemberInfoForWeb(String userId, String position,
			String fullName, String sign) {

		JSONObject jo = new JSONObject();

		if (StringUtils.getInstance().isBlank(userId)) {
			jo.put("code", -1);
			jo.put("text", Tips.WRONGPARAMS.getText());
		} else {
			try {
				int userIdInt = StringUtils.getInstance().strToInt(userId);
				int ret = memberDao.updateMemeberInfoForWeb(userIdInt,
						fullName, sign);

				if (ret > 0) {

					if (!StringUtils.getInstance().isBlank(position)) {
						int positionId = StringUtils.getInstance().strToInt(
								position);
						branchMemberDao.updatePositionByUseId(userIdInt,
								positionId);
					}
					jo.put("code", 1);
					jo.put("text", Tips.OK.getText());
				} else {
					jo.put("code", 0);
					jo.put("text", Tips.FAIL.getText());
				}

			} catch (Exception e) {
				e.printStackTrace();
				logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			}
		}

		return jo.toString();
	}

	@Override
	public boolean updateUserPwd(String account, String newPwd) {
		boolean status = false;

		try {
			String md5Pwd = PasswordGenerator.getInstance().getMD5Str(newPwd);

			status = memberDao.updateUserPwd(account, md5Pwd);
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

		if (StringUtils.getInstance().isBlank(userId)) {
			jo.put("code", -1);
			jo.put("text", Tips.WRONGPARAMS.getText());
		} else {
			try {
				int userIdInt = StringUtils.getInstance().strToInt(userId);
				int ret = memberDao.updateMemeberInfoForApp(userIdInt, email,
						mobile, phone, address);

				if (ret > 0) {
					jo.put("code", 1);
					jo.put("text", Tips.OK.getText());
				} else {
					jo.put("code", 0);
					jo.put("text", Tips.FAIL.getText());
				}

			} catch (Exception e) {
				e.printStackTrace();
				logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			}
		}

		return jo.toString();
	}

	@Override
	public TMember getMemberByToken(String token) {

		try {
			if (!StringUtils.getInstance().isBlank(token)) {
				TMember member = memberDao.getMemberByToken(token);

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
	public String getAllMemberInfo() {
		JSONObject jo = new JSONObject();

		try {
			List<TMember> memberList = memberDao.getAllMemberInfo();

			if (memberList != null) {
				int memberLen = memberList.size();

				JSONArray ja = new JSONArray();

				for (int i = 0; i < memberLen; i++) {
					TMember tms = memberList.get(i);
					JSONObject text = JSONUtils.getInstance().modelToJSONObj(
							tms);
					ja.add(text);
				}
				jo.put("code", 1);
				jo.put("text", ja.toString());
			} else {
				jo.put("code", 0);
				jo.put("text", Tips.NULLGROUPMEMBER.getText());
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return jo.toString();
	}

	@Override
	public String getAllMemberOnLineStatus(String userIds) {
		JSONObject jo = new JSONObject();

		try {
			ArrayList<String> idList = null;

			if (StringUtils.getInstance().isBlank(userIds)) {
				idList = new ArrayList<String>();
				List<TMember> memberList = memberDao.getAllMemberInfo();
				if (memberList != null) {
					int memberLen = memberList.size();
					for (int i = 0; i < memberLen; i++) {
						TMember tms = memberList.get(i);
						String id = tms.getId() + "";
						idList.add(id);
					}
				}
			} else {
				userIds = StringUtils.getInstance().replaceChar(userIds, "\"",
						"");
				userIds = StringUtils.getInstance().replaceChar(userIds, "[",
						"");
				userIds = StringUtils.getInstance().replaceChar(userIds, "]",
						"");

				String[] userIdses = userIds.split(",");

				idList = new ArrayList<String>(Arrays.asList(userIdses));
			}

			JSONObject ja = new JSONObject();
			// 各成员在线状态
			for (int i = 0; i < idList.size(); i++) {
				String id = idList.get(i);
				String status = RongCloudUtils.getInstance().checkOnLine(id);
				ja.put(id, status);
			}
			jo.put("code", 1);
			jo.put("text", ja.toString());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		return jo.toString();
	}

	@Override
	public String getMultipleMemberForAccounts(String mulMemberStr) {
		String[] mulMemberStrs = null;
		String ret = null;
		JSONObject jo = new JSONObject();
		
		if (!StringUtils.getInstance().isBlank(mulMemberStr)) {
			mulMemberStr = StringUtils.getInstance().replaceChar(mulMemberStr, "]", "");
			mulMemberStr = StringUtils.getInstance().replaceChar(mulMemberStr, "[", "");
			mulMemberStr = StringUtils.getInstance().replaceChar(mulMemberStr, "\"", "");
			mulMemberStrs = mulMemberStr.split(",");
			List<TMember> memberList = memberDao.getMultipleMemberForAccounts(mulMemberStrs);
			int[] ids = null;
			
			if (memberList != null) {
				int len = memberList.size();
				ids = new int[len];
				for (int i = 0; i < len; i++) {
					TMember t = memberList.get(i);
					ids[i] = t.getId();
				}
				jo.put("code", 1);
				jo.put("text", ids);
			} else {
				jo.put("code", 0);
				jo.put("text", Tips.NULLGROUPMEMBER.getText());
			}
		} else {
			jo.put("code", 0);
			jo.put("text", Tips.NULLUSER.getText());
		}
		ret = jo.toString();
		return ret;
	}
	
	@Override
	public int countMember() {
		try {
			int count = memberDao.getMemberCount();
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return 0;
	}

	@Override
	public String getMemberIdsByAccount(String names) {
		String code = "0";
		String text = null;
		JSONObject ret = new JSONObject();
		
		try {
			if (StringUtils.getInstance().isBlank(names)) {
				text = Tips.WRONGPARAMS.getText();
			} else {
				String[] namesArr = StringUtils.getInstance().strToArray(names);
				List list = memberDao.getMemberIdsByAccount(namesArr);
				
				if (list != null) {
					code = "1";
					text = list.toString();
				} else {
					text = Tips.NULLID.getText();
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
	public String isUsedPic(String userId, String picName) {
		JSONObject jo = new JSONObject();
		
		try {
			int userIdInt = Integer.parseInt(userId);
			boolean used = memberDao.isUsedPic(userIdInt, picName);
			jo.put("code", 1);
			jo.put("text", used);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		
		return jo.toString();
	}
	
	@Override
	public String getMemberIdForAccount(String account) {
		JSONObject jo = new JSONObject();
		
		try {
			int id = memberDao.getMemberIdForAccount(account);
			jo.put("code", 1);
			jo.put("text", id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		return jo.toString();
	}
	
	@Override
	public String getMultipleMemberForIds(String ids) {
		JSONObject jo = new JSONObject();
		
		try {
			String[] idArr = StringUtils.getInstance().strToArray(ids);
			int len = idArr.length;
			Integer[] idIntArr = new Integer[len];
			
			for(int i = 0; i < len; i++) {
				idIntArr[i] = Integer.parseInt(idArr[i]);
			}
			
			List<TMember> list = memberDao.getMultipleMemberForIds(idIntArr);
			List<JSONObject> lj = new ArrayList<JSONObject>();
			
 			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					JSONObject tm = JSONUtils.getInstance().modelToJSONObj(list.get(i));
					tm.remove("password");
					lj.add(tm);
				}
 			}
 			
			if (list != null) {
				jo.put("code", 1);
				jo.put("text", lj.toString());
			} else {
				jo.put("code", 0);
				jo.put("text", Tips.FAIL.getText());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		return jo.toString();
	}
	
	@Override
	public String getMemberForId(String userId) {
		JSONObject jo = new JSONObject();
		
		try {
			int userIdInt = Integer.parseInt(userId);
			TMember tm = memberDao.getMemberForId(userIdInt);
			JSONObject j = JSONUtils.getInstance().modelToJSONObj(tm);
			j.remove("password");
			if (tm != null) {
				jo.put("code", 1);
				jo.put("text", j.toString());
			} else {
				jo.put("code", 0);
				jo.put("text", Tips.FAIL.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		
		return null;
	}
	
	@Override
	public String getLimitMemberIds(String mapMax) {
		JSONObject jo = new JSONObject();
		
		try {
			int mapMaxInt = Integer.parseInt(mapMax);
			List<TMember> tm = memberDao.getLimitMemberIds(mapMaxInt);
			List<JSONObject> ret = new ArrayList<JSONObject>();
			
			if (tm != null) {
				for(int i = 0; i < tm.size(); i++) {
					ret.add(JSONUtils.getInstance().modelToJSONObj(tm.get(i)));
				}
				jo.put("code", 1);
				jo.put("text", ret.toString());
			} else {
				jo.put("code", 0);
				jo.put("text", Tips.FAIL.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		
		return null;
	}
	

	@Override
	public String getMemberParam(String id, String ps) {
		JSONObject jo = new JSONObject();
		String code = "0";
		String text = null;
		
		try {
			if (!StringUtils.getInstance().isBlank(id) && !StringUtils.getInstance().isBlank(ps)) {
				id = StringUtils.getInstance().replaceChar(id, "[", "");
				id = StringUtils.getInstance().replaceChar(id, "]", "");
				id = StringUtils.getInstance().replaceChar(id, "\"", "");
				String[] pss = StringUtils.getInstance().strToArray(ps);

				List memList = memberDao.getMemberParam(id, pss);
				JSONArray ja = new JSONArray();
				
				if (memList != null) {
					code = "1";
					for(int i = 0; i < memList.size(); i++) {
						JSONObject t = new JSONObject();
						Object[] o = (Object[]) memList.get(i);
						t.put("userID", o[0]);
						t.put("logo", o[1]);
						ja.add(t);
					}
					text = ja.toString();
				} else {
					text = Tips.FAIL.getText();
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

	private String isBlank(Object o) {
		return o == null ? "" : o + "";
	}

	private TextCodeDao textCodeDao;
	private MemberDao memberDao;
	private BranchMemberDao branchMemberDao;

	public void setBranchMemberDao(BranchMemberDao branchMemberDao) {
		this.branchMemberDao = branchMemberDao;
	}

	public void setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
	}

	public TextCodeDao getTextCodeDao() {
		return textCodeDao;
	}

	public void setTextCodeDao(TextCodeDao textCodeDao) {
		this.textCodeDao = textCodeDao;
	}

	public MemberDao getMemberDao() {
		return memberDao;
	}
}
