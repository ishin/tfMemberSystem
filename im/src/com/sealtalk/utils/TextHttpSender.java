package com.sealtalk.utils;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import com.bcloud.msg.http.HttpSender;
import com.sealtalk.common.Tips;

/**
 * 短信验证码发送
 * @author hao_dy
 *
 */
public class TextHttpSender {
	
	public static Map<String, String> code = new HashMap<String, String>();
	
	/*private TextHttpSender() {
		code.put("0", "提交成功");
		code.put("101", "无此用户");
		code.put("102", "密码错");
		code.put("103", "提交过快");
		code.put("104", "系统忙");
		code.put("105", "敏感短信");
		code.put("106", "消息长度错");
		code.put("107", "包含错误的手机号码");
		code.put("108", "手机号码个数错");
		code.put("109", "无发送额度");
		code.put("110", "不在发送时间内");
		code.put("111", "超出该账户当月发送额度限制");
		code.put("112", "无此产品，用户没有订购该产品");
		code.put("113", "extno格式错（非数字或者长度不对）");
		code.put("115", "自动审核驳回");
		code.put("116", "签名不合法，未带签名");
		code.put("117", "IP地址认证错,请求调用的IP地址不是系统登记的IP地址");
		code.put("118", "用户没有相应的发送权限");
		code.put("119", "用户已过期");
	};*/
	
	private static class Inner {
		private static final TextHttpSender THS = new TextHttpSender();
	}
	
	public static TextHttpSender getInstance() {
		return Inner.THS;
	}
	
	/**
	 * 群发短信
	 * @param uri应用地址
	 * @param account账号
	 * @param pswd密码
	 * @param mobiles手机号码，多个号码使用","分割
	 * @param content短信内容
	 * @param needstatus是否需要状态报告，需要true，不需要false
	 * @param product产品ID
	 * @param extrno扩展码
	 * @return
	 */
	public String sendText(String mobiles, String content) {
		String resultCode = null;
		
		try {
			String uri = PropertiesUtils.getStringByKey("code.uri");
			String account = PropertiesUtils.getStringByKey("code.account");
			String pswd = PropertiesUtils.getStringByKey("code.pswd");
			String product = PropertiesUtils.getStringByKey("code.product");
			String extno = PropertiesUtils.getStringByKey("code.extno");
			
			boolean needstatus = PropertiesUtils.getStringByKey("code.needstatus").equals("1") ? true : false;
			boolean status = false;
			
			String returnString = HttpSender.batchSend(uri, account, pswd, mobiles, content, needstatus, product, extno);
			
			if (returnString != null) {
				String[] result = returnString.split("\\n");
				
				if (result.length > 0 && !StringUtils.getInstance().isBlank(result[0])) {
					String[] codeR =result[0].split(",");
					
					if (codeR.length > 1 && !StringUtils.getInstance().isBlank(result[1])) {
						resultCode = codeR[1];
						status = true;
					}
				}
			}
			
			if (!status) {
				resultCode = "-1";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultCode;
	}
	
}
