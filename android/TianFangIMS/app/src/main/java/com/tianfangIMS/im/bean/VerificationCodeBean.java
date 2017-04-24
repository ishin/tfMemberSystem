package com.tianfangIMS.im.bean;

/**
 * Created by LianMengYu on 2017/1/13.
 */

public class VerificationCodeBean {
    private int code;
    private String context;

    public VerificationCodeBean(int code, String context) {
        this.code = code;
        this.context = context;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "VerificationCodeBean{" +
                "code=" + code +
                ", context='" + context + '\'' +
                '}';
    }
}
