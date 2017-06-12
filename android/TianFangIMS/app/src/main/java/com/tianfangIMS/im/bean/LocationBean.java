package com.tianfangIMS.im.bean;

import java.io.Serializable;

/**
 * Created by LianMengYu on 2017/3/3.
 */

public class LocationBean implements Serializable{
    private String userID;
    private String logo;
    private String latitude;
    private String longtitude;

    public LocationBean(String userID, String logo, String latitude, String longtitude) {
        this.userID = userID;
        this.logo = logo;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    @Override
    public String toString() {
        return "LocationBean{" +
                "userID='" + userID + '\'' +
                ", logo='" + logo + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longtitude='" + longtitude + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        LocationBean lb = (LocationBean)obj;
        return userID.equals(lb.getUserID());
    }
}
