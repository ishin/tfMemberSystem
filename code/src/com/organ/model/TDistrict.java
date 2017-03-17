package com.organ.model;

/**
 * TDistrict entity. @author MyEclipse Persistence Tools
 */

public class TDistrict implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = -6649921081297771421L;
	
	private Integer id;
	private Integer cityId;
	private String name;
	private Integer listorder;

	// Constructors

	/** default constructor */
	public TDistrict() {
	}

	/** minimal constructor */
	public TDistrict(Integer cityId, Integer listorder) {
		this.cityId = cityId;
		this.listorder = listorder;
	}

	/** full constructor */
	public TDistrict(Integer cityId, String name, Integer listorder) {
		this.cityId = cityId;
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

	public Integer getCityId() {
		return this.cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
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