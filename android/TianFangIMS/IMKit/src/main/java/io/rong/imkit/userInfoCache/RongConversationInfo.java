package io.rong.imkit.userInfoCache;

import android.net.Uri;

public class RongConversationInfo {
    public RongConversationInfo (String type, String id, String name, Uri uri) {
        this.conversationType = type;
        this.id = id;
        this.name = name;
        this.uri = uri;
    }

    public String getConversationType() {
        return conversationType;
    }

    public void setConversationType(String conversationType) {
        this.conversationType = conversationType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String conversationType;
    private String id;
    private String name;
    private Uri uri;
}
