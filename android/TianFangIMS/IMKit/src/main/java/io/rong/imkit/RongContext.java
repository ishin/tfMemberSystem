package io.rong.imkit;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Handler;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.rong.common.FileUtils;
import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imageloader.cache.disc.impl.ext.LruDiskCache;
import io.rong.imageloader.cache.disc.naming.Md5FileNameGenerator;
import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.core.ImageLoaderConfiguration;
import io.rong.imageloader.utils.StorageUtils;
import io.rong.imkit.cache.RongCache;
import io.rong.imkit.cache.RongCacheWrap;
import io.rong.imkit.model.ConversationInfo;
import io.rong.imkit.model.ConversationKey;
import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.model.Event;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utilities.KitCommonDefine;
import io.rong.imlib.common.WeakValueHashMap;
import io.rong.imkit.utils.RongAuthImageDownloader;
import io.rong.imkit.utils.StringUtils;
import io.rong.imkit.widget.provider.AppServiceConversationProvider;
import io.rong.imkit.widget.provider.CustomerServiceConversationProvider;
import io.rong.imkit.widget.provider.DiscussionConversationProvider;
import io.rong.imkit.widget.provider.EvaluateTextMessageItemProvider;
import io.rong.imkit.widget.provider.GroupConversationProvider;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imkit.widget.provider.PrivateConversationProvider;
import io.rong.imkit.widget.provider.PublicServiceConversationProvider;
import io.rong.imkit.widget.provider.SystemConversationProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.UserInfo;

public class RongContext extends ContextWrapper {
    private final static String TAG = "RongContext";

    private static final int NOTIFICATION_CACHE_MAX_COUNT = 64;

    private static RongContext sContext;
    private EventBus mBus;

    private ExecutorService executorService;
    private RongIM.ConversationBehaviorListener mConversationBehaviorListener;// 会话页面
    private RongIM.ConversationListBehaviorListener mConversationListBehaviorListener;// 会话列表页面
    private RongIM.PublicServiceBehaviorListener mPublicServiceBehaviorListener;//公众号界面
    private RongIM.OnSelectMemberListener mMemberSelectListener;
    private RongIM.OnSendMessageListener mOnSendMessageListener;//发送消息监听
    private RongIM.RequestPermissionsListener mRequestPermissionsListener; //Android 6.0以上系统时，请求权限监听器
    private IPublicServiceMenuClickListener mPublicServiceMenuClickListener; //公众号菜单点击事件监听器

    private RongIM.UserInfoProvider mUserInfoProvider;
    private RongIM.GroupInfoProvider mGroupProvider;
    private RongIM.GroupUserInfoProvider mGroupUserInfoProvider;

    private Map<Class<? extends MessageContent>, IContainerItemProvider.MessageProvider> mTemplateMap;
    private Map<Class<? extends MessageContent>, IContainerItemProvider.MessageProvider> mWeakTemplateMap;
    private Map<Class<? extends MessageContent>, ProviderTag> mProviderMap;
    private Map<String, IContainerItemProvider.ConversationProvider> mConversationProviderMap;
    private Map<String, ConversationProviderTag> mConversationTagMap;

    private RongCache<String, Conversation.ConversationNotificationStatus> mNotificationCache;

    private List<Conversation.ConversationType> mReadReceiptConversationTypeList;
    private RongIM.LocationProvider mLocationProvider;

    private List<String> mCurrentConversationList;

    Handler mHandler;

    private UserInfo mCurrentUserInfo;

    private boolean isUserInfoAttached;

    private boolean isShowUnreadMessageState;
    private boolean isShowNewMessageState;
    private EvaluateTextMessageItemProvider evaluateTextMessageItemProvider;

    static public void init(Context context) {

        if (sContext == null) {
            sContext = new RongContext(context);
            sContext.initRegister();
        }
    }


    public static RongContext getInstance() {
        return sContext;
    }

