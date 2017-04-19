package com.tianfangIMS.im.bean;

/**
 * Created by LianMengYu on 2017/1/12.
 */

public class DepartmentBean{



    private String id;
    private String pID;
    private String name;

    public DepartmentBean(String id, String name, String pID) {
        this.id = id;
        this.name = name;
        this.pID = pID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getpID() {
        return pID;
    }

    public void setpID(String pID) {
        this.pID = pID;
    }

    @Override
    public String toString() {
        return "DepartmentBean{" +
                "id='" + id + '\'' +
                ", pID='" + pID + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
