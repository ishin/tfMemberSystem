package com.organ.action.adm;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.hibernate.SessionFactory;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.googlecode.sslplugin.annotation.Secured;
import com.organ.common.Constants;
import com.organ.service.adm.ImpService;

import net.sf.json.JSONObject;

@MultipartConfig

public class ImpServlet extends HttpServlet {

	/**
	 * 文件上传
	 * by alopex
	 * 2017.2.2
	 */
	private static final long serialVersionUID = 4368072561206144721L;
	XmlWebApplicationContext context = null;
	
	private ImpService impService = null;
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res) 
			throws ServletException, IOException {

		JSONObject js = new JSONObject();

		this.impService = new ImpService(this.getSessionFactory(req));

		Part part = req.getPart("impfile");
		String contentType = part.getContentType();
		
		if (!contentType.equals(Constants.XLS) && !contentType.equals(Constants.XLSX)) {
			js.put("status", 1);//文件类型错
		}
		else {
			if (contentType.equals(Constants.XLS)) {
				js = impService.handleXls(part);
			}
			else {
				js = impService.handleXlsx(part);
			}
		}

		this.context.close();
		
		res.setContentType("application/json;charset=utf-8");
		PrintWriter out = res.getWriter();
		
		out.println(js.toString());
	}
	
	private SessionFactory getSessionFactory(HttpServletRequest req) {
		
		this.context = new XmlWebApplicationContext();
		this.context.setConfigLocation("/WEB-INF/classes/spring.xml");
		this.context.setServletContext(req.getSession().getServletContext());
		this.context.refresh();
		
		return (SessionFactory)this.context.getBean("sessionFactory");
	}
	
}