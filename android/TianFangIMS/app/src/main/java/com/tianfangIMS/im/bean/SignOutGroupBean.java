package com.tianfangIMS.im.bean;

import java.io.Serializable;

/**
 * Created by LianMengYu on 2017/2/13.
 */

public class SignOutGroupBean implements Serializable{
    private String code;
    private Text text;
    private static class Text implements Serializable{
        private String context;
        private String code;
    }
}
