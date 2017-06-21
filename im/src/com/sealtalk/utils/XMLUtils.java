package com.sealtalk.utils;

import com.sealtalk.common.XmlCommon;

public class XMLUtils {
	private XMLUtils(){}
	
	private static class Inner {
		private static final XMLUtils xu = new XMLUtils();
	}
	public static XMLUtils getInstance() {
		return Inner.xu;
	}
	
	public String getDynamicData(String key) {
		XmlCommon xc = new XmlCommon();
		return xc.getByKey(key);
	}
	
	public String getDynamicData(String key, String file) {
		XmlCommon xc = new XmlCommon();
		return xc.getByKey(key, file);
	}
	
	public XmlCommon getXmlCommon(String file) {
		XmlCommon xc = new XmlCommon();
		xc.load(file);
		return xc;
	}
}
