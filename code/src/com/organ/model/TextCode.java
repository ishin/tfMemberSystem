package com.organ.model;

/** 
* @ClassName: SessionTextCode 
* @Description: TODO(短信验证码) 
* @author hdy
*  
*/
public class TextCode{
	
	private int id;
	private String phoneNum; 	//手机号码
	private String textCode;	//短信验证码
	private long createTime;		//有效时间
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getTextCode() {
		return textCode;
	}
	public void setTextCode(String textCode) {
		this.textCode = textCode;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
}