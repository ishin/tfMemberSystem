package io.rong.imkit.userInfoCache;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import io.rong.common.RLog;
import io.rong.imkit.RongIM;
import io.rong.imkit.cache.RongCache;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.utilities.KitCommonDefine;
import io.rong.imkit.utils.StringUtils;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.UserInfo;

public class RongUserInfoManager implements Handler.Callback {
    private final static String TAG = "RongUserInfoManager";

    private static final int USER_CACHE_MAX_COUNT = 256;
    private static final int PUBLIC_ACCOUNT_CACHE_MAX_COUNT = 64;
    private static final int GROUP_CACHE_MAX_COUNT = 128;
    private static final int DISCUSSION_CACHE_MAX_COUNT = 16;

    private final static int EVENT_INIT = 0;
    private final static int EVENT_CONNECT = 1;
    private final static int EVENT_GET_USER_INFO = 2;
    private final static int EVENT_GET_GROUP_INFO = 3;
    private final static int EVENT_GET_GROUP_USER_INFO = 4;
    private final static int EVENT_GET_DISCUSSION = 5;
    private final static int EVENT_UPDATE_USER_INFO = 7;
    private final static int EVENT_UPDATE_GROUP_USER_INFO = 8;
    private final static int EVENT_UPDATE_GROUP_INFO = 9;
    private final static int EVENT_UPDATE_DISCUSSION = 10;
    private final static int EVENT_LOGOUT = 11;
    private final static int EVENT_CLEAR_CACHE = 12;
    private final static String GROUP_PREFIX = "groups";

    private List<String> mRequestCache;

    private RongDatabaseDao mRongDatabaseDao;
    private RongCache<String, UserInfo> mUserInfoCache;
    private RongCache<String, GroupUserInfo> mGroupUserInfoCache;
    private RongCache<String, RongConversationInfo> mGroupCache;
    private RongCache<String, RongConversationInfo> mDiscussionCache;
    private RongCache<String, PublicServiceProfile> mPublicServiceProfileCache;
    private IRongCacheListener mCacheListener;
    private boolean mIsCacheUserInfo = true;
    private boolean mIsCacheGroupInfo = true;
    private boolean mIsCacheGroupUserInfo = true;
    private Handler mWorkHandler;
    private String mAppKey;
    private String mUserId;
    private boolean mInitialized;
    private Context mContext;

    private static class SingletonHolder {
        static RongUserInfoManager sInstance = new RongUserInfoManager();
    }

    private RongUserInfoManager() {
        mUserInfoCache = new RongCache<>(USER_CACHE_MAX_COUNT);
        mGroupUserInfoCache = new RongCache<>(USER_CACHE_MAX_COUNT);
        mGroupCache = new RongCache<>(GROUP_CACHE_MAX_COUNT);
        mDiscussionCache = new RongCache<>(DISCUSSION_CACHE_MAX_COUNT);
        mPublicServiceProfileCache = new RongCache<>(PUBLIC_ACCOUNT_CACHE_MAX_COUNT);
        HandlerThread workThread = new HandlerThread("RongUserInfoManager");
        workThread.start();
        mWorkHandler = new Handler(workThread.getLooper(), this);
        mInitialized = false;
        mRequestCache = new ArrayList<>();
    }

    public void setIsCacheUserInfo(boolean mIsCacheUserInfo) {
        this.mIsCacheUserInfo = mIsCacheUserInfo;
    }

    public void setIsCacheGroupInfo(boolean mIsCacheGroupInfo) {
        this.mIsCacheGroupInfo = mIsCacheGroupInfo;
    }

    public void setIsCacheGroupUserInfo(boolean mIsCacheGroupUserInfo) {
        this.mIsCacheGroupUserInfo = mIsCacheGroupUserInfo;
    }

