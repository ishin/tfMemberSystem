package com.organ.action.adm;

import java.util.Map;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import com.googlecode.sslplugin.annotation.Secured;
import com.organ.common.BaseAction;
import com.organ.common.Tips;
import com.organ.model.TOrgan;
import com.organ.service.adm.OrgService;
import com.organ.utils.PasswordGenerator;
import com.organ.utils.PropertiesUtils;
import com.organ.utils.StringUtils;
import com.organ.utils.TimeGenerator;

/**
 * 多系统
 * @author hao_dy
 *
 */

public class FKMultOrganAction extends BaseAction {

	private static final long serialVersionUID = 6961033310590131108L;
	
	/**
	 * 注册
	 * @return
	 * @throws ServletException
	 */
	@SuppressWarnings("unchecked")
	public String registOrgan() throws ServletException {
		String result = null;
		JSONObject jo = new JSONObject();
		
		String[] mustParams = {"name", "shortname", "provinceid", "cityid", "districtid", "contact", "address", "sign", "timestamp"};
		
		result = this.valideParams(this.request, mustParams);
		
		if (result == null) {
			String timeStamp = this.request.getParameter("timestamp");
			String validTime = PropertiesUtils.getStringByKey("organ.validtime");
			String key = PropertiesUtils.getStringByKey("organ.key");
			long validTimeLong = validTime != null ? Long.parseLong(validTime) : 0;
			Map<String, String[]> paramMap = this.request.getParameterMap();
			JSONObject jsonParam = new JSONObject();

			for(Map.Entry<String, String[]> m : paramMap.entrySet()) {
				String mapKey = m.getKey();
				String value = m.getValue()[0];
				jsonParam.put(mapKey, value);
			}
			
			boolean valid = PasswordGenerator.getInstance().valideMd5(jsonParam, timeStamp, validTimeLong, key);
			
			if (!valid) {
				result = Tips.VALIDFAIL.getText();
			} else {
				TOrgan organ = new TOrgan();
				
				organ.setName(this.request.getParameter("name"));
				organ.setShortname(this.request.getParameter("shortname"));
				organ.setEnglishname(this.request.getParameter("englishname"));
				organ.setAd(this.request.getParameter("ad"));
				organ.setProvinceId(this.request.getParameter("provinceid") == "" ? 0 : Integer.parseInt(this.request.getParameter("provinceid")));
				organ.setCityId(this.request.getParameter("cityid") == "" ? 0 : Integer.parseInt(this.request.getParameter("cityid")));
				organ.setDistrictId(this.request.getParameter("districtid") == "" ? 0 : Integer.parseInt(this.request.getParameter("districtid")));
				organ.setContact(this.request.getParameter("contact"));
				organ.setAddress(this.request.getParameter("address"));
				organ.setTelephone(this.request.getParameter("telephone"));
				organ.setFax(this.request.getParameter("fax"));
				organ.setEmail(this.request.getParameter("email"));
				organ.setPostcode(this.request.getParameter("postcode"));
				organ.setWebsite(this.request.getParameter("website"));
				organ.setInwardId(this.request.getParameter("inwardid") == "" ? 0 : Integer.parseInt(this.request.getParameter("inwardid")));
				organ.setIndustryId(this.request.getParameter("industryid") == "" ? 0 : Integer.parseInt(this.request.getParameter("industryid")));
				organ.setSubdustryId(this.request.getParameter("subdustryid") == "" ? 0 : Integer.parseInt(this.request.getParameter("subdustryid")));
				organ.setCapital(this.request.getParameter("capital") == "" ? 0 : Integer.parseInt(this.request.getParameter("capital")));
				organ.setMembernumber(this.request.getParameter("membernumber") == "" ? 0 : Integer.parseInt(this.request.getParameter("membernumber")));
				organ.setComputernumber(this.request.getParameter("computernumber") == "" ? 0 : Integer.parseInt(this.request.getParameter("computernumber")));
				organ.setIntro(this.request.getParameter("intro"));
				organ.setLogo(this.request.getParameter("logo"));
				organ.setListorder(StringUtils.getInstance().isBlank(this.request.getParameter("listorder")) ? 0 : Integer.parseInt(this.request.getParameter("listorder")));
				
				result = orgService.registOrgan(organ);
			}
		}
		
		jo.put("code", 1);
		jo.put("text", result);
		returnToClient(jo.toString());
		return "text";
	}
	
	/**
	 * 获取组织列表
	 * @return
	 * @throws ServletException
	 */
	public String getList() throws ServletException {
		String result = null;
		result = orgService.getList();
		returnToClient(result);
		return "text";
	}
	
	@Deprecated
	private boolean valideMd5() {
		String timestamp = this.request.getParameter("timestamp");
		String validTime = PropertiesUtils.getStringByKey("organ.validtime");
		
		long validTimeLong = Long.parseLong(validTime);
		long now = TimeGenerator.getInstance().getUnixTime();
		long maxTime = now + validTimeLong;
		long minTime = now - validTimeLong;
		
		long timeStampLong = timestamp != null ? Long.parseLong(timestamp) : 0;
		
		if (timeStampLong < minTime || timeStampLong > maxTime) {
			return false;
		}
		String sign = this.request.getParameter("sign");
		String key = PropertiesUtils.getStringByKey("organ.key");
		
		Map<String, String[]> paramMap = this.request.getParameterMap();
		StringBuilder sbp = new StringBuilder();
		
		for(Map.Entry<String, String[]> m: paramMap.entrySet()) {
			if (m.getKey().equals("sign")) continue;
			sbp.append(m.getKey()).append("=");
			String[] t = m.getValue();
			for(int i = 0; i < t.length; i++) {
				sbp.append(t[i]);
			}
		}
		String pStr = sbp.toString();
		pStr = StringUtils.getInstance().sortByChars(pStr);
		pStr = key + pStr + timestamp;
		String caclSign = PasswordGenerator.getInstance().getMD5Str(sbp.toString());
		
		System.out.println("sign: " + caclSign);
		
		if (!caclSign.equals(sign)) {
			return false;
		}
		
		return true;
	}
	
	private OrgService orgService;

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}
	
}
