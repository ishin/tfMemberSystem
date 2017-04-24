package com.organ.action.adm;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.googlecode.sslplugin.annotation.Secured;
import com.organ.model.TMember;
import com.organ.model.TOrgan;
import com.organ.utils.PinyinGenerator;

@Secured
@MultipartConfig
public class LogoServlet extends HttpServlet {

	/**
	 * 文件上传
	 * by alopex
	 * 2017.1.27
	 */
	private static final long serialVersionUID = 4368072561206144721L;
	XmlWebApplicationContext context = null;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		Part p = req.getPart("logofile");
		String name = getFileName(p);
		name = PinyinGenerator.getPinYin(name);
		String path = getServletContext().getRealPath("images") + "/";
		
		p.write(path + name);
		
		Object o = req.getSession().getAttribute("member");
		if (null != o) {
			TMember member = (TMember)o;
			Integer orgId = member.getOrganId();
			SessionFactory factory = this.getSessionFactory(req);
			Session session = factory.openSession();
			Transaction t = session.beginTransaction();
			TOrgan org = (TOrgan) session.get(TOrgan.class, orgId);
			org.setLogo(name);
			session.save(org);
			t.commit();
			session.close();
		}
		
		res.setContentType("text/plain;charset=utf-8");
		PrintWriter out = res.getWriter();
		
		out.println(name);
	}

	private String getFileName(Part part) {

		for (String content : part.getHeader("content-disposition").split(";")) {
	        if (content.trim().startsWith("filename")) {
	            return content.substring(
	                    content.indexOf('=') + 1).trim().replace("\"", "");
	        }
	    }
	    return null;
	}
	
	private SessionFactory getSessionFactory(HttpServletRequest req) {
		
		this.context = new XmlWebApplicationContext();
		this.context.setConfigLocation("/WEB-INF/classes/spring.xml");
		this.context.setServletContext(req.getSession().getServletContext());
		this.context.refresh();
		
		return (SessionFactory)this.context.getBean("sessionFactory");
	}
}