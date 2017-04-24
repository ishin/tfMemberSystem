package com.sealtalk.model;

/**
 * TGroupMember entity. @author MyEclipse Persistence Tools
 */

public class TGroupMember implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 3016790088915444840L;
	private Integer id;
	private Integer groupId;
	private Integer memberId;
	private String isCreator;
	private Integer listorder;

	// Constructors

	/** default constructor */
	public TGroupMember() {
	}

	/** minimal constructor */
	public TGroupMember(Integer groupId, Integer memberId, Integer listorder) {
		this.groupId = groupId;
		this.memberId = memberId;
		this.listorder = listorder;
	}

	/** full constructor */
	public TGroupMember(Integer groupId, Integer memberId, String isCreator,
			Integer listorder) {
		this.groupId = groupId;
		this.memberId = memberId;
		this.isCreator = isCreator;
		this.listorder = listorder;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getGroupId() {
		return this.groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getMemberId() {
		return this.memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public String getIsCreator() {
		return this.isCreator;
	}

	public void setIsCreator(String isCreator) {
		this.isCreator = isCreator;
	}

	public Integer getListorder() {
		return this.listorder;
	}

	public void setListorder(Integer listorder) {
		this.listorder = listorder;
	}

}