    public static RongUserInfoManager getInstance() {
        return SingletonHolder.sInstance;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case EVENT_INIT:
                mRongDatabaseDao = new RongDatabaseDao();
                if (!TextUtils.isEmpty(mUserId)) {
                    mRongDatabaseDao.open(mContext, mAppKey, mUserId);
                }
                break;
            case EVENT_CONNECT:
                String userId = (String) msg.obj;
                if (TextUtils.isEmpty(mUserId)) {
                    mUserId = userId;
                    RLog.d(TAG, "onConnected, userId = " + userId);
                    updateCachedUserId(userId);
                    if(mRongDatabaseDao != null){
                        mRongDatabaseDao.open(mContext, mAppKey, mUserId);
                    }
                } else if (!mUserId.equals(userId)) {
                    RLog.d(TAG, "onConnected, user changed, old userId = " + mUserId + ", userId = " + userId);
                    clearUserInfoCache();
                    mUserId = userId;
                    updateCachedUserId(userId);
                    if(mRongDatabaseDao != null) {
                        mRongDatabaseDao.close();
                        mRongDatabaseDao.open(mContext, mAppKey, mUserId);
                    }
                }
                break;
            case EVENT_GET_GROUP_INFO:
                String groupId = (String) msg.obj;
                Group group = null;
                String cacheGroupId = GROUP_PREFIX + groupId;
                if (mRequestCache.contains(cacheGroupId)) {
                    break;
                }
                mRequestCache.add(cacheGroupId);
                if (mRongDatabaseDao != null) {
                    group = mRongDatabaseDao.getGroupInfo(groupId);
                }
                if (group == null) {
                    if (mCacheListener != null) {
                        group = mCacheListener.getGroupInfo(groupId);
                    }
                    if (group != null) {
                        if (mRongDatabaseDao != null) {
                            mRongDatabaseDao.putGroupInfo(group);
                        }
                    }
                }
                if (group != null) {
                    RongConversationInfo conversationInfo = new RongConversationInfo(Conversation.ConversationType.GROUP.getValue() + "", group.getId(), group.getName(), group.getPortraitUri());
                    mGroupCache.put(groupId, conversationInfo);
                    mRequestCache.remove(cacheGroupId);
                    if (mCacheListener != null) {
                        mCacheListener.onGroupUpdated(group);
                    }
                }
                break;
            case EVENT_GET_GROUP_USER_INFO:
                GroupUserInfo groupUserInfo = null;
                if (mRequestCache.contains((String) msg.obj)) {
                    break;
                }
                mRequestCache.add((String) msg.obj);
                groupId = StringUtils.getArg1((String) msg.obj);
                userId = StringUtils.getArg2((String) msg.obj);
                if (mRongDatabaseDao != null) {
                    groupUserInfo = mRongDatabaseDao.getGroupUserInfo(groupId, userId);
                }
                if (groupUserInfo == null) {
                    if (mCacheListener != null) {
                        groupUserInfo = mCacheListener.getGroupUserInfo(groupId, userId);
                    }
                    if (groupUserInfo != null && mRongDatabaseDao != null) {
                        mRongDatabaseDao.putGroupUserInfo(groupUserInfo);
                    }
                }
                if (groupUserInfo != null) {
                    mGroupUserInfoCache.put((String) msg.obj, groupUserInfo);
                    mRequestCache.remove((String) msg.obj);
                    if (mCacheListener != null) {
                        mCacheListener.onGroupUserInfoUpdated(groupUserInfo);
                    }
                }
                break;
            case EVENT_GET_DISCUSSION:
                final String discussionId = (String) msg.obj;
                Discussion discussion = null;
                if (mRongDatabaseDao != null) {
                    discussion = mRongDatabaseDao.getDiscussionInfo(discussionId);
                }
                if (discussion != null) {
                    RongConversationInfo conversationInfo = new RongConversationInfo(Conversation.ConversationType.DISCUSSION.getValue() + "", discussion.getId(), discussion.getName(), null);
                    mDiscussionCache.put(discussionId, conversationInfo);
                    if (mCacheListener != null) {
                        mCacheListener.onDiscussionUpdated(discussion);
                    }
                } else {
                    RongIM.getInstance().getDiscussion(discussionId, new RongIMClient.ResultCallback<Discussion>() {
                        @Override
                        public void onSuccess(Discussion discussion) {
                            if (discussion != null) {
                                if (mRongDatabaseDao != null) {
                                    mRongDatabaseDao.putDiscussionInfo(discussion);
                                }
                                RongConversationInfo conversationInfo = new RongConversationInfo(Conversation.ConversationType.DISCUSSION.getValue() + "", discussion.getId(), discussion.getName(), null);
                                mDiscussionCache.put(discussionId, conversationInfo);
                                if (mCacheListener != null) {
                                    mCacheListener.onDiscussionUpdated(discussion);
                                }
                            }
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    });
                }
                break;
            case EVENT_UPDATE_GROUP_USER_INFO:
                groupUserInfo = (GroupUserInfo) msg.obj;
                String key = StringUtils.getKey(groupUserInfo.getGroupId(), groupUserInfo.getUserId());
                final GroupUserInfo oldGroupUserInfo = mGroupUserInfoCache.put(key, groupUserInfo);
                if ((oldGroupUserInfo == null)
                        || (oldGroupUserInfo.getNickname() != null && groupUserInfo.getNickname() != null && !oldGroupUserInfo.getNickname().equals(groupUserInfo.getNickname()))) {
                    mRequestCache.remove(key);
                    if (mRongDatabaseDao != null) {
                        mRongDatabaseDao.putGroupUserInfo(groupUserInfo);
                    }
                    if (mCacheListener != null) {
                        mCacheListener.onGroupUserInfoUpdated(groupUserInfo);
                    }
                }
                break;
            case EVENT_UPDATE_GROUP_INFO:
                group = (Group) msg.obj;
                RongConversationInfo conversationInfo = new RongConversationInfo(Conversation.ConversationType.GROUP.getValue() + "", group.getId(), group.getName(), group.getPortraitUri());
                RongConversationInfo oldConversationInfo = mGroupCache.put(conversationInfo.getId(), conversationInfo);
                if ((oldConversationInfo == null)
                        || (oldConversationInfo.getName() != null && conversationInfo.getName() != null && !oldConversationInfo.getName().equals(conversationInfo.getName()))
                        || (oldConversationInfo.getUri() != null && conversationInfo.getUri() != null && !oldConversationInfo.getUri().toString().equals(conversationInfo.getUri().toString()))) {
                    String cachedGroupId = GROUP_PREFIX + group.getId();
                    mRequestCache.remove(cachedGroupId);
                    if (mRongDatabaseDao != null) {
                        mRongDatabaseDao.putGroupInfo(group);
                    }
                    if (mCacheListener != null) {
                        mCacheListener.onGroupUpdated(group);
                    }
                }
                break;
            case EVENT_UPDATE_DISCUSSION:
                discussion = (Discussion) msg.obj;
                conversationInfo = new RongConversationInfo(Conversation.ConversationType.DISCUSSION.getValue() + "", discussion.getId(), discussion.getName(), null);
                oldConversationInfo = mDiscussionCache.put(conversationInfo.getId(), conversationInfo);
                if ((oldConversationInfo == null)
                        || (oldConversationInfo.getName() != null && conversationInfo.getName() != null && !oldConversationInfo.getName().equals(conversationInfo.getName()))) {
                    if (mRongDatabaseDao != null) {
                        mRongDatabaseDao.putDiscussionInfo(discussion);
                    }
                    if (mCacheListener != null) {
                        mCacheListener.onDiscussionUpdated(discussion);
                    }
                }
                break;
            case EVENT_GET_USER_INFO:
                userId = (String) msg.obj;
                UserInfo userInfo = null;
                if (mRequestCache.contains(userId)) break;
                mRequestCache.add(userId);
                if (mRongDatabaseDao != null) {
                    userInfo = mRongDatabaseDao.getUserInfo(userId);
                }
                if (userInfo == null) {
                    if (mCacheListener != null) {
                        userInfo = mCacheListener.getUserInfo(userId);
                    }
                    if (userInfo != null) {
                        putUserInfoInDB(userInfo);
                    }
                }
                if (userInfo != null) {
                    putUserInfoInCache(userInfo);
                    mRequestCache.remove(userId);
                    if (mCacheListener != null) {
                        mCacheListener.onUserInfoUpdated(userInfo);
                    }
                }
                break;
            case EVENT_UPDATE_USER_INFO:
                userInfo = (UserInfo) msg.obj;
                UserInfo oldUserInfo = putUserInfoInCache(userInfo);
                if ((oldUserInfo == null)
                        || (oldUserInfo.getName() != null && userInfo.getName() != null && !oldUserInfo.getName().equals(userInfo.getName()))
                        || (oldUserInfo.getPortraitUri() != null && userInfo.getPortraitUri() != null && !oldUserInfo.getPortraitUri().toString().equals(userInfo.getPortraitUri().toString()))) {
                    putUserInfoInDB(userInfo);
                    mRequestCache.remove(userInfo.getUserId());
                    if (mCacheListener != null) {
                        mCacheListener.onUserInfoUpdated(userInfo);
                    }
                }
                break;
            case EVENT_LOGOUT:
                clearUserInfoCache();
                mInitialized = false;
                mCacheListener = null;
                mUserId = null;
                mAppKey = null;
                if (mRongDatabaseDao != null) {
                    mRongDatabaseDao.close();
                    mRongDatabaseDao = null;
                }
                updateCachedUserId("");
                break;
            case EVENT_CLEAR_CACHE:
                mRequestCache.clear();
                break;
        }
        return false;
    }

