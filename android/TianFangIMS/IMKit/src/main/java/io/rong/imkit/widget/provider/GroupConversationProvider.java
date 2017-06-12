package io.rong.imkit.widget.provider;

import android.net.Uri;

import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.userInfoCache.RongUserInfoManager;

@ConversationProviderTag(conversationType = "group", portraitPosition = 1)
public class GroupConversationProvider extends PrivateConversationProvider implements IContainerItemProvider.ConversationProvider<UIConversation> {
    @Override
    public String getTitle(String groupId) {
        String name;
        if (RongUserInfoManager.getInstance().getGroupInfo(groupId) == null) {
            name = "";
        } else {
            name = RongUserInfoManager.getInstance().getGroupInfo(groupId).getName();
        }
        return name;
    }

    @Override
    public Uri getPortraitUri(String id) {
        Uri uri;
        if (RongUserInfoManager.getInstance().getGroupInfo(id) == null) {
            uri = null;
        } else {
            uri = RongUserInfoManager.getInstance().getGroupInfo(id).getPortraitUri();
        }
        return uri;
    }


}
