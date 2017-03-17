package com.organ.model;

/**
 * TBranchMember entity. @author MyEclipse Persistence Tools
 */

public class TBranchMember implements java.io.Serializable {

	private static final long serialVersionUID = -3725723239917391498L;

	private Integer id;
	private Integer branchId;
	private Integer memberId;
	private Integer positionId;
	private String isMaster;
	private Integer listorder;

	// Constructors

	/** default constructor */
	public TBranchMember() {
	}

	/** minimal constructor */
	public TBranchMember(Integer branchId, Integer memberId,
			Integer positionId, Integer listorder) {
		this.branchId = branchId;
		this.memberId = memberId;
		this.positionId = positionId;
		this.listorder = listorder;
	}

	/** full constructor */
	public TBranchMember(Integer branchId, Integer memberId,
			Integer positionId, String isMaster, Integer listorder) {
		this.branchId = branchId;
		this.memberId = memberId;
		this.positionId = positionId;
		this.isMaster = isMaster;
		this.listorder = listorder;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBranchId() {
		return this.branchId;
	}

	public void setBranchId(Integer branchId) {
		this.branchId = branchId;
	}

	public Integer getMemberId() {
		return this.memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getPositionId() {
		return this.positionId;
	}

	public void setPositionId(Integer positionId) {
		this.positionId = positionId;
	}

	public String getIsMaster() {
		return this.isMaster;
	}

	public void setIsMaster(String isMaster) {
		this.isMaster = isMaster;
	}

	public Integer getListorder() {
		return this.listorder;
	}

	public void setListorder(Integer listorder) {
		this.listorder = listorder;
	}

}