    public void init(Context context, String appKey, IRongCacheListener listener) {
        if (TextUtils.isEmpty(appKey)) {
            RLog.e(TAG, "init, appkey is null.");
            return;
        }
        if (mInitialized) {
            RLog.d(TAG, "has been init, no need init again");
            return;
        }
        mContext = context;
        mUserId = getCachedUserId();
        mAppKey = appKey;
        mCacheListener = listener;
        mInitialized = true;
        mWorkHandler.sendEmptyMessage(EVENT_INIT);
        RLog.d(TAG, "init, mUserId = " + mUserId);
    }

    public void onConnected(String userId) {
        if (TextUtils.isEmpty(userId)) {
            RLog.e(TAG, "onConnected, appkey is null.");
            return;
        }
        Message message = Message.obtain();
        message.what = EVENT_CONNECT;
        message.obj = userId;
        mWorkHandler.sendMessage(message);
    }

    private void clearUserInfoCache() {
        if (mUserInfoCache != null) {
            mUserInfoCache.clear();
        }
        if (mDiscussionCache != null) {
            mDiscussionCache.clear();
        }
        if (mGroupCache != null) {
            mGroupCache.clear();
        }
        if (mGroupUserInfoCache != null) {
            mGroupUserInfoCache.clear();
        }
        if (mPublicServiceProfileCache != null) {
            mPublicServiceProfileCache.clear();
        }
    }

