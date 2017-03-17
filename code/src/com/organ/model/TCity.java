package com.organ.model;

/**
 * TCity entity. @author MyEclipse Persistence Tools
 */

public class TCity implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 2556325298622372854L;
	private Integer id;
	private Integer provinceId;
	private String name;
	private Integer listorder;

	// Constructors

	/** default constructor */
	public TCity() {
	}

	/** minimal constructor */
	public TCity(Integer provinceId, Integer listorder) {
		this.provinceId = provinceId;
		this.listorder = listorder;
	}

	/** full constructor */
	public TCity(Integer provinceId, String name, Integer listorder) {
		this.provinceId = provinceId;
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

	public Integer getProvinceId() {
		return this.provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
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