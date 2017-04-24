package com.sealtalk.utils;

import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.sealtalk.common.ResourceLoader;

/**
 * 获取sealtalk配置文件属性
 * @author hao_dy
 *
 */
public class PropertiesUtils {
	private static ResourceLoader loader = ResourceLoader.getInstance();  
    private static ConcurrentMap<String, String> configMap = new ConcurrentHashMap<String, String>();  
    private static final String DEFAULT_CONFIG_FILE = "sealtalk.properties";  
  
    private static Properties prop = null;  
  
    public static String getStringByKey(String key, String propName) {  
        try {  
            prop = loader.getPropFromProperties(propName);  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
        key = key.trim();  
        if (!configMap.containsKey(key)) {  
            if (prop.getProperty(key) != null) {  
                configMap.put(key, prop.getProperty(key));  
            }  
        }  
        return configMap.get(key);  
    }  
  
    public static String getStringByKey(String key) {  
        return getStringByKey(key, DEFAULT_CONFIG_FILE);  
    }  
  
    public static ArrayList<String> getListByKey(String key) {
    	ArrayList<String> list = new ArrayList<String>();
    	String dev = getStringByKey(key);
    	
    	dev = (dev == null) ? "" : dev;
    	
    	String[] devs = dev.split(",");
    	
    	for(int i = 0; i < devs.length; i++) {
    		list.add(devs[i]);
    	}
    	
    	return list;
    }
    
    public static Properties getProperties() {  
        try {  
            return loader.getPropFromProperties(DEFAULT_CONFIG_FILE);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
    
    public static boolean devsContains(String dev) {
    	ArrayList<String> devs = getListByKey("cfg.dev");
    	return devs.contains(dev);
    }

    public static String getDomain() {
		String domain = getStringByKey("cfg.domain");
		
		if (domain.endsWith("/")) {
			domain = domain.substring(0, domain.length() - 1);
		}
		
		return domain;
    }
    
    public static String getUploadDir() {
    	String uploadDir = getStringByKey("cfg.uploaddir");
		
    	if (uploadDir.startsWith("/")) {
			uploadDir = "/" + uploadDir;
		}
		
		if (uploadDir.endsWith("/")) {
			uploadDir += "/";
		}
		
		return uploadDir;
		
    }
    
    public static String getDefaultLogo() {
    	return getStringByKey("cfg.defaultlogo");
    }
}
