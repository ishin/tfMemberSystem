package com.sealtalk.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.sealtalk.utils.Dom4jXmlUtils;
import com.sealtalk.utils.LogUtils;

public class XmlCommon {
    private Map<String, String> config = null;
    private static final Logger logger = LogManager.getLogger(XmlCommon.class);
    private static final String DYNAMICDATA = "data.xml";
    
    public void load(String file) {  
        try {  
        	config = new HashMap<String, String>();
        	if (file == null) file = DYNAMICDATA;
            Document doc = Dom4jXmlUtils.getInstance().load(XmlCommon.class.getResource("/").getPath() + file);  
            
            Element root = doc.getRootElement();
            @SuppressWarnings("unchecked")
			Iterator<Element> it = root.elementIterator();
            
            while(it.hasNext()) {
            	Element e = (Element)it.next();
            	config.put(e.getName().toLowerCase(), e.getStringValue());
            }
            
        } catch(Exception e) {  
        	logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
            e.printStackTrace();  
        }  
    }  
    
    public String getByKey(String key, String file) {
    	if (config == null) {
    		this.load(file);
    	}
    	
    	return config.get(key);
    }
    
    public String getByKey(String key) {
    	if (config == null) {
    		this.load(DYNAMICDATA);
    	}
    	return config.get(key);
    }
}