    protected RongContext(Context base) {
        super(base);

        mBus = EventBus.getDefault();
        mHandler = new Handler(getMainLooper());

        mTemplateMap = new HashMap<Class<? extends MessageContent>, IContainerItemProvider.MessageProvider>();
        mWeakTemplateMap = new WeakValueHashMap();

        mProviderMap = new HashMap<Class<? extends MessageContent>, ProviderTag>();

        mConversationProviderMap = new HashMap<String, IContainerItemProvider.ConversationProvider>();

        mConversationTagMap = new HashMap<String, ConversationProviderTag>();

        mCurrentConversationList = new ArrayList<String>();

        mReadReceiptConversationTypeList = new ArrayList<>();
        mReadReceiptConversationTypeList.add(Conversation.ConversationType.PRIVATE);

        initCache();

        //TODO
        executorService = Executors.newSingleThreadExecutor();

        RongNotificationManager.getInstance().init(this);

        ImageLoader.getInstance().init(getDefaultConfig(getApplicationContext()));
    }

    private ImageLoaderConfiguration getDefaultConfig(Context context) {
        ImageLoaderConfiguration config;
        String path = FileUtils.getCachePath(context, "image");
        File cacheDir;
        if(TextUtils.isEmpty(path)) {
            cacheDir = StorageUtils.getOwnCacheDirectory(context, context.getPackageName() + "/cache/image/");
        } else {
            cacheDir = new File(path);
        }
        try {
            config = new ImageLoaderConfiguration
            .Builder(context)
            .threadPoolSize(3) // 线程池内加载的数量
            .threadPriority(Thread.NORM_PRIORITY - 2) // 降低线程的优先级，减小对UI主线程的影响
            .denyCacheImageMultipleSizesInMemory()
            .diskCache(new LruDiskCache(cacheDir, new Md5FileNameGenerator(), 0))
            .imageDownloader(new RongAuthImageDownloader(this))
            .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
            .build();
            io.rong.imageloader.utils.L.writeLogs(false);
            return config;

        } catch (IOException e) {
            RLog.i(TAG, "Use default ImageLoader config.");
            config = ImageLoaderConfiguration.createDefault(context);
        }
        return config;
    }

    private void initRegister() {
        registerConversationTemplate(new PrivateConversationProvider());
        registerConversationTemplate(new GroupConversationProvider());
        registerConversationTemplate(new DiscussionConversationProvider());
        registerConversationTemplate(new SystemConversationProvider());
        registerConversationTemplate(new CustomerServiceConversationProvider());
        registerConversationTemplate(new AppServiceConversationProvider());
        registerConversationTemplate(new PublicServiceConversationProvider());
    }

    private void initCache() {
        mNotificationCache = new RongCacheWrap<String, Conversation.ConversationNotificationStatus>(this, NOTIFICATION_CACHE_MAX_COUNT) {
            Vector<String> mRequests = new Vector<String>();

            @Override
            public Conversation.ConversationNotificationStatus obtainValue(final String key) {

                if (TextUtils.isEmpty(key))
                    return null;

                if (mRequests.contains(key))
                    return null;
                mRequests.add(key);

                mHandler.post(new Runnable() {

                    @Override
                    public void run() {

                        final ConversationKey conversationKey = ConversationKey.obtain(key);

                        if (conversationKey != null) {

                            RongIM.getInstance().getConversationNotificationStatus(conversationKey.getType(),
                            conversationKey.getTargetId(), new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {

                                @Override
                                public void onSuccess(Conversation.ConversationNotificationStatus status) {
                                    mRequests.remove(key);
                                    put(key, status);
                                    getContext().getEventBus().post(new Event.ConversationNotificationEvent(conversationKey.getTargetId(),
                                                                    conversationKey.getType(), status));
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    mRequests.remove(key);
                                }
                            });
                        }
                    }
                });

                return null;
            }
        };
    }

