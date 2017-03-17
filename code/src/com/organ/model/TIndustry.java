package com.organ.model;

/**
 * TIndustry entity. @author MyEclipse Persistence Tools
 */

public class TIndustry implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 5501633397086427136L;
	private Integer id;
	private String name;
	private Integer listorder;

	// Constructors

	/** default constructor */
	public TIndustry() {
	}

	/** minimal constructor */
	public TIndustry(Integer listorder) {
		this.listorder = listorder;
	}

	/** full constructor */
	public TIndustry(String name, Integer listorder) {
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