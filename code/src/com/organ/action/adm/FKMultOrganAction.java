package com.organ.action.adm;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import com.organ.common.BaseAction;
import com.organ.model.TOrgan;
import com.organ.service.adm.OrgService;
import com.organ.utils.StringUtils;

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
	public String registOrgan() throws ServletException {
		String result = null;
		JSONObject jo = new JSONObject();
		
		String[] mustParams = {"name", "shortname", "provinceid", "cityid", "districtid", "contact", "address"};
		
		result = this.valideParams(this.request, mustParams);
		
		if (result == null) {
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
		
		jo.put("code", 1);
		jo.put("text", result);
		returnToClient(jo.toString());
		return "text";
	}
	
	private OrgService orgService;

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}
	
}