    public List<ConversationInfo> getCurrentConversationList() {
        ArrayList<ConversationInfo> infos = new ArrayList<>();
        int size = mCurrentConversationList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                ConversationKey key = ConversationKey.obtain(mCurrentConversationList.get(i));
                ConversationInfo info = ConversationInfo.obtain(key.getType(), key.getTargetId());
                infos.add(info);
            }
        }
        return infos;
    }

    public EventBus getEventBus() {
        return mBus;
    }

    public void registerConversationTemplate(IContainerItemProvider.ConversationProvider provider) {
        ConversationProviderTag tag = provider.getClass().getAnnotation(ConversationProviderTag.class);
        if (tag == null)
            throw new RuntimeException("No ConversationProviderTag added with your provider!");
        mConversationProviderMap.put(tag.conversationType(), provider);
        mConversationTagMap.put(tag.conversationType(), tag);
    }

    public IContainerItemProvider.ConversationProvider getConversationTemplate(String conversationType) {
        return mConversationProviderMap.get(conversationType);
    }

    public ConversationProviderTag getConversationProviderTag(String conversationType) {
        if (!mConversationProviderMap.containsKey(conversationType)) {
            throw new RuntimeException("the conversation type hasn't been registered!");
        }
        return mConversationTagMap.get(conversationType);
    }

    public void registerMessageTemplate(IContainerItemProvider.MessageProvider provider) {
        ProviderTag tag = provider.getClass().getAnnotation(ProviderTag.class);
        if (tag == null)
            throw new RuntimeException("ProviderTag not def MessageContent type");
        mTemplateMap.put(tag.messageContent(), provider);
        mProviderMap.put(tag.messageContent(), tag);
    }

    public IContainerItemProvider.MessageProvider getMessageTemplate(Class<? extends MessageContent> type) {
        IContainerItemProvider.MessageProvider provider = mWeakTemplateMap.get(type);
        if (provider == null) {
            try {
                if (mTemplateMap != null && mTemplateMap.get(type) != null) {
                    provider = (IContainerItemProvider.MessageProvider) mTemplateMap.get(type).clone();
                    mWeakTemplateMap.put(type, provider);
                } else {
                    RLog.e(TAG, "The template of message can't be null. type :" + type);
                }
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return provider;
    }

    public ProviderTag getMessageProviderTag(Class<? extends MessageContent> type) {
        return mProviderMap.get(type);
    }

    public EvaluateTextMessageItemProvider getEvaluateProvider() {
        if (evaluateTextMessageItemProvider == null) {
            evaluateTextMessageItemProvider = new EvaluateTextMessageItemProvider();
        }
        return evaluateTextMessageItemProvider;
    }

    public void executorBackground(Runnable runnable) {
        if (runnable == null)
            return;

        executorService.execute(runnable);
    }


    public UserInfo getUserInfoFromCache(String userId) {
        if (userId != null) {
            return RongUserInfoManager.getInstance().getUserInfo(userId);
        } else {
            return null;
        }
    }

    public Group getGroupInfoFromCache(String groupId) {
        if (groupId != null) {
            return RongUserInfoManager.getInstance().getGroupInfo(groupId);
        } else {
            return null;
        }
    }

    public GroupUserInfo getGroupUserInfoFromCache(String groupId, String userId) {
        return RongUserInfoManager.getInstance().getGroupUserInfo(groupId, userId);
    }

    public Discussion getDiscussionInfoFromCache(String discussionId) {
        return RongUserInfoManager.getInstance().getDiscussionInfo(discussionId);
    }

    public PublicServiceProfile getPublicServiceInfoFromCache(String messageKey) {
        String id = StringUtils.getArg1(messageKey);
        String arg2 = StringUtils.getArg2(messageKey);
        int iArg2 = Integer.parseInt(arg2);
        Conversation.PublicServiceType type = null;

        if (iArg2 == Conversation.PublicServiceType.PUBLIC_SERVICE.getValue()) {
            type = Conversation.PublicServiceType.PUBLIC_SERVICE;
        } else if (iArg2 == Conversation.PublicServiceType.APP_PUBLIC_SERVICE.getValue()) {
            type = Conversation.PublicServiceType.APP_PUBLIC_SERVICE;
        }
        return RongUserInfoManager.getInstance().getPublicServiceProfile(type, id);
    }

    public Conversation.ConversationNotificationStatus getConversationNotifyStatusFromCache(ConversationKey key) {
        Conversation.ConversationNotificationStatus status = Conversation.ConversationNotificationStatus.NOTIFY;

        if (key != null && key.getKey() != null) {
            status =  mNotificationCache.get(key.getKey());
            if (status == null) {
                status = Conversation.ConversationNotificationStatus.NOTIFY;
            }
        }
        return status;
    }

    public void setConversationNotifyStatusToCache(ConversationKey conversationKey, Conversation.ConversationNotificationStatus status) {
        mNotificationCache.put(conversationKey.getKey(), status);
    }

    public RongIM.ConversationBehaviorListener getConversationBehaviorListener() {
        return mConversationBehaviorListener;
    }

    public void setConversationBehaviorListener(RongIM.ConversationBehaviorListener conversationBehaviorListener) {
        this.mConversationBehaviorListener = conversationBehaviorListener;
    }

    public RongIM.PublicServiceBehaviorListener getPublicServiceBehaviorListener() {
        return this.mPublicServiceBehaviorListener;
    }

    public void setPublicServiceBehaviorListener(RongIM.PublicServiceBehaviorListener publicServiceBehaviorListener) {
        this.mPublicServiceBehaviorListener = publicServiceBehaviorListener;
    }

    public void setOnMemberSelectListener(RongIM.OnSelectMemberListener listener) {
        this.mMemberSelectListener = listener;
    }

    public RongIM.OnSelectMemberListener getMemberSelectListener() {
        return mMemberSelectListener;
    }


    public void setGetUserInfoProvider(RongIM.UserInfoProvider provider, boolean isCache) {
        this.mUserInfoProvider = provider;
        RongUserInfoManager.getInstance().setIsCacheUserInfo(isCache);
    }

    void setGetGroupInfoProvider(RongIM.GroupInfoProvider provider, boolean isCacheGroupInfo) {
        this.mGroupProvider = provider;
        RongUserInfoManager.getInstance().setIsCacheGroupInfo(isCacheGroupInfo);
    }

    RongIM.UserInfoProvider getUserInfoProvider() {
        return mUserInfoProvider;
    }

    public RongIM.GroupInfoProvider getGroupInfoProvider() {
        return mGroupProvider;
    }

    public void setGroupUserInfoProvider(RongIM.GroupUserInfoProvider groupUserInfoProvider, boolean isCache) {
        this.mGroupUserInfoProvider = groupUserInfoProvider;
        RongUserInfoManager.getInstance().setIsCacheGroupUserInfo(isCache);
    }

    public RongIM.GroupUserInfoProvider getGroupUserInfoProvider() {
        return mGroupUserInfoProvider;
    }

    public void registerConversationInfo(ConversationInfo info) {
        if (info != null) {
            ConversationKey key = ConversationKey.obtain(info.getTargetId(), info.getConversationType());
            if (key != null && !mCurrentConversationList.contains(key.getKey())) {
                mCurrentConversationList.add(key.getKey());
            }
        }
    }

    public void unregisterConversationInfo(ConversationInfo info) {
        if (info != null) {
            ConversationKey key = ConversationKey.obtain(info.getTargetId(), info.getConversationType());
            if (key != null && mCurrentConversationList.size() > 0) {
                mCurrentConversationList.remove(key.getKey());
            }
        }
    }


    public RongIM.LocationProvider getLocationProvider() {
        return mLocationProvider;
    }

    public void setLocationProvider(RongIM.LocationProvider locationProvider) {
        this.mLocationProvider = locationProvider;
    }

    public RongIM.OnSendMessageListener getOnSendMessageListener() {
        return mOnSendMessageListener;
    }

    public void setOnSendMessageListener(RongIM.OnSendMessageListener onSendMessageListener) {
        mOnSendMessageListener = onSendMessageListener;
    }

    /**
     * 设置当前用户信息。
     *
     * @param userInfo 当前用户信息。
     */
    public void setCurrentUserInfo(UserInfo userInfo) {
        mCurrentUserInfo = userInfo;

        if (userInfo != null && !TextUtils.isEmpty(userInfo.getUserId())) {
            RongUserInfoManager.getInstance().setUserInfo(userInfo);
        }
    }

    /**
     * 获取当前用户信息。
     *
     * @return 当前用户信息。
     */
    public UserInfo getCurrentUserInfo() {
        if (mCurrentUserInfo != null)
            return mCurrentUserInfo;

        return null;
    }

    /**
     * 获取保存的token信息。
     *
     * @return 当前用户的token信息。
     */
    public String getToken() {
        return getSharedPreferences(KitCommonDefine.RONG_KIT_SP_CONFIG, Context.MODE_PRIVATE).getString("token", "");
    }

    /**
     * 设置消息体内是否携带用户信息。
     *
     * @param state 是否携带用户信息？true:携带；false:不携带。
     */
    public void setUserInfoAttachedState(boolean state) {
        this.isUserInfoAttached = state;
    }

    /**
     * 获取当前用户关于消息体内是否携带用户信息的配置
     *
     * @return 是否携带用户信息
     */
    public boolean getUserInfoAttachedState() {
        return isUserInfoAttached;
    }

    /**
     * 设置公众服务菜单点击监听。
     * 建议使用方法：在进入对应公众服务会话时，设置监听。当退出会话时，重置监听为 null
     * {@link #setPublicServiceMenuClickListener(IPublicServiceMenuClickListener null)}. 这样可以防止内存泄露。
     *
     * @param menuClickListener 监听。
     */
    public void setPublicServiceMenuClickListener(IPublicServiceMenuClickListener menuClickListener) {
        this.mPublicServiceMenuClickListener = menuClickListener;
    }

    public IPublicServiceMenuClickListener getPublicServiceMenuClickListener() {
        return mPublicServiceMenuClickListener;
    }

    public RongIM.ConversationListBehaviorListener getConversationListBehaviorListener() {
        return mConversationListBehaviorListener;
    }

    public void setConversationListBehaviorListener(RongIM.ConversationListBehaviorListener conversationListBehaviorListener) {
        mConversationListBehaviorListener = conversationListBehaviorListener;
    }

    public void setRequestPermissionListener(RongIM.RequestPermissionsListener listener) {
        mRequestPermissionsListener = listener;
    }

    public RongIM.RequestPermissionsListener getRequestPermissionListener() {
        return mRequestPermissionsListener;
    }

    public void showUnreadMessageIcon(boolean state) {
        this.isShowUnreadMessageState = state;
    }

    public void showNewMessageIcon(boolean state) {
        this.isShowNewMessageState = state;
    }

    public boolean getUnreadMessageState() {
        return isShowUnreadMessageState;
    }

    public boolean getNewMessageState() {
        return isShowNewMessageState;
    }

    public String getGatheredConversationTitle(Conversation.ConversationType type) {
        String title = "";
        switch (type) {
            case PRIVATE:
                title = this.getString(R.string.rc_conversation_list_my_private_conversation);
                break;
            case GROUP:
                title = this.getString(R.string.rc_conversation_list_my_group);
                break;
            case DISCUSSION:
                title = this.getString(R.string.rc_conversation_list_my_discussion);
                break;
            case CHATROOM:
                title = this.getString(R.string.rc_conversation_list_my_chatroom);
                break;
            case CUSTOMER_SERVICE:
                title = this.getString(R.string.rc_conversation_list_my_customer_service);
                break;
            case SYSTEM:
                title = this.getString(R.string.rc_conversation_list_system_conversation);
                break;
            case APP_PUBLIC_SERVICE:
                title = this.getString(R.string.rc_conversation_list_app_public_service);
                break;
            case PUBLIC_SERVICE:
                title = this.getString(R.string.rc_conversation_list_public_service);
                break;
            default:
                System.err.print("It's not the default conversation type!!");
                break;
        }
        return title;
    }

    void setReadReceiptConversationTypeList(Conversation.ConversationType... types) {
        if (types == null) {
            RLog.d(TAG, "setReadReceiptConversationTypeList parameter is null");
            return;
        }
        mReadReceiptConversationTypeList.clear();
        for (Conversation.ConversationType type : types) {
            mReadReceiptConversationTypeList.add(type);
        }
    }

    public boolean isReadReceiptConversationType(Conversation.ConversationType type) {
        if (mReadReceiptConversationTypeList == null) {
            RLog.d(TAG, "isReadReceiptConversationType mReadReceiptConversationTypeList is null");
            return false;
        }
        return mReadReceiptConversationTypeList.contains(type);
    }
}