    public void uninit() {
        RLog.i(TAG, "uninit");
        mWorkHandler.sendEmptyMessage(EVENT_LOGOUT);
    }

    private String getCachedUserId() {
        if (mContext != null) {
            SharedPreferences preferences = mContext.getSharedPreferences(KitCommonDefine.RONG_KIT_SP_CONFIG, Context.MODE_PRIVATE);
            return preferences.getString("userID", null);
        } else {
            return null;
        }
    }

    private void updateCachedUserId(String userId) {
        if (mContext != null) {
            SharedPreferences preferences = mContext.getSharedPreferences(KitCommonDefine.RONG_KIT_SP_CONFIG, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("userID", userId);
            editor.commit();
        }
    }

    private UserInfo putUserInfoInCache(UserInfo info) {
        if (mUserInfoCache != null) {
            return mUserInfoCache.put(info.getUserId(), info);
        } else {
            return null;
        }
    }

    private void insertUserInfoInDB(UserInfo info) {
        if (mRongDatabaseDao != null) {
            mRongDatabaseDao.insertUserInfo(info);
        }
    }

    private void putUserInfoInDB(UserInfo info) {
        if (mRongDatabaseDao != null) {
            mRongDatabaseDao.putUserInfo(info);
        }
    }

    public UserInfo getUserInfo(final String id) {
        if (id == null) {
            return null;
        }
        UserInfo info = null;

        if (mIsCacheUserInfo) {
            info = mUserInfoCache.get(id);
            if (info == null) {
                Message message = Message.obtain();
                message.what = EVENT_GET_USER_INFO;
                message.obj = id;
                mWorkHandler.sendMessage(message);
                if (!mWorkHandler.hasMessages(EVENT_CLEAR_CACHE)) {
                    mWorkHandler.sendEmptyMessageDelayed(EVENT_CLEAR_CACHE, 30 * 1000);
                }
            }
        } else {
            if (mCacheListener != null) {
                info = mCacheListener.getUserInfo(id);
            }
        }
        return info;
    }

    public GroupUserInfo getGroupUserInfo(final String gId, final String id) {
        if (gId == null || id == null) {
            return null;
        }
        final String key = StringUtils.getKey(gId, id);
        GroupUserInfo info = null;
        if (mIsCacheGroupUserInfo) {
            info = mGroupUserInfoCache.get(key);
            if (info == null) {
                Message message = Message.obtain();
                message.what = EVENT_GET_GROUP_USER_INFO;
                message.obj = key;
                mWorkHandler.sendMessage(message);
                if (!mWorkHandler.hasMessages(EVENT_CLEAR_CACHE)) {
                    mWorkHandler.sendEmptyMessageDelayed(EVENT_CLEAR_CACHE, 30 * 1000);
                }
            }
        } else {
            if (mCacheListener != null) {
                info = mCacheListener.getGroupUserInfo(gId, id);
            }
        }
        return info;
    }

    public Group getGroupInfo(final String id) {
        if (id == null) {
            return null;
        }
        Group groupInfo = null;
        if (mIsCacheGroupInfo) {
            RongConversationInfo info = mGroupCache.get(id);
            if (info == null) {
                Message message = Message.obtain();
                message.what = EVENT_GET_GROUP_INFO;
                message.obj = id;
                mWorkHandler.sendMessage(message);
                if (!mWorkHandler.hasMessages(EVENT_CLEAR_CACHE)) {
                    mWorkHandler.sendEmptyMessageDelayed(EVENT_CLEAR_CACHE, 30 * 1000);
                }
            } else {
                groupInfo = new Group(info.getId(), info.getName(), info.getUri());
            }
        } else {
            if (mCacheListener != null) {
                groupInfo = mCacheListener.getGroupInfo(id);
            }
        }
        return groupInfo;
    }

    public Discussion getDiscussionInfo(final String id) {
        if (id == null) {
            return null;
        }
        Discussion discussionInfo = null;
        RongConversationInfo info = mDiscussionCache.get(id);
        if (info == null) {
            Message message = Message.obtain();
            message.what = EVENT_GET_DISCUSSION;
            message.obj = id;
            mWorkHandler.sendMessage(message);
        } else {
            discussionInfo = new Discussion(info.getId(), info.getName());
        }
        return discussionInfo;
    }

    public PublicServiceProfile getPublicServiceProfile(final Conversation.PublicServiceType type, final String id) {
        if (type == null || id == null) {
            return null;
        }
        final String key = StringUtils.getKey(type.getValue() + "", id);

        PublicServiceProfile info = mPublicServiceProfileCache.get(key);

        if (info == null) {
            mWorkHandler.post(new Runnable() {
                @Override
                public void run() {
                    RongIM.getInstance().getPublicServiceProfile(type, id, new RongIMClient.ResultCallback<PublicServiceProfile>() {
                        @Override
                        public void onSuccess(PublicServiceProfile result) {
                            if (result != null) {
                                mPublicServiceProfileCache.put(key, result);
                                if (mCacheListener != null) {
                                    mCacheListener.onPublicServiceProfileUpdated(result);
                                }
                            }
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    });
                }
            });
        }
        return info;
    }

    public void setUserInfo(final UserInfo info) {
        if (mIsCacheUserInfo) {
            Message message = Message.obtain();
            message.what = EVENT_UPDATE_USER_INFO;
            message.obj = info;
            mWorkHandler.sendMessage(message);
        } else {
            if (mCacheListener != null) {
                mCacheListener.onUserInfoUpdated(info);
            }
        }
    }

    public void setGroupUserInfo(final GroupUserInfo info) {
        if (mIsCacheGroupUserInfo) {
            Message message = Message.obtain();
            message.what = EVENT_UPDATE_GROUP_USER_INFO;
            message.obj = info;
            mWorkHandler.sendMessage(message);
        } else {
            if (mCacheListener != null) {
                mCacheListener.onGroupUserInfoUpdated(info);
            }
        }
    }

    public void setGroupInfo(final Group group) {
        if (mIsCacheGroupInfo) {
            Message message = Message.obtain();
            message.what = EVENT_UPDATE_GROUP_INFO;
            message.obj = group;
            mWorkHandler.sendMessage(message);
        } else {
            if (mCacheListener != null) {
                mCacheListener.onGroupUpdated(group);
            }
        }
    }

    public void setDiscussionInfo(final Discussion discussion) {
        Message message = Message.obtain();
        message.what = EVENT_UPDATE_DISCUSSION;
        message.obj = discussion;
        mWorkHandler.sendMessage(message);
    }

    public void setPublicServiceProfile(final PublicServiceProfile profile) {
        String key = StringUtils.getKey(profile.getConversationType().getValue() + "", profile.getTargetId());
        PublicServiceProfile oldInfo = mPublicServiceProfileCache.put(key, profile);

        if ((oldInfo == null)
                || (oldInfo.getName() != null && profile.getName() != null && !oldInfo.getName().equals(profile.getName()))
                || (oldInfo.getPortraitUri() != null && profile.getPortraitUri() != null && !oldInfo.getPortraitUri().toString().equals(profile.getPortraitUri().toString()))) {
            if (mCacheListener != null) {
                mCacheListener.onPublicServiceProfileUpdated(profile);
            }
        }
    }
}
