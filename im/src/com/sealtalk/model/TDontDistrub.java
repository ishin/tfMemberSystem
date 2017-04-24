package com.sealtalk.model;

/**
 * 消息免打扰
 * @author hao_dy
 * @date 2017/01/17
 */
public class TDontDistrub {
	private int id;				
	private int groupId;		//组id
	private int memberId;		//成员id
	private String isOpen;		//开关
	private int listOrder;
	
	public TDontDistrub() {}
	
	public TDontDistrub(int id, int groupId, int memberId, String isOpen,
			int listOrder) {
		super();
		this.id = id;
		this.groupId = groupId;
		this.memberId = memberId;
		this.isOpen = isOpen;
		this.listOrder = listOrder;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public int getMemberId() {
		return memberId;
	}
	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}
	public String getIsOpen() {
		return isOpen;
	}
	public void setIsOpen(String isOpen) {
		this.isOpen = isOpen;
	}
	public int getListOrder() {
		return listOrder;
	}
	public void setListOrder(int listOrder) {
		this.listOrder = listOrder;
	}
	
}
