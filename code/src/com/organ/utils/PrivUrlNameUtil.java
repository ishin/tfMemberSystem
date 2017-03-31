package com.organ.utils;

/**
 * 
 * @author Lmy 生成权限Url名称工具类，主要命名规则为：汉子首字母（英文直接使用）+时间戳后3位
 */
public class PrivUrlNameUtil {

	public static String initUrlName(String name) {
		String str = PinyinGenerator.getPinYinHeadChar(name)
				+ (System.currentTimeMillis() + "").substring((System
						.currentTimeMillis() + "").length() - 3, (System
						.currentTimeMillis() + "").length());
		return str;
	}

}
