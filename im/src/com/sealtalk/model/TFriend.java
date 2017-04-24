package com.sealtalk.model;

/**
 * TFriend entity. @author MyEclipse Persistence Tools
 */

public class TFriend implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -2866419913239400770L;
	private Integer id;
	private Integer memberId;
	private Integer friendId;
	private String createdate;
	private Integer listorder;

	// Constructors

	/** default constructor */
	public TFriend() {
	}

	/** minimal constructor */
	public TFriend(Integer memberId, Integer friendId, Integer listorder) {
		this.memberId = memberId;
		this.friendId = friendId;
		this.listorder = listorder;
	}

	/** full constructor */
	public TFriend(Integer memberId, Integer friendId, String createdate,
			Integer listorder) {
		this.memberId = memberId;
		this.friendId = friendId;
		this.createdate = createdate;
		this.listorder = listorder;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMemberId() {
		return this.memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getFriendId() {
		return this.friendId;
	}

	public void setFriendId(Integer friendId) {
		this.friendId = friendId;
	}

	public String getCreatedate() {
		return this.createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public Integer getListorder() {
		return this.listorder;
	}

	public void setListorder(Integer listorder) {
		this.listorder = listorder;
	}

}