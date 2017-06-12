package io.rong.imkit.widget.provider;

import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.userInfoCache.RongUserInfoManager;

@ConversationProviderTag(conversationType = "system", portraitPosition = 1)
public class SystemConversationProvider extends PrivateConversationProvider implements IContainerItemProvider.ConversationProvider<UIConversation> {

    @Override
    public String getTitle(String id) {
        String name;
        if (RongUserInfoManager.getInstance().getUserInfo(id) == null) {
            name = id;
        } else {
            name = RongUserInfoManager.getInstance().getUserInfo(id).getName();
        }
        return name;
    }



}
