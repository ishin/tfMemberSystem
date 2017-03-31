package com.organ.service.member.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.organ.utils.PasswordGenerator;
import com.organ.utils.PinyinGenerator;
import com.organ.utils.PropertiesUtils;
import com.organ.utils.RongCloudUtils;
import com.organ.utils.StringUtils;
import com.organ.utils.TimeGenerator;

public class MemberServiceImpl implements MemberService {

	@Override
	public TMember searchSigleUser(String name, String password) {
		TMember memeber = null;

		try {
			// password = PasswordGenerator.getInstance().getMD5Str(password);
			// //前端加密
			memeber = memberDao.searchSigleUser(name, password);
		} catch (Exception e) {
			e.printStackTrace();
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
					// jo.put("groupmax", isBlank(member[12]));
					// jo.put("groupuse", isBlank(member[13]));
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
		}
		return jo.toString();
	}

	@Override
	public String getMultipleMemberForAccounts(String mulMemberStr) {
		//List<TMember> memberList = memberDao.getMultipleMemberForAccounts(mulMemberStr);
		return null;
	}
	
	@Override
	public int countMember() {
		try {
			int count = memberDao.getMemberCount();
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
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
