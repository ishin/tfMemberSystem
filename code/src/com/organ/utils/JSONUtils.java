package com.organ.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @Package com.vincent.fishing.utils
 * @Title JSONOperation.java
 * @ClassName JSONOperation
 * @Description json处理类
 * @author hao_dy
 * @date September the 29th 2016
 * @version V1.0
 * */
public class JSONUtils {
	
	private JSONUtils(){}
	
	private static class Inner{
		private static final JSONUtils JSONOPERATION = new JSONUtils();
	}
	
	public static final JSONUtils getInstance() {
		return Inner.JSONOPERATION;
	}
	
	//java对象转json字符串，注意，java对象必须是public属性的
	public String objToString(Object obj) {
		JSONObject jo = JSONObject.fromObject(obj);
		return jo.toString();
	}
	
	public JSONObject modelToJSONObj(Object obj) {
		return JSONObject.fromObject(obj);
	}
	/**
	 * @Description json字符串转json对象数组
	 * 注意格式必须是{}
	 * */
	public JSONObject stringToObj(String jsonStr) {
		JSONObject ja = JSONObject.fromObject(jsonStr);
		
		return ja;
	}
	
	/**
	 * @Description json字符串转json对象数组
	 * 注意格式必须是[{},{}]
	 * */
	public JSONArray stringToArrObj(String jsonStr) {
		JSONArray ja = JSONArray.fromObject(jsonStr);
		return ja;
	}
	
	/**
	 * @Description 读json文件
	 * */
	public String readJsonFile(String path) {
		File file = new File(path);
        Scanner scanner = null;
        StringBuilder buffer = new StringBuilder();
       
        try {
            scanner = new Scanner(file, "utf-8");
            
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine());
            }
            	
            return buffer.toString();
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        
        return null;
	}
	
}

