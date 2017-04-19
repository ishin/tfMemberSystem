package com.tianfangIMS.im.bean;

import java.io.Serializable;

/**
 * Created by LianMengYu on 2017/2/3.
 * 提交添加好友申请后，所返回的数据
 */

public class AddFriendRequestBean implements Serializable {

    private String code;
    private Text text;

    public static class Text implements Serializable {
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

    public AddFriendRequestBean(String code, Text text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "AddFriendRequestBean{" +
                "code='" + code + '\'' +
                ", text=" + text +
                '}';
    }
}
