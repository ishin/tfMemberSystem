package com.sealtalk.model;

/**
 * TFunction entity. @author MyEclipse Persistence Tools
 */

public class TFunction implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 538599882463307971L;
	private Integer id;
	private String name;
	private String isOpen;
	private Integer listorder;

	// Constructors

	/** default constructor */
	public TFunction() {
	}

	/** minimal constructor */
	public TFunction(String isOpen, Integer listorder) {
		this.isOpen = isOpen;
		this.listorder = listorder;
	}

	/** full constructor */
	public TFunction(String name, String isOpen, Integer listorder) {
		this.name = name;
		this.isOpen = isOpen;
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

	public String getIsOpen() {
		return this.isOpen;
	}

	public void setIsOpen(String isOpen) {
		this.isOpen = isOpen;
	}

	public Integer getListorder() {
		return this.listorder;
	}

	public void setListorder(Integer listorder) {
		this.listorder = listorder;
	}

}