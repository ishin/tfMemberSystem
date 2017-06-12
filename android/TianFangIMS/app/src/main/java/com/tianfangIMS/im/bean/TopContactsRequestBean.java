package com.tianfangIMS.im.bean;

import java.io.Serializable;

/**
 * Created by LianMengYu on 2017/2/6.
 * 发起群聊Bean
 */

public class TopContactsRequestBean implements Serializable {

    private String code;
    private Text text;

    public static class Text implements Serializable {
        private String annexlong;
        private String code;
        private String createdate;
        private String creatorId;
        private String id;
        private String listorder;
        private String name;
        private String notice;
        private String space;
        private String spaceuse;
        private String volume;
        private String volumeuse;

        public Text(String annexlong, String code, String createdate, String creatorId, String id, String listorder, String name, String notice, String space, String spaceuse, String volume, String volumeuse) {
            this.annexlong = annexlong;
            this.code = code;
            this.createdate = createdate;
            this.creatorId = creatorId;
            this.id = id;
            this.listorder = listorder;
            this.name = name;
            this.notice = notice;
            this.space = space;
            this.spaceuse = spaceuse;
            this.volume = volume;
            this.volumeuse = volumeuse;
        }

        public String getAnnexlong() {
            return annexlong;
        }

        public void setAnnexlong(String annexlong) {
            this.annexlong = annexlong;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCreatedate() {
            return createdate;
        }

        public void setCreatedate(String createdate) {
            this.createdate = createdate;
        }

        public String getCreatorId() {
            return creatorId;
        }

        public void setCreatorId(String creatorId) {
            this.creatorId = creatorId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getListorder() {
            return listorder;
        }

        public void setListorder(String listorder) {
            this.listorder = listorder;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNotice() {
            return notice;
        }

        public void setNotice(String notice) {
            this.notice = notice;
        }

        public String getSpace() {
            return space;
        }

        public void setSpace(String space) {
            this.space = space;
        }

        public String getSpaceuse() {
            return spaceuse;
        }

        public void setSpaceuse(String spaceuse) {
            this.spaceuse = spaceuse;
        }

        public String getVolume() {
            return volume;
        }

        public void setVolume(String volume) {
            this.volume = volume;
        }

        public String getVolumeuse() {
            return volumeuse;
        }

        public void setVolumeuse(String volumeuse) {
            this.volumeuse = volumeuse;
        }

        @Override
        public String toString() {
            return "Text{" +
                    "annexlong='" + annexlong + '\'' +
                    ", code='" + code + '\'' +
                    ", createdate='" + createdate + '\'' +
                    ", creatorId='" + creatorId + '\'' +
                    ", id='" + id + '\'' +
                    ", listorder='" + listorder + '\'' +
                    ", name='" + name + '\'' +
                    ", notice='" + notice + '\'' +
                    ", space='" + space + '\'' +
                    ", spaceuse='" + spaceuse + '\'' +
                    ", volume='" + volume + '\'' +
                    ", volumeuse='" + volumeuse + '\'' +
                    '}';
        }
    }

    public TopContactsRequestBean(String code, Text text) {
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
        return "TopContactsRequestBean{" +
                "text=" + text +
                ", code='" + code + '\'' +
                '}';
    }
}
