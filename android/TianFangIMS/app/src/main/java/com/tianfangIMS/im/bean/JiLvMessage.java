package com.tianfangIMS.im.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import io.rong.imlib.model.Message;

/**
 * Created by Raink on 2017/4/30.
 */

public class JiLvMessage implements MultiItemEntity {

    private int type;
    private Message message;

    //1.代表图片
    public static final int TYPE_1 = 1;
    //2.视频
    public static final int TYPE_2 = 2;
    //3.文档
    public static  final int TYPE_3 = 3;

    public JiLvMessage(int type, Message message) {
        this.type = type;
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public int getItemType() {
        return type;
    }
}
