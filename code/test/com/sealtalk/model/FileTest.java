package com.sealtalk.model;

import com.organ.utils.FileUtil;

public class FileTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "D:\\programs\\apache-tomcat-7.0.73\\webapps\\sealtalk\\upload\\images\\0-1484899870.jpg";
		
		delete(path);
	}
	
	public static void delete(String path) {
		FileUtil.deleteFile(path);
	}

}
