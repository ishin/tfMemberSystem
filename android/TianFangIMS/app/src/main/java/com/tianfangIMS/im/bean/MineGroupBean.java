package com.tianfangIMS.im.bean;

import java.util.List;

/**
 * Created by Titan on 2017/2/9.
 */

public class MineGroupBean {

    private int code;
    private TextBean text;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public TextBean getText() {
        return text;
    }

    public void setText(TextBean text) {
        this.text = text;
    }

    public static class TextBean {
        private List<GroupBean> ICreate;
        private List<GroupBean> IJoin;

        public List<GroupBean> getICreate() {
            return ICreate;
        }

        public void setICreate(List<GroupBean> ICreate) {
            this.ICreate = ICreate;
        }

        public List<GroupBean> getIJoin() {
            return IJoin;
        }

        public void setIJoin(List<GroupBean> IJoin) {
            this.IJoin = IJoin;
        }


    }
}
