package com.tianfangIMS.im.bean;

/**
 * Created by LianMengYu on 2017/1/13.
 */

public class SubmitCodeBean {

    private int code;
    private String Messagetext;

    public SubmitCodeBean(int code, String messagetext) {
        this.code = code;
        Messagetext = messagetext;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessagetext() {
        return Messagetext;
    }

    public void setMessagetext(String messagetext) {
        Messagetext = messagetext;
    }

    @Override
    public String toString() {
        return "SubmitCodeBean{" +
                "code=" + code +
                ", Messagetext='" + Messagetext + '\'' +
                '}';
    }
}
