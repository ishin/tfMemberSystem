package com.tianfangIMS.im.bean;

import java.io.Serializable;

/**
 * Created by LianMengYu on 2017/2/6.
 */

public class SetSyncUserBean implements Serializable{
    private String code;
    private Text text;

    public SetSyncUserBean(String code, Text text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public SetSyncUserBean.Text getText() {
        return text;
    }

    public void setText(SetSyncUserBean.Text text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "SetSyncUserBean{" +
                "code='" + code + '\'' +
                ", text=" + text +
                '}';
    }

    private static class Text implements Serializable{
        private String context;
        private String code;

        public Text(String code, String context) {
            this.code = code;
            this.context = context;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
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
            return "Text{" +
                    "code='" + code + '\'' +
                    ", context='" + context + '\'' +
                    '}';
        }
    }

}
