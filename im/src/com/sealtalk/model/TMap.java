package com.sealtalk.model;

/**
 * map location
 * @author hao_dy
 *
 */
public class TMap implements java.io.Serializable {

	private static final long serialVersionUID = -8722162848002300100L;

	private int id;
	private int userId;
	private String latitude;
	private String longitude;
	private long subDate;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public long getSubDate() {
		return subDate;
	}
	public void setSubDate(long subDate) {
		this.subDate = subDate;
	}
}