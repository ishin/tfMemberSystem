package com.organ.model;

/**
 * TPosition entity. @author MyEclipse Persistence Tools
 */

public class TPosition implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -1078788726634141460L;
	private Integer id;
	private Integer organId;
	private String name;
	private Integer listorder;

	// Constructors

	/** default constructor */
	public TPosition() {
	}

	/** minimal constructor */
	public TPosition(Integer organId, Integer listorder) {
		this.organId = organId;
		this.listorder = listorder;
	}

	/** full constructor */
	public TPosition(Integer organId, String name, Integer listorder) {
		this.organId = organId;
		this.name = name;
		this.listorder = listorder;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOrganId() {
		return this.organId;
	}

	public void setOrganId(Integer organId) {
		this.organId = organId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getListorder() {
		return this.listorder;
	}

	public void setListorder(Integer listorder) {
		this.listorder = listorder;
	}

}