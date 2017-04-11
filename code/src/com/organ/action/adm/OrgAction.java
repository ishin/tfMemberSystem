package com.organ.action.adm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

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
		
		Integer provinceId = Integer.parseInt(this.request.getParameter("provinceid"));
		List list = orgService.getCity(provinceId);
		
		handle(list);
		
		return "text";
	}
	
	public String getDistrict() {
		
		Integer cityId = Integer.parseInt(this.request.getParameter("cityid"));
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
		
		Integer industryId = Integer.parseInt(this.request.getParameter("industryid"));
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

	public String save() {
	
		Integer organId = this.getOrganId();

		TOrgan organ = new TOrgan();
		organ.setId(organId);
		
		organ.setCode(this.request.getParameter("code"));
		organ.setName(this.request.getParameter("name"));
		organ.setShortname(this.request.getParameter("shortname"));
		organ.setEnglishname(this.request.getParameter("englishname"));
		organ.setAd(this.request.getParameter("ad"));
		//organ.setProvinceId(this.request.getParameter("provinceid") == "" ? 0 : Integer.parseInt(this.request.getParameter("provinceid")));
		organ.setProvinceId(StringUtils.isBlank(this.request.getParameter("provinceid"))?0:Integer.parseInt(this.request.getParameter("provinceid")));
		//cityid
		organ.setCityId(StringUtils.isBlank(this.request.getParameter("cityid"))?0:Integer.parseInt(this.request.getParameter("cityid")));
		//districtid
		System.err.println(this.request.getParameter("districtid"));
		organ.setDistrictId(StringUtils.isBlank(this.request.getParameter("districtid"))?0:Integer.parseInt(this.request.getParameter("districtid")));
		organ.setContact(this.request.getParameter("contact"));
		organ.setAddress(this.request.getParameter("address"));
		organ.setTelephone(this.request.getParameter("telephone"));
		organ.setFax(this.request.getParameter("fax"));
		organ.setEmail(this.request.getParameter("email"));
		organ.setPostcode(this.request.getParameter("postcode"));
		organ.setWebsite(this.request.getParameter("website"));
		//inwardid
		organ.setInwardId(StringUtils.isBlank(this.request.getParameter("inwardid"))?0:Integer.parseInt(this.request.getParameter("inwardid")));
		//industryid
		organ.setIndustryId(StringUtils.isBlank(this.request.getParameter("industryid"))?0:Integer.parseInt(this.request.getParameter("industryid")));
		//subdustryid
		organ.setSubdustryId(StringUtils.isBlank(this.request.getParameter("subdustryid"))?0:Integer.parseInt(this.request.getParameter("subdustryid")));
		//capital
		organ.setCapital(StringUtils.isBlank(this.request.getParameter("capital"))?0:Integer.parseInt(this.request.getParameter("capital")));
		//membernumber
		organ.setMembernumber(StringUtils.isBlank(this.request.getParameter("membernumber"))?0:Integer.parseInt(this.request.getParameter("membernumber")));
		//computernumber
		organ.setComputernumber(StringUtils.isBlank(this.request.getParameter("computernumber"))?0:Integer.parseInt(this.request.getParameter("computernumber")));
		organ.setIntro(this.request.getParameter("intro"));
		organ.setLogo(this.request.getParameter("logo"));
		organ.setListorder(Integer.parseInt(this.request.getParameter("listorder")));
		System.err.println("是否走到这一步"+organ);
		orgService.save(organ);
		
		return returnajaxid(organId);
	}
	
	private void handle(List list) {

		ArrayList<JSONObject> js = new ArrayList<JSONObject>();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Object[] o = (Object[])it.next();
			JSONObject j = new JSONObject();
			j.put("id", o[0]);
			j.put("name", o[1]);
			js.add(j);
		}
		
		returnToClient(js.toString());
	}
	
}