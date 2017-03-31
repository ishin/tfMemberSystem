package com.organ.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import com.opensymphony.xwork2.ActionSupport;
import com.organ.model.SessionUser;
import com.organ.model.TMember;
import com.organ.utils.StringUtils;

public class BaseAction extends ActionSupport implements ServletRequestAware, ServletResponseAware
{
	private static final long serialVersionUID = 1L;
	
	public BaseAction(){}
	public InputStream inputStream;
	public HttpServletRequest request;
	public HttpServletResponse response;
	
	public void setServletResponse(HttpServletResponse response)
	{
		this.response = response;
	}
	public void setInputStream(InputStream inputStream)
	{
		this.inputStream = inputStream;
	}
	public InputStream getInputStream()
	{
		return inputStream;
	}
	public void setServletRequest(HttpServletRequest request)
	{
		this.request = request;
	}
	
	/**
	 * 获取url参数。android请求用url方式传参
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map getRequestParams() {
		Map<String, String[]> map = request.getParameterMap();
	
		for(Map.Entry<String, String[]> m: map.entrySet()) {
			System.out.println("key: " + m.getKey());
			
			String[] s = (String[])m.getValue();
			
			for(int i = 0; i < m.getValue().length; i++) {
				System.out.println(m.getValue()[i]);
			}
		}
		return map;
	}
	
	/**
	 * 参数长度
	 * @return
	 */
	public int getRequestParamsLength() {
		return this.getRequestParams().size();
	}
	
	/**
	 * 取指定的参数
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String[] getRequestParamsValue(String key) {
		Map<String, String[]> map = request.getParameterMap();
		String[] values = (String[])map.get(key);
		
		return values;
	}
	
	
	/** 返回客户端JSON格式数据 */
	public void returnToClient(String jsonString)
	{
		if (StringUtils.getInstance().isBlank(jsonString)) {
			jsonString = "{}";
		}
		try {
			response.addHeader("pragma", "NO-cache");
			response.addHeader("Cache-Control", "no-cache");
			response.addDateHeader("Expries", 0);
			//跨域
			response.setHeader("AccessControlAllowOrigin", "*"); 
			response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE"); 
			response.setHeader("Access-Control-Max-Age", "3600"); //设置过期时间 
			response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, client_id, uuid, Authorization"); 
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // 支持HTTP 1.1. 
			response.setHeader("Pragma", "no-cache"); // 支持HTTP 1.0. response.setHeader("Expires", "0"); 
			setInputStream(new ByteArrayInputStream(jsonString.getBytes("utf-8")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	// AJAX输出，返回null
	public String ajax(String content, String type)
	{
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType(type + ";charset=UTF-8");
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.getWriter().write(content);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	// 根据Map输出JSON，返回null
	public String ajaxJson(Map<String, String> jsonMap)
	{
		JSONObject jsonObject = JSONObject.fromObject(jsonMap);
		return ajax(jsonObject.toString(), "text/html");
	}
	
	
	
	/** 返回客户端XML格式数据 */
	public void returnXMLToClient(String xml)
	{
		try {
			response.addHeader("pragma", "NO-cache");
			response.addHeader("Cache-Control", "no-cache");
			response.addDateHeader("Expries", 0);
			response.setContentType("text/xml;charset=utf-8");
			
			setInputStream(new ByteArrayInputStream(xml.getBytes("utf-8")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 获取项目地址，不包含参数 */ 
	protected String getUrl() {
		String path = request.getContextPath();  
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
		return basePath;
	}
	
	/** 设置当前会话的用户包装类 */
	protected void setSessionUser(SessionUser su)
	{
		if (request == null) {
			WebContext ctx = WebContextFactory.get();
			HttpSession session = ctx.getSession(false);
			session.setAttribute(Constants.ATTRIBUTE_NAME_OF_SESSIONUSER, su);
		} else {
			request.getSession().setAttribute(Constants.ATTRIBUTE_NAME_OF_SESSIONUSER, su);
		}
	}

	/** 获取当前会话的用户包装类 */
	protected SessionUser getSessionUser()
	{
		if (request == null) {
			WebContext ctx = WebContextFactory.get();
			HttpSession session = ctx.getSession(false);
			return (SessionUser) session.getAttribute(Constants.ATTRIBUTE_NAME_OF_SESSIONUSER);
		} else {
			return (SessionUser) request.getSession().getAttribute(Constants.ATTRIBUTE_NAME_OF_SESSIONUSER);
		}
	}

	protected void setSessionAttribute(String key, Object o)
	{
		if (request == null)
		{
			WebContext ctx = WebContextFactory.get();
			HttpSession session = ctx.getSession(false);
			session.setAttribute(key, o);
		} else
		{
			request.getSession().setAttribute(key, o);
		}
	}
	
	
	protected Object getSessionAttribute(String key)
	{
		if (request == null)
		{
			WebContext ctx = WebContextFactory.get();
			HttpSession session = ctx.getSession(false);
			return session.getAttribute(key);
		} else
		{
			return request.getSession().getAttribute(key);
		}
	}
	
	//获取HTTP请求的输入流,适用于http客户端后台请求数据
	protected String getRequestDataByStream() throws IOException {
        //已HTTP请求输入流建立一个BufferedReader对象
        BufferedReader br =  request.getReader();
        String buffer = null;
        StringBuffer buff = new StringBuffer();
        
        while ((buffer = br.readLine()) != null) {
              buff.append(buffer+"\n");
        }
        
        br.close();
        
       return buff.toString().trim();
	}
	
/*	
	protected String getApplicaitonQueryFilter()
	{
		
		String condition = "";
		SessionUser su = getSessionUser();
		if (su == null || su.isSuperAdmin())
			return condition;
		
		List<String> applicaitonP = su.getApplicationIds();
		if (applicaitonP == null || applicaitonP.isEmpty())
			return condition;
		return "('" + StringUtils.collectionToDelimitedString(applicaitonP, "','") + "')";
	}
	*/
	
	/*protected String getOrganizationQueryFilter()
	{
		
		String condition = "";
		SessionUser su = getSessionUser();
		if (su == null || su.isSuperAdmin())
			return condition;
		
		List<String> organizationP = su.getOrganizationIds();
		if (organizationP == null || organizationP.isEmpty())
			return condition;
		return "('" + StringUtils.collectionToDelimitedString(organizationP, "','") + "')";
	}*/
	
	
	/** 获得当前登录管理员的accountID */
	/*protected String obtainLoginAccountId()
	{
		SessionUser userInfo = getSessionUser();
		return userInfo == null ? null : userInfo.getAccountId();
	}*/
	
	
	/*protected String obtainLoginAccount()
	{
		SessionUser userInfo = getSessionUser();
		return userInfo == null ? null : userInfo.getAccountName();
	}*/
	
	
	protected Integer getOrganId() {
		
		Object o = this.getSessionAttribute("member");

		return o == null ? 0 : ((TMember)o).getOrganId();
	}
	
	protected String returnajaxid(Integer id) {
		
		JSONObject jo = new JSONObject();
		jo.put("id", id);
		returnToClient(jo.toString());
		return "text";
	}
	
	
}