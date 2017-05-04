package com.sealtalk.action.upload;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sealtalk.common.BaseAction;
import com.sealtalk.service.upload.UploadService;
import com.sealtalk.utils.LogUtils;

public class UploadAction extends BaseAction {  
 
	private static final long serialVersionUID = 74195611146343183L;
	private static final Logger logger = LogManager.getLogger(UploadAction.class);
	
	/**
	 * 选择头像
	 * @return
	 * @throws ServletException
	 */
	public String secUserLogos() throws ServletException {
		String result = null;
		
		try {
			result = uploadService.saveSelectedPic(clearChar(userid), clearChar(picname));
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 上传头像(裁剪)
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	public String uploadUserLogo() throws IOException, ServletException {  
		String result = null;
		
		try {
			//获取服务器的实际路径  
		    String realPath = request.getSession().getServletContext().getRealPath("/");  
			result = uploadService.cutImage(clearChar(userid), clearChar(x), clearChar(y), clearChar(width), clearChar(height), clearChar(degree), file, realPath);
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		logger.info(result);
		returnToClient(result);
		return "text";
    }  
	
	/**
	 * 上传头像(非裁剪)
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	public String uploadUserLogoNotCut() throws IOException, ServletException {  
		//获取服务器的实际路径  
	    String realPath = request.getSession().getServletContext().getRealPath("/");  
		String result = uploadService.uploadUserLogNotCut(clearChar(userid), file, realPath);
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 删除头像从头像库
	 * @return
	 * @throws ServletException
	 */
	public String delUserLogos() throws ServletException {
		String result = null;
		
		try {
			result = uploadService.delUserLogos(clearChar(userid), clearChar(picname));
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 拉取头像库列表 
	 * @return
	 * @throws ServletException
	 */
	public String getUserLogos() throws ServletException {
		String result = null;
		
		try {
			result = uploadService.getUserLogos(clearChar(userid));
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	

	private UploadService uploadService;
	private String userid;
	private String x;
	private String y;
	private String width;
	private String height;
	private String degree;
	private File file;
	private String picname;
	
	public String getPicname() {
		return picname;
	}

	public void setPicname(String picname) {
		this.picname = picname;
	}

	public UploadService getUploadService() {
		return uploadService;
	}
	public void setUploadService(UploadService uploadService) {
		this.uploadService = uploadService;
	}
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getDegree() {
		return degree;
	}
	public void setDegree(String degree) {
		this.degree = degree;
	}
	
}  
