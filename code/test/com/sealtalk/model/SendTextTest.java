package com.sealtalk.model;

import com.bcloud.msg.http.HttpSender;
import com.organ.utils.PropertiesUtils;

public class SendTextTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String mobiles = "18612755630";
		String content = "1234567【天坊信息】";
		
		
		String result = sendText(mobiles, content);
		
		System.out.println(result);
		
	} 
	
	public static String sendText(String mobiles, String content) {
		try {
			String uri = PropertiesUtils.getStringByKey("code.uri");
			String account = PropertiesUtils.getStringByKey("code.account");
			String pswd = PropertiesUtils.getStringByKey("code.pswd");
			String product = PropertiesUtils.getStringByKey("code.product");
			String extno = PropertiesUtils.getStringByKey("code.extno");
			
			boolean needstatus = PropertiesUtils.getStringByKey("code.needstatus").equals("1") ? true : false;
			
			String returnString = HttpSender.batchSend(uri, account, pswd, mobiles, content, needstatus, product, extno);
			
			return returnString;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
