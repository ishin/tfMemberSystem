package com.sealtalk.model;

/**
 * TGroup entity. @author MyEclipse Persistence Tools
 */

public class TGroup implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 8696859135232432407L;
	private Integer id;
	private String code;
	private String name;
	private String createdate;
	private Integer creatorId;
	private Integer volume;
	private Integer volumeuse;
	private Integer space;
	private Integer spaceuse;
	private Integer annexlong;
	private String notice;
	private Integer listorder;

	// Constructors

	/** default constructor */
	public TGroup() {
	}

	/** minimal constructor */
	public TGroup(Integer creatorId, Integer volume, Integer volumeuse,
			Integer space, Integer spaceuse, Integer annexlong,
			Integer listorder) {
		this.creatorId = creatorId;
		this.volume = volume;
		this.volumeuse = volumeuse;
		this.space = space;
		this.spaceuse = spaceuse;
		this.annexlong = annexlong;
		this.listorder = listorder;
	}

	/** full constructor */
	public TGroup(String code, String name, String createdate,
			Integer creatorId, Integer volume, Integer volumeuse,
			Integer space, Integer spaceuse, Integer annexlong, String notice,
			Integer listorder) {
		this.code = code;
		this.name = name;
		this.createdate = createdate;
		this.creatorId = creatorId;
		this.volume = volume;
		this.volumeuse = volumeuse;
		this.space = space;
		this.spaceuse = spaceuse;
		this.annexlong = annexlong;
		this.notice = notice;
		this.listorder = listorder;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreatedate() {
		return this.createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public Integer getCreatorId() {
		return this.creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}

	public Integer getVolume() {
		return this.volume;
	}

	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	public Integer getVolumeuse() {
		return this.volumeuse;
	}

	public void setVolumeuse(Integer volumeuse) {
		this.volumeuse = volumeuse;
	}

	public Integer getSpace() {
		return this.space;
	}

	public void setSpace(Integer space) {
		this.space = space;
	}

	public Integer getSpaceuse() {
		return this.spaceuse;
	}

	public void setSpaceuse(Integer spaceuse) {
		this.spaceuse = spaceuse;
	}

	public Integer getAnnexlong() {
		return this.annexlong;
	}

	public void setAnnexlong(Integer annexlong) {
		this.annexlong = annexlong;
	}

	public String getNotice() {
		return this.notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public Integer getListorder() {
		return this.listorder;
	}

	public void setListorder(Integer listorder) {
		this.listorder = listorder;
	}

}