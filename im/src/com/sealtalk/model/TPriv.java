package com.sealtalk.model;

/**
 * TPriv entity. @author MyEclipse Persistence Tools
 */

public class TPriv implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 8778037318918502533L;
	private Integer id;
	private Integer parentId;
	private String name;
	private String category;
	private String grouping;
	private String url;
	private Integer listorder;

	// Constructors

	/** default constructor */
	public TPriv() {
	}

	/** minimal constructor */
	public TPriv(Integer parentId, Integer listorder) {
		this.parentId = parentId;
		this.listorder = listorder;
	}

	/** full constructor */
	public TPriv(Integer parentId, String name, String category,
			String grouping, String url, Integer listorder) {
		this.parentId = parentId;
		this.name = name;
		this.category = category;
		this.grouping = grouping;
		this.url = url;
		this.listorder = listorder;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getParentId() {
		return this.parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getGrouping() {
		return this.grouping;
	}

	public void setGrouping(String grouping) {
		this.grouping = grouping;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getListorder() {
		return this.listorder;
	}

	public void setListorder(Integer listorder) {
		this.listorder = listorder;
	}

}