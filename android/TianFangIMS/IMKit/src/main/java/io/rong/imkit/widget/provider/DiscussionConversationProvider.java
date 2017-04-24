package io.rong.imkit.widget.provider;

import android.net.Uri;
import io.rong.imkit.R;
import io.rong.imkit.RongContext;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.userInfoCache.RongUserInfoManager;

@ConversationProviderTag(conversationType = "discussion", portraitPosition = 1)
public class DiscussionConversationProvider extends PrivateConversationProvider implements IContainerItemProvider.ConversationProvider<UIConversation> {
    @Override
    public String getTitle(String id) {
        String name;
        if (RongUserInfoManager.getInstance().getDiscussionInfo(id) == null) {
            name = RongContext.getInstance().getResources().getString(R.string.rc_conversation_list_default_discussion_name);
        } else {
            name = RongUserInfoManager.getInstance().getDiscussionInfo(id).getName();
        }
        return name;
    }

    @Override
    public Uri getPortraitUri(String id) {
        return null;
    }
}
