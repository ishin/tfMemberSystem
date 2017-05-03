package com.organ.action.adm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.googlecode.sslplugin.annotation.Secured;
import com.organ.common.BaseAction;
import com.organ.model.TMember;
import com.organ.model.TOrgan;
import com.organ.service.adm.OrgService;

import net.sf.json.JSONObject;


public class OrgAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1731481715198316913L;

	private OrgService orgService;

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public String getProvince() {

		List list = orgService.getProvince();

		handle(list);

		return "text";
	}

	public String getCity() {

		Integer provinceId = Integer.parseInt(clearChar(this.request.getParameter("provinceid")));
		List list = orgService.getCity(provinceId);

		handle(list);

		return "text";
	}

	public String getDistrict() {

		Integer cityId = Integer.parseInt(clearChar(this.request.getParameter("cityid")));
		List list = orgService.getDistrict(cityId);

		handle(list);

		return "text";
	}

	public String getInward() {

		List list = orgService.getInward();

		handle(list);

		return "text";
	}

	public String getIndustry() {

		List list = orgService.getIndustry();

		handle(list);

		return "text";
	}

	public String getSubdustry() {

		Integer industryId = Integer.parseInt(clearChar(this.request.getParameter("industryid")));
		List list = orgService.getSubdustry(industryId);

		handle(list);

		return "text";
	}

	public String getInfo() {

		System.out.println(this.getOrganId());
		TOrgan organ = orgService.getInfo(this.getOrganId());
		JSONObject js = new JSONObject();
		js.put("code", organ.getCode());
		js.put("name", organ.getName());
		js.put("shortname", organ.getShortname());
		js.put("englishname", organ.getEnglishname());
		js.put("ad", organ.getAd());
		js.put("provinceid", organ.getProvinceId());
		js.put("cityid", organ.getCityId());
		js.put("districtid", organ.getDistrictId());
		js.put("contact", organ.getContact());
		js.put("address", organ.getAddress());
		js.put("telephone", organ.getTelephone());
		js.put("fax", organ.getFax());
		js.put("email", organ.getEmail());
		js.put("postcode", organ.getPostcode());
		js.put("website", organ.getWebsite());
		js.put("inwardid", organ.getInwardId());
		js.put("industryid", organ.getIndustryId());
		js.put("subdustryid", organ.getSubdustryId());
		js.put("capital", organ.getCapital());
		js.put("membernumber", organ.getMembernumber());
		js.put("computernumber", organ.getComputernumber());
		js.put("intro", organ.getIntro());
		js.put("logo", organ.getLogo());
		js.put("listorder", organ.getListorder());
		returnToClient(js.toString());

		return "text";
	}
	// 判断一个字符串是否都为数字  
	public boolean isDigit(String strNum) {  
	    return strNum.matches("[0-9]{1,}");  
	} 
	public String save() {
		Integer organId = this.getOrganId();
		String code = clearChar(this.request.getParameter("code"));
		String name = clearChar(this.request.getParameter("name"));
		String shortName = clearChar(this.request.getParameter("shortname"));
		String englishName = clearChar(this.request.getParameter("englishname"));
		String ad = clearChar(this.request.getParameter("ad"));
		String provinceId = clearChar(this.request.getParameter("provinceid"));
		String cityId = clearChar(this.request.getParameter("cityid"));
		String distrinctId = clearChar(this.request.getParameter("districtid"));
		String contact = clearChar(this.request.getParameter("contact"));
		String address = clearChar(this.request.getParameter("address"));
		String telePhone = clearChar(this.request.getParameter("telephone"));
		String fax = clearChar(this.request.getParameter("fax"));
		String email = clearChar(this.request.getParameter("email"));
		String postCode = clearChar(this.request.getParameter("postcode"));
		String webSite = clearChar(this.request.getParameter("website"));
		String inwardId = clearChar(this.request.getParameter("inwardid"));
		String industryId = clearChar(this.request.getParameter("industryid"));
		String subdustryId = clearChar(this.request.getParameter("subdustryid"));
		String capital = clearChar(this.request.getParameter("capital"));
		String memberNumber = clearChar(this.request.getParameter("membernumber"));
		String computerNumber = clearChar(this.request.getParameter("computernumber"));
		String intro = clearChar(this.request.getParameter("intro"));
		String logo = clearChar(this.request.getParameter("logo"));
		String listOrder = clearChar(this.request.getParameter("listorder"));
		
		TOrgan organ = new TOrgan();
		organ.setId(organId);
		organ.setCode(code);
		organ.setName(name);
		organ.setShortname(shortName);
		organ.setEnglishname(englishName);
		organ.setAd(ad);
		organ.setProvinceId(StringUtils.isBlank(provinceId) ? 0 : isNumber(provinceId));
		organ.setCityId(StringUtils.isBlank(cityId) ? 0 : isNumber(cityId));
		organ.setDistrictId(StringUtils.isBlank(distrinctId) ? 0 : isNumber(distrinctId));
		organ.setContact(contact);
		organ.setAddress(address);
		organ.setTelephone(telePhone);
		organ.setFax(fax);
		organ.setEmail(email);
		organ.setPostcode(postCode);
		organ.setWebsite(webSite);
		organ.setInwardId(StringUtils.isBlank(inwardId) ? 0 : isNumber(inwardId));
		organ.setIndustryId(StringUtils.isBlank(industryId) ? 0 : isNumber(industryId));
		organ.setSubdustryId(StringUtils.isBlank(subdustryId) ? 0 : isNumber(subdustryId));
		organ.setCapital(StringUtils.isBlank(capital) ? 0 : isNumber(capital));
		organ.setMembernumber(StringUtils.isBlank(memberNumber) ? 0 : isNumber(memberNumber));
		organ.setComputernumber(StringUtils.isBlank(computerNumber) ? 0 : isNumber(computerNumber));
		organ.setIntro(intro);
		organ.setLogo(logo);
		organ.setListorder(Integer.parseInt(listOrder));
		orgService.save(organ);
		return returnajaxid(organId);
	}

	// 判断传进来的参数是否为数字
	private Integer isNumber(String name) {
		int num = 0;
		boolean flag = com.organ.utils.StringUtils.isDigit(name);
		if (flag) {
			num = Integer.parseInt(name);
		} else {
			num = 0;
		}
		return num;
	}

	private void handle(List list) {

		ArrayList<JSONObject> js = new ArrayList<JSONObject>();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Object[] o = (Object[]) it.next();
			JSONObject j = new JSONObject();
			j.put("id", o[0]);
			j.put("name", o[1]);
			js.add(j);
		}

		returnToClient(js.toString());
	}

}