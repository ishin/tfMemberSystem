package com.sealtalk.model;

public class TMsgtop implements java.io.Serializable {

	private static final long serialVersionUID = 5527523490739967899L;
	private Integer id;
	private Integer userId;
	private Integer topId;
	private String msgType;
	private Integer listorder;

	public TMsgtop() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getTopId() {
		return topId;
	}

	public void setTopId(Integer topId) {
		this.topId = topId;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public Integer getListorder() {
		return listorder;
	}

	public void setListorder(Integer listorder) {
		this.listorder = listorder;
	}

}