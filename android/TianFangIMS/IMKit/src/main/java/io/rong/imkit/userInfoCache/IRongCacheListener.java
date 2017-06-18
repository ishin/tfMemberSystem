package io.rong.imkit.userInfoCache;

import io.rong.imkit.model.GroupUserInfo;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.UserInfo;

public interface IRongCacheListener {
    void onUserInfoUpdated(UserInfo info);

    void onGroupUserInfoUpdated(GroupUserInfo info);

    void onGroupUpdated(Group group);

    void onDiscussionUpdated(Discussion discussion);

    void onPublicServiceProfileUpdated(PublicServiceProfile profile);

    UserInfo getUserInfo(String id);

    GroupUserInfo getGroupUserInfo(String group, String id);

    Group getGroupInfo(String id);
}
