package com.tianfangIMS.im.bean;

import java.io.Serializable;
import java.net.URI;

/**
 * Created by LianMengYu on 2017/3/6.
 */

public class SelectPhotoBean implements Serializable{
    private URI uri;
    private boolean isChecked;


    public SelectPhotoBean(URI uri, boolean isChecked) {
        this.uri = uri;
        this.isChecked = isChecked;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "SelectPhotoBean{" +
                "uri=" + uri +
                ", isChecked=" + isChecked +
                '}';
    }
}