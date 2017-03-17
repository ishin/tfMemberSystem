package com.organ.model;

/**
 * TSex entity. @author MyEclipse Persistence Tools
 */

public class TSex implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -112457505763862756L;
	private Integer id;
	private String name;
	private Integer listorder;

	// Constructors

	/** default constructor */
	public TSex() {
	}

	/** minimal constructor */
	public TSex(Integer listorder) {
		this.listorder = listorder;
	}

	/** full constructor */
	public TSex(String name, Integer listorder) {
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