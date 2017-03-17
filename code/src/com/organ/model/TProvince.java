package com.organ.model;

/**
 * TProvince entity. @author MyEclipse Persistence Tools
 */

public class TProvince implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 6236232431519679903L;
	private Integer id;
	private String name;
	private Integer listorder;

	// Constructors

	/** default constructor */
	public TProvince() {
	}

	/** minimal constructor */
	public TProvince(Integer listorder) {
		this.listorder = listorder;
	}

	/** full constructor */
	public TProvince(String name, Integer listorder) {
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