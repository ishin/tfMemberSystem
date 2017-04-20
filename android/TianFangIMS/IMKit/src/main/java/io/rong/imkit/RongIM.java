package io.rong.imkit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.core.assist.FailReason;
import io.rong.imageloader.core.listener.ImageLoadingListener;
import io.rong.imageloader.core.listener.ImageLoadingProgressListener;
import io.rong.imkit.manager.AudioRecordManager;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imkit.manager.InternalModuleManager;
import io.rong.imkit.manager.SendImageManager;
import io.rong.imkit.manager.UnReadMessageManager;
import io.rong.imkit.mention.RongMentionManager;
import io.rong.imkit.model.ConversationKey;
import io.rong.imkit.model.Event;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.notification.MessageNotificationManager;
import io.rong.imkit.plugin.image.AlbumBitmapCacheHelper;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utilities.KitCommonDefine;
import io.rong.imkit.utils.SystemUtils;
import io.rong.imkit.widget.provider.CSPullLeaveMsgItemProvider;
import io.rong.imkit.widget.provider.DiscussionNotificationMessageItemProvider;
import io.rong.imkit.widget.provider.FileMessageItemProvider;
import io.rong.imkit.widget.provider.GroupNotificationMessageItemProvider;
import io.rong.imkit.widget.provider.HandshakeMessageItemProvider;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imkit.widget.provider.ImageMessageItemProvider;
import io.rong.imkit.widget.provider.InfoNotificationMsgItemProvider;
import io.rong.imkit.widget.provider.LocationMessageItemProvider;
import io.rong.imkit.widget.provider.PublicServiceMultiRichContentMessageProvider;
import io.rong.imkit.widget.provider.PublicServiceRichContentMessageProvider;
import io.rong.imkit.widget.provider.RealTimeLocationMessageProvider;
import io.rong.imkit.widget.provider.RecallMessageItemProvider;
import io.rong.imkit.widget.provider.RichContentMessageItemProvider;
import io.rong.imkit.widget.provider.TextMessageItemProvider;
import io.rong.imkit.widget.provider.UnknownMessageItemProvider;
import io.rong.imkit.widget.provider.VoiceMessageItemProvider;
import io.rong.imlib.AnnotationNotFoundException;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.MessageTag;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.CSCustomServiceInfo;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.PublicServiceProfileList;
import io.rong.imlib.model.UserData;
import io.rong.imlib.model.UserInfo;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.TextMessage;
import io.rong.push.RongPushClient;

/**
 * IM 界面组件核心类。
 * <p/>
 * 所有 IM 相关界面、功能都由此调用和设置。
 */
public class RongIM {

    private static final String TAG = RongIM.class.getSimpleName();
    private static final int ON_SUCCESS_CALLBACK = 100;
    private static final int ON_PROGRESS_CALLBACK = 101;
    private static final int ON_CANCEL_CALLBACK = 102;
    private static final int ON_ERROR_CALLBACK = 103;

    private static Context mContext;
    static RongIMClient.OnReceiveMessageListener sMessageListener;
    static RongIMClient.ConnectionStatusListener sConnectionStatusListener;
    private RongIMClientWrapper mClientWrapper;
    private String mAppKey;

    /**
     * 实例化客户端核心类 RongIM，同时初始化 SDK。
     */
    private RongIM() {
        mClientWrapper = new RongIMClientWrapper();
    }

    private static void saveToken(String token) {
        SharedPreferences preferences = mContext.getSharedPreferences(KitCommonDefine.RONG_KIT_SP_CONFIG, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.commit();// 提交数据到背后的xml文件中
    }

    static class SingletonHolder {
        static RongIM sRongIM = new RongIM();
    }

    /**
     * 初始化 SDK，在整个应用程序全局，只需要调用一次。
     *
     * @param context 应用上下文。
     */
    public static void init(Context context) {
        String current = SystemUtils.getCurProcessName(context);
        String mainProcessName = context.getPackageName();
        if (!mainProcessName.equals(current)) {
            RLog.w(TAG, "Init. Current process : " + current);
            return;
        }

        RLog.i(TAG, "init : " + current);

        mContext = context;
        RongContext.init(context);
        initListener();
        if (TextUtils.isEmpty(SingletonHolder.sRongIM.mAppKey)) {
            try {
                ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    SingletonHolder.sRongIM.mAppKey = applicationInfo.metaData.getString("RONG_CLOUD_APP_KEY");
                }
                if (TextUtils.isEmpty(SingletonHolder.sRongIM.mAppKey)) {
                    throw new IllegalArgumentException("can't find RONG_CLOUD_APP_KEY in AndroidManifest.xml.");
                }

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                throw new ExceptionInInitializerError("can't find packageName!");
            }
        }
        RongIMClient.init(context, SingletonHolder.sRongIM.mAppKey);

        registerMessageTemplate(new TextMessageItemProvider());
        registerMessageTemplate(new ImageMessageItemProvider());
        registerMessageTemplate(new LocationMessageItemProvider());
        registerMessageTemplate(new VoiceMessageItemProvider(context));
        registerMessageTemplate(new DiscussionNotificationMessageItemProvider());
        registerMessageTemplate(new InfoNotificationMsgItemProvider());
        registerMessageTemplate(new RichContentMessageItemProvider());
        registerMessageTemplate(new PublicServiceMultiRichContentMessageProvider());
        registerMessageTemplate(new PublicServiceRichContentMessageProvider());
        registerMessageTemplate(new HandshakeMessageItemProvider());
        registerMessageTemplate(new RecallMessageItemProvider());
        registerMessageTemplate(new FileMessageItemProvider());
        registerMessageTemplate(new GroupNotificationMessageItemProvider());
        registerMessageTemplate(new RealTimeLocationMessageProvider());
        registerMessageTemplate(new UnknownMessageItemProvider());
        registerMessageTemplate(new CSPullLeaveMsgItemProvider());

        RongExtensionManager.init(context, SingletonHolder.sRongIM.mAppKey);
        RongExtensionManager.getInstance().registerExtensionModule(new DefaultExtensionModule());

        InternalModuleManager.init(context);
        InternalModuleManager.getInstance().onInitialized(SingletonHolder.sRongIM.mAppKey);
        AlbumBitmapCacheHelper.init(context);
    }


    /**
     * 初始化 SDK，在整个应用程序全局，只需要调用一次。
     *
     * @param appKey  应用的app key.
     * @param context 应用上下文。
     */
    public static void init(Context context, String appKey) {
        String current = SystemUtils.getCurProcessName(context);
        String mainProcessName = context.getPackageName();
        if (!mainProcessName.equals(current)) {
            RLog.w(TAG, "Init with appKey. Current process : " + current);
            return;
        }

        RLog.i(TAG, "init with appKey : " + current);
        SingletonHolder.sRongIM.mAppKey = appKey;
        init(context);
    }

    /**
     * 注册消息类型，如果不对消息类型进行扩展，可以忽略此方法。
     *
     * @param messageContentClass 消息类型，必须要继承自 io.rong.imlib.model.MessageContent。
     */
    public static void registerMessageType(Class<? extends MessageContent> messageContentClass) {
        if (RongContext.getInstance() != null) {
            try {
                RongIMClient.registerMessageType(messageContentClass);
            } catch (AnnotationNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 注册消息模板。
     *
     * @param provider 模板类型。
     */
    public static void registerMessageTemplate(IContainerItemProvider.MessageProvider provider) {
        if (RongContext.getInstance() != null) {
            RongContext.getInstance().registerMessageTemplate(provider);
        }
    }

    /**
     * 设置当前用户信息。
     * 如果开发者没有实现用户信息提供者，而是使用消息携带用户信息，需要使用这个方法设置当前用户的信息，
     * 然后在{@link #init(Context)}之后调用{@link #setMessageAttachedUserInfo(boolean)}，
     * 这样可以在每条消息中携带当前用户的信息，IMKit会在接收到消息的时候取出用户信息并刷新到界面上。
     *
     * @param userInfo 当前用户信息。
     */
    public void setCurrentUserInfo(UserInfo userInfo) {
        if (RongContext.getInstance() != null) {
            RongContext.getInstance().setCurrentUserInfo(userInfo);
        }
    }

    /**
     * 连接服务器，在整个应用程序全局，只需要调用一次。
     *
     * @param token    从服务端获取的 <a
     *                 href="http://docs.rongcloud.cn/android#token">用户身份令牌（
     *                 Token）</a>。
     * @param callback 连接回调。
     * @return RongIM IM 客户端核心类的实例。
     */
    public static RongIM connect(final String token, final RongIMClient.ConnectCallback callback) {

        if (RongContext.getInstance() == null) {
            RLog.e(TAG, "connect should be called in main process.");
            return SingletonHolder.sRongIM;
        }

        saveToken(token);
        RongUserInfoManager.getInstance().init(mContext, SingletonHolder.sRongIM.mAppKey, new RongUserCacheListener());

        RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String userId) {
                if (callback != null) {
                    callback.onSuccess(userId);
                }

                RongUserInfoManager.getInstance().onConnected(userId);
                RongContext.getInstance().getEventBus().post(Event.ConnectEvent.obtain(true));
                RongExtensionManager.getInstance().connect(token);
                InternalModuleManager.getInstance().onConnected(token);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                if (callback != null) {
                    callback.onError(e);
                }
                String userId = RongIMClient.getInstance().getCurrentUserId();
                RongUserInfoManager.getInstance().onConnected(userId);
                RongExtensionManager.getInstance().connect(token);
                RongContext.getInstance().getEventBus().post(Event.ConnectEvent.obtain(false));
            }

            @Override
            public void onTokenIncorrect() {
                if (callback != null)
                    callback.onTokenIncorrect();
            }
        });
        return SingletonHolder.sRongIM;
    }

    private static RongIMClient.ConnectionStatusListener mConnectionStatusListener = new RongIMClient.ConnectionStatusListener() {

        @Override
        public void onChanged(ConnectionStatus status) {
            if (status != null) {
                RLog.d(TAG, "ConnectionStatusListener onChanged : " + status.toString());
                if (sConnectionStatusListener != null)
                    sConnectionStatusListener.onChanged(status);

                //如果 ipc 进程崩溃，会导致发送中的图片状态错误
                if (status.equals(ConnectionStatus.DISCONNECTED)) {
                    SendImageManager.getInstance().reset();
                }
                RongContext.getInstance().getEventBus().post(status);
            }
        }
    };

    private static void initListener() {

        RongIMClient.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
            @Override
            public boolean onReceived(final Message message, final int left) {
                boolean isProcess = false;

                if (sMessageListener != null)
                    isProcess = sMessageListener.onReceived(message, left); //首先透传给用户处理。

                final MessageTag msgTag = message.getContent().getClass().getAnnotation(MessageTag.class);
                //如果该条消息是计数的或者存到历史记录的，则post到相应界面显示或响铃，否则直接返回（VoIP消息除外）。
                if (msgTag != null && (msgTag.flag() == MessageTag.ISCOUNTED || msgTag.flag() == MessageTag.ISPERSISTED)) {
                    RongContext.getInstance().getEventBus().post(new Event.OnReceiveMessageEvent(message, left));

                    //如果消息中附带了用户信息，则通知界面刷新此用户信息。
                    if (message.getContent() != null && message.getContent().getUserInfo() != null) {
                        RongUserInfoManager.getInstance().setUserInfo(message.getContent().getUserInfo());
                    }

                    //如果用户自己处理铃声和后台通知，或者是web端自己发送的消息，则直接返回。
                    if (isProcess || message.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId())) {
                        return true;
                    }

                    MessageNotificationManager.getInstance().notifyIfNeed(RongContext.getInstance(), message, left);
                } else {
                    //未知的消息类型：UnknownMessage
                    if (message.getMessageId() > 0) {
                        RongContext.getInstance().getEventBus().post(new Event.OnReceiveMessageEvent(message, left));
                    } else {
                        RongContext.getInstance().getEventBus().post(new Event.MessageLeftEvent(left));
                    }
                }
                RongExtensionManager.getInstance().onReceivedMessage(message);

                return false;
            }
        });

        //消息回执监听
        boolean readRec = false;
        try {
            readRec = RongContext.getInstance().getResources().getBoolean(R.bool.rc_read_receipt);
        } catch (Resources.NotFoundException e) {
            RLog.e(TAG, "rc_read_receipt not configure in rc_config.xml");
            e.printStackTrace();
        }

        if (readRec) {
            RongIMClient.setReadReceiptListener(new RongIMClient.ReadReceiptListener() {
                @Override
                public void onReadReceiptReceived(final Message message) {
                    RongContext.getInstance().getEventBus().post(new Event.ReadReceiptEvent(message));
                }

                @Override
                public void onMessageReceiptRequest(Conversation.ConversationType type, String targetId, String messageUId) {
                    RongContext.getInstance().getEventBus().post(new Event.ReadReceiptRequestEvent(type, targetId, messageUId));
                }

                @Override
                public void onMessageReceiptResponse(Conversation.ConversationType type, String targetId, String messageUId, HashMap<String, Long> respondUserIdList) {
                    RongContext.getInstance().getEventBus().post(new Event.ReadReceiptResponseEvent(type, targetId, messageUId, respondUserIdList));
                }
            });
        }

        boolean syncReadStatus = false;
        try {
            syncReadStatus = RongContext.getInstance().getResources().getBoolean(R.bool.rc_enable_sync_read_status);
        } catch (Resources.NotFoundException e) {
            RLog.e(TAG, "rc_enable_sync_read_status not configure in rc_config.xml");
            e.printStackTrace();
        }
        if (syncReadStatus) {
            RongIMClient.getInstance().setSyncConversationReadStatusListener(new RongIMClient.SyncConversationReadStatusListener() {
                @Override
                public void onSyncConversationReadStatus(Conversation.ConversationType type, String targetId) {
                    RongContext.getInstance().getEventBus().post(new Event.SyncReadStatusEvent(type, targetId));
                }
            });
        }

        //撤回消息监听
        RongIMClient.setOnRecallMessageListener((new RongIMClient.OnRecallMessageListener() {
            @Override
            public boolean onMessageRecalled(Message message, RecallNotificationMessage recallNotificationMessage) {
                RongContext.getInstance().getEventBus().post(new Event.RemoteMessageRecallEvent(message.getMessageId(), recallNotificationMessage, true));

                final MessageTag msgTag = recallNotificationMessage.getClass().getAnnotation(MessageTag.class);
                if (msgTag != null && (msgTag.flag() == MessageTag.ISCOUNTED || msgTag.flag() == MessageTag.ISPERSISTED)) {
                    MessageNotificationManager.getInstance().notifyIfNeed(RongContext.getInstance(), message, 0);
                }
                return true;
            }
        }));

        RongIMClient.setConnectionStatusListener(mConnectionStatusListener);
    }


    /**
     * 设置接收消息的监听器。
     * <p/>
     * 所有接收到的消息、通知、状态都经由此处设置的监听器处理。包括私聊消息、讨论组消息、群组消息、聊天室消息以及各种状态。
     *
     * @param listener 接收消息的监听器。
     */
    public static void setOnReceiveMessageListener(RongIMClient.OnReceiveMessageListener listener) {
        RLog.i(TAG, "RongIM setOnReceiveMessageListener");
        sMessageListener = listener;
    }

    /**
     * 设置连接状态变化的监听器。
     *
     * @param listener 连接状态变化的监听器。
     */
    public static void setConnectionStatusListener(final RongIMClient.ConnectionStatusListener listener) {
        sConnectionStatusListener = listener;
    }

    /**
     * 该方法废弃。
     * 请直接使用 RongIM.getInstance() 获取 RongIM 实例，并直接使用 RongIM 相关方法。
     */
    @Deprecated
    public RongIMClientWrapper getRongIMClient() {
        return mClientWrapper;
    }

    /**
     * 断开连接或注销当前登录。
     *
     * @param isReceivePush 断开后是否接收 push。
     * @deprecated 该方法废弃，请使用{@link #disconnect()}或者{@link #logout()}方法。
     */
    @Deprecated
    public void disconnect(boolean isReceivePush) {
        RongIMClient.getInstance().disconnect(isReceivePush);
    }

    /**
     * 注销当前登录，执行该方法后不会再收到 push 消息。
     */
    public void logout() {
        RongIMClient.getInstance().logout();
        RongUserInfoManager.getInstance().uninit();
        UnReadMessageManager.getInstance().clearObserver();
        RongExtensionManager.getInstance().disconnect();
    }

    /**
     * 设置群组成员提供者。
     * <p/>
     * '@' 功能和VoIP功能在选人界面,需要知道群组内成员信息,开发者需要设置该提供者。 开发者需要在回调中获取到群成员信息
     * 并通过{@link IGroupMemberCallback}中的方法设置到 sdk 中
     * <p/>
     *
     * @param groupMembersProvider 群组成员提供者。
     */
    public void setGroupMembersProvider(IGroupMembersProvider groupMembersProvider) {
        RongMentionManager.getInstance().setGroupMembersProvider(groupMembersProvider);
    }

    public interface IGroupMembersProvider {
        void getGroupMembers(String groupId, IGroupMemberCallback callback);
    }

    public interface IGroupMemberCallback {
        void onGetGroupMembersResult(List<UserInfo> members);
    }

    /**
     * 位置信息的提供者，实现后获取用户位置信息。
     */
    public interface LocationProvider {
        void onStartLocation(Context context, LocationCallback callback);

        interface LocationCallback {
            void onSuccess(LocationMessage message);

            void onFailure(String msg);
        }
    }

    /**
     * 设置位置信息的提供者。
     *
     * @param locationProvider 位置信息提供者。
     */
    public static void setLocationProvider(LocationProvider locationProvider) {

        if (RongContext.getInstance() != null)
            RongContext.getInstance().setLocationProvider(locationProvider);
    }

    /**
     * 断开连接(断开后继续接收 Push 消息)。
     */
    public void disconnect() {
        RongIMClient.getInstance().disconnect();
        RongExtensionManager.getInstance().disconnect();
    }

    /**
     * 获取 IMKit RongIM 实例，需在执行 init 方法初始化 SDK 后获取否则返回值为 NULL。
     *
     * @return RongIM IM 客户端核心类的实例。
     */
    public static RongIM getInstance() {
        return SingletonHolder.sRongIM;
    }

    /**
     * 启动会话列表界面。
     *
     * @param context 应用上下文。
     * @deprecated 废弃该方法，请使用 {@link #startConversationList(Context, Map)}
     */
    @Deprecated
    public void startConversationList(Context context) {

        if (context == null)
            throw new IllegalArgumentException();

        if (RongContext.getInstance() == null)
            throw new ExceptionInInitializerError("RongCloud SDK not init");

        Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist").build();

        context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    /**
     * 启动会话列表界面。
     *
     * @param context               应用上下文。
     * @param supportedConversation 定义会话列表支持显示的会话类型，及对应的会话类型是否聚合显示。
     *                              例如：supportedConversation.put(Conversation.ConversationType.PRIVATE.getName(), false) 非聚合式显示 private 类型的会话。
     */
    public void startConversationList(Context context, Map<String, Boolean> supportedConversation) {

        if (context == null)
            throw new IllegalArgumentException();

        if (RongContext.getInstance() == null)
            throw new ExceptionInInitializerError("RongCloud SDK not init");

        Uri.Builder builder = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon().appendPath("conversationlist");
        if (supportedConversation != null && supportedConversation.size() > 0) {
            Set<String> keys = supportedConversation.keySet();
            for (String key : keys) {
                builder.appendQueryParameter(key, supportedConversation.get(key) ? "true" : "false");
            }
        }

        context.startActivity(new Intent(Intent.ACTION_VIEW, builder.build()));
    }

    /**
     * 启动聚合后的某类型的会话列表。<br> 例如：如果设置了单聊会话为聚合，则通过该方法可以打开包含所有的单聊会话的列表。
     *
     * @param context          应用上下文。
     * @param conversationType 会话类型。
     */
    public void startSubConversationList(Context context, Conversation.ConversationType conversationType) {

        if (context == null)
            throw new IllegalArgumentException();

        if (RongContext.getInstance() == null)
            throw new ExceptionInInitializerError("RongCloud SDK not init");

        Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon()
                .appendPath("subconversationlist")
                .appendQueryParameter("type", conversationType.getName())
                .build();

        context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    /**
     * 设置会话界面操作的监听器。
     *
     * @param listener 会话界面操作的监听器。
     */
    public static void setConversationBehaviorListener(ConversationBehaviorListener listener) {
        if (RongContext.getInstance() != null) {
            RongContext.getInstance().setConversationBehaviorListener(listener);
        }
    }


    /**
     * 设置会话列表界面操作的监听器。
     *
     * @param listener 会话列表界面操作的监听器。
     */
    public static void setConversationListBehaviorListener(ConversationListBehaviorListener listener) {
        if (RongContext.getInstance() != null) {
            RongContext.getInstance().setConversationListBehaviorListener(listener);
        }
    }

    /**
     * 设置公众号界面操作的监听器。
     *
     * @param listener 会话公众号界面操作的监听器。
     */
    public static void setPublicServiceBehaviorListener(PublicServiceBehaviorListener listener) {
        if (RongContext.getInstance() != null) {
            RongContext.getInstance().setPublicServiceBehaviorListener(listener);
        }
    }

    /**
     * 公众号界面操作的监听器
     */
    public interface PublicServiceBehaviorListener {
        /**
         * 当点击关注后执行。
         *
         * @param context 上下文。
         * @param info    公众号信息。
         * @return 如果用户自己处理了点击后的逻辑处理，则返回 true，否则返回 false，false 走融云默认处理方式。
         */
        boolean onFollowClick(Context context, PublicServiceProfile info);

        /**
         * 当点击取消关注后执行。
         *
         * @param context 上下文。
         * @param info    公众号信息。
         * @return 如果用户自己处理了点击后的逻辑处理，则返回 true，否则返回 false，false 走融云默认处理方式。
         */
        boolean onUnFollowClick(Context context, PublicServiceProfile info);

        /**
         * 当点击进入进入会话后执行。
         *
         * @param context 上下文。
         * @param info    公众号信息。
         * @return 如果用户自己处理了点击后的逻辑处理，则返回 true，否则返回 false，false 走融云默认处理方式。
         */
        boolean onEnterConversationClick(Context context, PublicServiceProfile info);
    }


    /**
     * 启动单聊界面。
     *
     * @param context      应用上下文。
     * @param targetUserId 要与之聊天的用户 Id。
     * @param title        聊天的标题。开发者需要在聊天界面通过intent.getData().getQueryParameter("title")获取该值, 再手动设置为聊天界面的标题。
     */
    public void startPrivateChat(Context context, String targetUserId, String title) {

        if (context == null || TextUtils.isEmpty(targetUserId))
            throw new IllegalArgumentException();

        if (RongContext.getInstance() == null)
            throw new ExceptionInInitializerError("RongCloud SDK not init");

        Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(Conversation.ConversationType.PRIVATE.getName().toLowerCase())
                .appendQueryParameter("targetId", targetUserId).appendQueryParameter("title", title).build();

        context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    /**
     * <p>启动会话界面。</p>
     * <p>使用时，可以传入多种会话类型 {@link io.rong.imlib.model.Conversation.ConversationType} 对应不同的会话类型，开启不同的会话界面。
     * 如果传入的是 {@link io.rong.imlib.model.Conversation.ConversationType#CHATROOM}，sdk 会默认调用
     * {@link RongIMClient#joinChatRoom(String, int, RongIMClient.OperationCallback)} 加入聊天室。
     * 如果你的逻辑是，只允许加入已存在的聊天室，请使用接口 {@link #startChatRoomChat(Context, String, boolean)} 并且第三个参数为 true</p>
     *
     * @param context          应用上下文。
     * @param conversationType 会话类型。
     * @param targetId         根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param title            聊天的标题。开发者需要在聊天界面通过intent.getData().getQueryParameter("title")获取该值, 再手动设置为聊天界面的标题。
     */
    public void startConversation(Context context, Conversation.ConversationType conversationType, String targetId, String title) {

        if (context == null || TextUtils.isEmpty(targetId) || conversationType == null)
            throw new IllegalArgumentException();

        Uri uri = Uri.parse("rong://" + context.getApplicationInfo().processName).buildUpon()
                .appendPath("conversation").appendPath(conversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", targetId).appendQueryParameter("title", title).build();

        context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }


    /**
     * 创建讨论组会话并进入会话界面。
     * <p>该方法会同时根据传入的 userId 创建讨论组，但无法获取奥讨论组创建结果。
     * 如果想要获取到讨论组创建结果，请使用 {@link #createDiscussion(String, List, RongIMClient.CreateDiscussionCallback)}</p>
     *
     * @param context       应用上下文。
     * @param targetUserIds 要与之聊天的讨论组用户 Id 列表。
     * @param title         聊天的标题，如果传入空值，则默认显示与之聊天的用户名称。
     */
    public void createDiscussionChat(final Context context, final List<String> targetUserIds, final String title) {

        if (context == null || targetUserIds == null || targetUserIds.size() == 0)
            throw new IllegalArgumentException();

        RongIMClient.getInstance().createDiscussion(title, targetUserIds, new RongIMClient.CreateDiscussionCallback() {
            @Override
            public void onSuccess(String targetId) {

                Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon()
                        .appendPath("conversation").appendPath(Conversation.ConversationType.DISCUSSION.getName().toLowerCase())
                        .appendQueryParameter("targetIds", TextUtils.join(",", targetUserIds)).appendQueryParameter("delimiter", ",")
                        .appendQueryParameter("targetId", targetId)
                        .appendQueryParameter("title", title).build();

                context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                RLog.d(TAG, "createDiscussionChat createDiscussion not success." + e);
            }
        });
    }

    /**
     * 创建讨论组会话并进入会话界面。
     * <p>该方法会同时根据传入的 userId 创建讨论组，并通过{@link io.rong.imlib.RongIMClient.CreateDiscussionCallback} 返回讨论组创建结果。</p>
     *
     * @param context       应用上下文。
     * @param targetUserIds 要与之聊天的讨论组用户 Id 列表。
     * @param title         聊天的标题，如果传入空值，则默认显示与之聊天的用户名称。
     * @param callback      讨论组回调，成功时，返回讨论组 id。
     */
    public void createDiscussionChat(final Context context, final List<String> targetUserIds, final String title, final RongIMClient.CreateDiscussionCallback callback) {

        if (context == null || targetUserIds == null || targetUserIds.size() == 0)
            throw new IllegalArgumentException();

        RongIMClient.getInstance().createDiscussion(title, targetUserIds, new RongIMClient.CreateDiscussionCallback() {
            @Override
            public void onSuccess(String targetId) {

                Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon()
                        .appendPath("conversation").appendPath(Conversation.ConversationType.DISCUSSION.getName().toLowerCase())
                        .appendQueryParameter("targetIds", TextUtils.join(",", targetUserIds)).appendQueryParameter("delimiter", ",")
                        .appendQueryParameter("targetId", targetId)
                        .appendQueryParameter("title", title).build();

                context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                if (callback != null)
                    callback.onSuccess(targetId);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                RLog.d(TAG, "createDiscussionChat createDiscussion not success." + e);
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    /**
     * 启动讨论组聊天界面。
     *
     * @param context            应用上下文。
     * @param targetDiscussionId 要聊天的讨论组 Id。
     * @param title              聊天的标题。开发者需要在聊天界面通过intent.getData().getQueryParameter("title")获取该值, 再手动设置为聊天界面的标题。
     */
    public void startDiscussionChat(Context context, String targetDiscussionId, String title) {

        if (context == null || TextUtils.isEmpty(targetDiscussionId)) {
            throw new IllegalArgumentException();
        }

        Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(Conversation.ConversationType.DISCUSSION.getName().toLowerCase())
                .appendQueryParameter("targetId", targetDiscussionId).appendQueryParameter("title", title).build();

        context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    /**
     * 启动群组聊天界面。
     *
     * @param context       应用上下文。
     * @param targetGroupId 要聊天的群组 Id。
     * @param title         聊天的标题。开发者需要在聊天界面通过intent.getData().getQueryParameter("title")获取该值, 再手动设置为聊天界面的标题。
     */
    public void startGroupChat(Context context, String targetGroupId, String title) {

        if (context == null || TextUtils.isEmpty(targetGroupId))
            throw new IllegalArgumentException();

        if (RongContext.getInstance() == null)
            throw new ExceptionInInitializerError("RongCloud SDK not init");

        Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(Conversation.ConversationType.GROUP.getName().toLowerCase())
                .appendQueryParameter("targetId", targetGroupId).appendQueryParameter("title", title).build();
        context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    /**
     * <p>启动聊天室会话。</p>
     * <p>设置参数 createIfNotExist 为 true，对应到 kit 中调用的接口是
     * {@link RongIMClient#joinChatRoom(String, int, RongIMClient.OperationCallback)}.
     * 如果聊天室不存在，则自动创建并加入，如果回调失败，则弹出 warning。</p>
     * <p>设置参数 createIfNotExist 为 false，对应到 kit 中调用的接口是
     * {@link RongIMClient#joinExistChatRoom(String, int, RongIMClient.OperationCallback)}.
     * 如果聊天室不存在，则返回错误 {@link io.rong.imlib.RongIMClient.ErrorCode#RC_CHATROOM_NOT_EXIST}，并且会话界面会弹出 warning.
     * </p>
     *
     * @param context          应用上下文。
     * @param chatRoomId       聊天室 id。
     * @param createIfNotExist 如果聊天室不存在，是否创建。
     */
    public void startChatRoomChat(Context context, String chatRoomId, boolean createIfNotExist) {

        if (context == null || TextUtils.isEmpty(chatRoomId))
            throw new IllegalArgumentException();

        if (RongContext.getInstance() == null)
            throw new ExceptionInInitializerError("RongCloud SDK not init");

        Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(Conversation.ConversationType.CHATROOM.getName().toLowerCase())
                .appendQueryParameter("targetId", chatRoomId).build();

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra("createIfNotExist", createIfNotExist);
        context.startActivity(intent);
    }


    /**
     * 启动客户服聊天界面。
     *
     * @param context           应用上下文。
     * @param customerServiceId 要与之聊天的客服 Id。
     * @param title             聊天的标题。开发者需要在聊天界面通过intent.getData().getQueryParameter("title")获取该值, 再手动设置为聊天界面的标题。
     * @param customServiceInfo 当前使用客服者的用户信息。{@link io.rong.imlib.model.CSCustomServiceInfo}
     */
    public void startCustomerServiceChat(Context context, String customerServiceId, String title, CSCustomServiceInfo customServiceInfo) {

        if (context == null || TextUtils.isEmpty(customerServiceId))
            throw new IllegalArgumentException();

        if (RongContext.getInstance() == null)
            throw new ExceptionInInitializerError("RongCloud SDK not init");

        Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(Conversation.ConversationType.CUSTOMER_SERVICE.getName().toLowerCase())
                .appendQueryParameter("targetId", customerServiceId).appendQueryParameter("title", title)
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra("customServiceInfo", customServiceInfo);
        context.startActivity(intent);
    }


    /**
     * <p>
     * 设置用户信息的提供者，供 RongIM 调用获取用户名称和头像信息。
     * 设置后，当 sdk 界面展示用户信息时，会回调 {@link io.rong.imkit.RongIM.UserInfoProvider#getUserInfo(String)}
     * 使用者只需要根据对应的 userId 提供对应的用户信息。
     * 如果需要异步从服务器获取用户信息，使用者可以在此方法中发起异步请求，然后返回 null 信息。
     * 在异步请求结果返回后，根据返回的结果调用 {@link #refreshUserInfoCache(UserInfo)} 刷新用户信息。
     * </p>
     *
     * @param userInfoProvider 用户信息提供者 {@link io.rong.imkit.RongIM.UserInfoProvider}。
     * @param isCacheUserInfo  设置是否由 IMKit 来缓存用户信息。<br>
     *                         如果 App 提供的 UserInfoProvider。
     *                         每次都需要通过网络请求用户数据，而不是将用户数据缓存到本地，会影响用户信息的加载速度；<br>
     *                         此时最好将本参数设置为 true，由 IMKit 来缓存用户信息。
     */
    public static void setUserInfoProvider(UserInfoProvider userInfoProvider, boolean isCacheUserInfo) {
        if (RongContext.getInstance() != null) {
            RongContext.getInstance().setGetUserInfoProvider(userInfoProvider, isCacheUserInfo);
        }
    }

    /**
     * <p>设置GroupUserInfo提供者，供RongIM 调用获取GroupUserInfo</p>
     * <p>可以使用此方法，修改群组中用户昵称</p>
     * <p>设置后，当 sdk 界面展示用户信息时，会回调 {@link io.rong.imkit.RongIM.GroupUserInfoProvider#getGroupUserInfo(String, String)}
     * 使用者只需要根据对应的 groupId, userId 提供对应的用户信息 {@link GroupUserInfo}。
     * 如果需要异步从服务器获取用户信息，使用者可以在此方法中发起异步请求，然后返回 null 信息。
     * 在异步请求结果返回后，根据返回的结果调用 {@link #refreshGroupUserInfoCache(GroupUserInfo)} 刷新信息。</p>
     *
     * @param userInfoProvider 群组用户信息提供者。
     * @param isCacheUserInfo  设置是否由 IMKit 来缓存 GroupUserInfo。<br>
     *                         如果 App 提供的 GroupUserInfoProvider。
     *                         每次都需要通过网络请求数据，而不是将数据缓存到本地，会影响信息的加载速度；<br>
     *                         此时最好将本参数设置为 true，由 IMKit 来缓存信息。
     */
    public static void setGroupUserInfoProvider(GroupUserInfoProvider userInfoProvider, boolean isCacheUserInfo) {
        if (RongContext.getInstance() != null) {
            RongContext.getInstance().setGroupUserInfoProvider(userInfoProvider, isCacheUserInfo);
        }
    }

    /**
     * <p>设置群组信息的提供者。</p>
     * <p>设置后，当 sdk 界面展示群组信息时，会回调 {@link io.rong.imkit.RongIM.GroupInfoProvider#getGroupInfo(String)}
     * 使用者只需要根据对应的 groupId 提供对应的群组信息。
     * 如果需要异步从服务器获取群组信息，使用者可以在此方法中发起异步请求，然后返回 null 信息。
     * 在异步请求结果返回后，根据返回的结果调用 {@link #refreshGroupInfoCache(Group)} 刷新信息。</p>
     *
     * @param groupInfoProvider 群组信息提供者。
     * @param isCacheGroupInfo  设置是否由 IMKit 来缓存用户信息。<br>
     *                          如果 App 提供的 GroupInfoProvider。
     *                          每次都需要通过网络请求群组数据，而不是将群组数据缓存到本地，会影响群组信息的加载速度；<br>
     *                          此时最好将本参数设置为 true，由 IMKit 来缓存群组信息。
     */
    public static void setGroupInfoProvider(GroupInfoProvider groupInfoProvider, boolean isCacheGroupInfo) {
        if (RongContext.getInstance() != null) {
            RongContext.getInstance().setGetGroupInfoProvider(groupInfoProvider, isCacheGroupInfo);
        }
    }

    /**
     * 刷新讨论组缓存数据，可用于讨论组修改名称后刷新讨论组内其他人员的缓存数据。
     *
     * @param discussion 需要更新的讨论组缓存数据。
     */
    public void refreshDiscussionCache(Discussion discussion) {

        if (discussion == null)
            return;

        RongUserInfoManager.getInstance().setDiscussionInfo(discussion);
    }

    /**
     * 刷新用户缓存数据。
     *
     * @param userInfo 需要更新的用户缓存数据。
     */
    public void refreshUserInfoCache(UserInfo userInfo) {

        if (userInfo == null)
            return;

        RongUserInfoManager.getInstance().setUserInfo(userInfo);
    }

    /**
     * 刷新、更改群组用户缓存数据。
     *
     * @param groupUserInfo 需要更新的群组用户缓存数据。
     */
    public void refreshGroupUserInfoCache(GroupUserInfo groupUserInfo) {

        if (groupUserInfo == null)
            return;

        RongUserInfoManager.getInstance().setGroupUserInfo(groupUserInfo);
    }

    /**
     * 刷新群组缓存数据。
     *
     * @param group 需要更新的群组缓存数据。
     */
    public void refreshGroupInfoCache(Group group) {
        if (group == null)
            return;

        RongUserInfoManager.getInstance().setGroupInfo(group);
    }

    /**
     * 设置发送消息的监听。
     *
     * @param listener 发送消息的监听。
     */
    public void setSendMessageListener(OnSendMessageListener listener) {
        if (RongContext.getInstance() == null)
            throw new ExceptionInInitializerError("RongCloud SDK not init");

        RongContext.getInstance().setOnSendMessageListener(listener);
    }

    /**
     * 会话界面操作的监听器。
     */
    public interface ConversationBehaviorListener {


        /**
         * 当点击用户头像后执行。
         *
         * @param context          上下文。
         * @param conversationType 会话类型。
         * @param user             被点击的用户的信息。
         * @return 如果用户自己处理了点击后的逻辑处理，则返回 true，否则返回 false，false 走融云默认处理方式。
         */
        boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo user);

        /**
         * 当长按用户头像后执行。
         *
         * @param context          上下文。
         * @param conversationType 会话类型。
         * @param user             被点击的用户的信息。
         * @return 如果用户自己处理了点击后的逻辑处理，则返回 true，否则返回 false，false 走融云默认处理方式。
         */
        boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo user);

        /**
         * 当点击消息时执行。
         *
         * @param context 上下文。
         * @param view    触发点击的 View。
         * @param message 被点击的消息的实体信息。
         * @return 如果用户自己处理了点击后的逻辑处理，则返回 true， 否则返回 false, false 走融云默认处理方式。
         */
        boolean onMessageClick(Context context, View view, Message message);

        /**
         * 当点击链接消息时执行。
         *
         * @param context 上下文。
         * @param link    被点击的链接。
         * @return 如果用户自己处理了点击后的逻辑处理，则返回 true， 否则返回 false, false 走融云默认处理方式。
         */
        boolean onMessageLinkClick(Context context, String link);

        /**
         * 当长按消息时执行。
         *
         * @param context 上下文。
         * @param view    触发点击的 View。
         * @param message 被长按的消息的实体信息。
         * @return 如果用户自己处理了长按后的逻辑处理，则返回 true，否则返回 false，false 走融云默认处理方式。
         */
        boolean onMessageLongClick(Context context, View view, Message message);

    }


    /**
     * 会话列表界面操作的监听器。
     */
    public interface ConversationListBehaviorListener {
        /**
         * 当点击会话头像后执行。
         *
         * @param context          上下文。
         * @param conversationType 会话类型。
         * @param targetId         被点击的用户id。
         * @return 如果用户自己处理了点击后的逻辑处理，则返回 true，否则返回 false，false 走融云默认处理方式。
         */
        boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String targetId);

        /**
         * 当长按会话头像后执行。
         *
         * @param context          上下文。
         * @param conversationType 会话类型。
         * @param targetId         被点击的用户id。
         * @return 如果用户自己处理了点击后的逻辑处理，则返回 true，否则返回 false，false 走融云默认处理方式。
         */
        boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String targetId);

        /**
         * 长按会话列表中的 item 时执行。
         *
         * @param context      上下文。
         * @param view         触发点击的 View。
         * @param conversation 长按时的会话条目。
         * @return 如果用户自己处理了长按会话后的逻辑处理，则返回 true， 否则返回 false，false 走融云默认处理方式。
         */
        boolean onConversationLongClick(Context context, View view, UIConversation conversation);

        /**
         * 点击会话列表中的 item 时执行。
         *
         * @param context      上下文。
         * @param view         触发点击的 View。
         * @param conversation 会话条目。
         * @return 如果用户自己处理了点击会话后的逻辑处理，则返回 true， 否则返回 false，false 走融云默认处理方式。
         */
        boolean onConversationClick(Context context, View view, UIConversation conversation);
    }

    /**
     * 用户信息的提供者。
     * <p/>
     * 如果在聊天中遇到的聊天对象是没有登录过的用户（即没有通过融云服务器鉴权过的），RongIM 是不知道用户信息的，RongIM 将调用此
     * Provider 获取用户信息。
     */
    public interface UserInfoProvider {
        /**
         * 获取用户信息。
         *
         * @param userId 用户 Id。
         * @return 用户信息。
         */
        UserInfo getUserInfo(String userId);
    }

    /**
     * GroupUserInfo提供者。
     */
    public interface GroupUserInfoProvider {
        /**
         * 获取GroupUserInfo。
         *
         * @param groupId 群组id。
         * @param userId  用户id。
         * @return GroupUserInfo。
         */
        GroupUserInfo getGroupUserInfo(String groupId, String userId);
    }


    /**
     * 群组信息的提供者。
     * <p/>
     * RongIM 本身不保存群组信息，如果在聊天中需要使用群组信息，RongIM 将调用此 Provider 获取群组信息。
     */
    public interface GroupInfoProvider {
        /**
         * 获取群组信息。
         *
         * @param groupId 群组 Id.
         * @return 群组信息。
         */
        Group getGroupInfo(String groupId);
    }

    /**
     * 启动好友选择界面的监听器
     */
    public interface OnSelectMemberListener {
        /**
         * 启动好友选择界面的接口。
         *
         * @param context          上下文。
         * @param conversationType 会话类型：PRIVATE / DISCUSSION.
         * @param targetId         该会话对应的 Id，私聊时为发送者 Id，讨论组时为讨论组 Id。
         */

        void startSelectMember(Context context, Conversation.ConversationType conversationType, String targetId);
    }

    /**
     * 获取自己发出的消息监听器。
     */
    public interface OnSendMessageListener {

        /**
         * 消息发送前监听器处理接口（是否发送成功可以从SentStatus属性获取）
         * 可以通过这个方法，过滤，修改发送出的消息。
         *
         * @param message 发送的消息实例。
         * @return 处理后的消息实例，注意：可以通过 return 的返回值，过滤消息
         * 当 return null 时，该消息不发送，界面也无显示
         * 也可以更改 message 内的消息内容，发送出的消息，就是更改后的。
         */
        Message onSend(Message message);


        /**
         * 消息发送后回调接口。
         *
         * @param message              消息实例。
         * @param sentMessageErrorCode 发送消息失败的状态码，消息发送成功 SentMessageErrorCode 为 null。
         */
        boolean onSent(Message message, SentMessageErrorCode sentMessageErrorCode);

    }

    /**
     * 发出的消息错误码。
     */
    public enum SentMessageErrorCode {

        /**
         * 未知错误。
         */
        UNKNOWN(-1, "Unknown error."),

        /**
         * 不在讨论组。
         */
        NOT_IN_DISCUSSION(21406, "not_in_discussion"),
        /**
         * 不在群组。
         */
        NOT_IN_GROUP(22406, "not_in_group"),
        /**
         * 群组禁言
         */
        FORBIDDEN_IN_GROUP(22408, "forbidden_in_group"),
        /**
         * 不在聊天室。
         */
        NOT_IN_CHATROOM(23406, "not_in_chatroom"),

        /**
         * 在黑名单中。
         */
        REJECTED_BY_BLACKLIST(405, "rejected by blacklist"),

        /**
         * 未关注此公众号
         */
        NOT_FOLLOWED(29106, "not followed");


        private int code;
        private String msg;

        /**
         * 构造函数。
         *
         * @param code 错误代码。
         * @param msg  错误消息。
         */
        SentMessageErrorCode(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        /**
         * 获取错误代码值。
         *
         * @return 错误代码值。
         */
        public int getValue() {
            return this.code;
        }

        /**
         * 获取错误消息。
         *
         * @return 错误消息。
         */
        public String getMessage() {
            return this.msg;
        }

        /**
         * 设置错误代码值。
         *
         * @param code 错误代码。
         * @return 错误代码枚举。
         */
        public static SentMessageErrorCode setValue(int code) {
            for (SentMessageErrorCode c : SentMessageErrorCode.values()) {
                if (code == c.getValue()) {
                    return c;
                }
            }

            RLog.d("RongIMClient", "SentMessageErrorCode---ErrorCode---code:" + code);

            return UNKNOWN;
        }
    }

    /**
     * 设置消息体内是否携带用户信息。
     *
     * @param state 是否携带用户信息，true 携带，false 不携带。
     */
    public void setMessageAttachedUserInfo(boolean state) {
        if (RongContext.getInstance() != null) {
            RongContext.getInstance().setUserInfoAttachedState(state);
        }
    }

    /**
     * 接收未读消息的监听器。
     */
    public interface OnReceiveUnreadCountChangedListener {
        void onMessageIncreased(int count);
    }

    /**
     * 设置接收未读消息的监听器。
     *
     * @param listener          接收未读消息消息的监听器。
     * @param conversationTypes 接收未读消息的会话类型。
     * @deprecated 该方法已废弃, 会造成内存泄漏, 请使用 {@link #addUnReadMessageCountChangedObserver(IUnReadMessageObserver, Conversation.ConversationType...)}
     * 和 {@link #removeUnReadMessageCountChangedObserver(IUnReadMessageObserver)}
     */
    @Deprecated
    public void setOnReceiveUnreadCountChangedListener(final OnReceiveUnreadCountChangedListener listener, Conversation.ConversationType... conversationTypes) {
        if (listener == null || conversationTypes == null || conversationTypes.length == 0) {
            RLog.w(TAG, "setOnReceiveUnreadCountChangedListener Illegal argument");
            return;
        }
        UnReadMessageManager.getInstance().addObserver(conversationTypes, new IUnReadMessageObserver() {
            @Override
            public void onCountChanged(int count) {
                listener.onMessageIncreased(count);
            }
        });
    }

    /**
     * 设置未读消息数变化监听器。
     * 注意:如果是在 activity 中设置,那么要在 activity 销毁时,调用 {@link #removeUnReadMessageCountChangedObserver(IUnReadMessageObserver)}
     * 否则会造成内存泄漏。
     *
     * @param observer          接收未读消息消息的监听器。
     * @param conversationTypes 接收未读消息的会话类型。
     */
    public void addUnReadMessageCountChangedObserver(final IUnReadMessageObserver observer, Conversation.ConversationType... conversationTypes) {
        if (observer == null || conversationTypes == null || conversationTypes.length == 0) {
            RLog.w(TAG, "addOnReceiveUnreadCountChangedListener Illegal argument");
            return;
        }
        UnReadMessageManager.getInstance().addObserver(conversationTypes, observer);
    }

    /**
     * 注销已注册的未读消息数变化监听器。
     *
     * @param observer 接收未读消息消息的监听器。
     */
    public void removeUnReadMessageCountChangedObserver(final IUnReadMessageObserver observer) {
        if (observer == null) {
            RLog.w(TAG, "removeOnReceiveUnreadCountChangedListener Illegal argument");
            return;
        }
        UnReadMessageManager.getInstance().removeObserver(observer);
    }

    /**
     * 启动公众号信息界面。
     *
     * @param context          应用上下文。
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。
     */
    public void startPublicServiceProfile(Context context, Conversation.ConversationType conversationType, String targetId) {

        if (context == null || conversationType == null || TextUtils.isEmpty(targetId))
            throw new IllegalArgumentException();

        if (RongContext.getInstance() == null)
            throw new ExceptionInInitializerError("RongCloud SDK not init");

        Uri uri = Uri.parse("rong://" + context.getApplicationInfo().processName).buildUpon()
                .appendPath("publicServiceProfile").appendPath(conversationType.getName().toLowerCase()).appendQueryParameter("targetId", targetId).build();

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 注册会话列表消息模板提供者。
     *
     * @param provider 会话列表模板提供者。
     */
    public void registerConversationTemplate(IContainerItemProvider.ConversationProvider provider) {
        if (RongContext.getInstance() != null) {
            if (provider == null)
                throw new IllegalArgumentException();

            RongContext.getInstance().registerConversationTemplate(provider);
        }
    }


    /**
     * 设置会话界面未读新消息是否展示 注:未读新消息大于1条即展示
     *
     * @param state true 展示，false 不展示。
     */
    public void enableNewComingMessageIcon(boolean state) {
        if (RongContext.getInstance() != null) {
            RongContext.getInstance().showNewMessageIcon(state);
        }
    }

    /**
     * 设置会话界面历史消息是否展示 注:历史消息大于10条即展示
     *
     * @param state true 展示，false 不展示。
     */
    public void enableUnreadMessageIcon(boolean state) {
        if (RongContext.getInstance() != null) {
            RongContext.getInstance().showUnreadMessageIcon(state);
        }
    }

    /**
     * 设置语音消息的最大长度
     *
     * @param sec 默认值是60s，有效值为不小于5秒，不大于60秒
     */
    public void setMaxVoiceDurationg(int sec) {
        AudioRecordManager.getInstance().setMaxVoiceDuration(sec);
    }

    /**
     * 获取连接状态。
     *
     * @return 连接状态枚举。
     */
    public RongIMClient.ConnectionStatusListener.ConnectionStatus getCurrentConnectionStatus() {
        return RongIMClient.getInstance().getCurrentConnectionStatus();
    }

    /**
     * 获取会话列表。
     *
     * @param callback 会话列表数据回调。
     *                 Conversation。
     */
    public void getConversationList(RongIMClient.ResultCallback<List<Conversation>> callback) {
        RongIMClient.getInstance().getConversationList(callback);
    }

    /**
     * 获取会话列表。
     *
     * @return 会话列表。
     * Conversation。
     * @deprecated 该方法废弃，请使用{@link #getConversationList(RongIMClient.ResultCallback)}
     */
    @Deprecated
    public List<Conversation> getConversationList() {
        return RongIMClient.getInstance().getConversationList();
    }

    /**
     * 根据会话类型，回调方式获取会话列表。
     *
     * @param callback 获取会话列表的回调。
     * @param types    会话类型。
     */
    public void getConversationList(RongIMClient.ResultCallback<List<Conversation>> callback, Conversation.ConversationType... types) {
        RongIMClient.getInstance().getConversationList(callback, types);
    }

    /**
     * 根据会话类型，获取会话列表。
     *
     * @param types 会话类型。
     * @return 返回会话列表。
     * @deprecated 该方法废弃，请使用{@link #getConversationList(RongIMClient.ResultCallback, Conversation.ConversationType...)}
     */
    @Deprecated
    public List<Conversation> getConversationList(Conversation.ConversationType... types) {
        return RongIMClient.getInstance().getConversationList(types);
    }

    /**
     * 根据不同会话类型的目标Id，回调方式获取某一会话信息。
     *
     * @param type     会话类型。
     * @param targetId 目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param callback 获取会话信息的回调。
     */
    public void getConversation(Conversation.ConversationType type, String targetId, RongIMClient.ResultCallback<Conversation> callback) {
        RongIMClient.getInstance().getConversation(type, targetId, callback);
    }

    /**
     * 获取某一会话信息。
     *
     * @param type     会话类型。
     * @param targetId 目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @return 会话信息。
     * @deprecated 该方法废弃，请使用{@link #getConversation(Conversation.ConversationType, String, RongIMClient.ResultCallback)}
     */
    @Deprecated
    public Conversation getConversation(Conversation.ConversationType type, String targetId) {
        return RongIMClient.getInstance().getConversation(type, targetId);
    }

    /**
     * 从会话列表中移除某一会话，但是不删除会话内的消息。
     * <p/>
     * 如果此会话中有新的消息，该会话将重新在会话列表中显示，并显示最近的历史消息。
     *
     * @param type     会话类型。
     * @param targetId 目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param callback 移除会话是否成功的回调。
     */
    public void removeConversation(final Conversation.ConversationType type, final String targetId, final RongIMClient.ResultCallback<Boolean> callback) {
        RongIMClient.getInstance().removeConversation(type, targetId, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean bool) {

                if (callback != null)
                    callback.onSuccess(bool);

                if (bool) {
                    RongContext.getInstance().getEventBus().post(new Event.ConversationRemoveEvent(type, targetId));
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                if (callback != null)
                    callback.onError(e);
            }
        });
    }

    /**
     * 从会话列表中移除某一会话，但是不删除会话内的消息。
     * <p/>
     * 如果此会话中有新的消息，该会话将重新在会话列表中显示，并显示最近的历史消息。
     *
     * @param type     会话类型。
     * @param targetId 目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @return 是否移除成功。
     * @deprecated 此方法废弃，请使用{@link #removeConversation(Conversation.ConversationType, String, RongIMClient.ResultCallback)}
     */
    @Deprecated
    public boolean removeConversation(Conversation.ConversationType type, String targetId) {
        boolean result = RongIMClient.getInstance().removeConversation(type, targetId);

        if (result) {
            RongContext.getInstance().getEventBus().post(new Event.ConversationRemoveEvent(type, targetId));
        }

        return result;
    }

    /**
     * 设置某一会话为置顶或者取消置顶，回调方式获取设置是否成功。
     *
     * @param type     会话类型。
     * @param id       目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param isTop    是否置顶。
     * @param callback 设置置顶或取消置顶是否成功的回调。
     */
    public void setConversationToTop(final Conversation.ConversationType type, final String id, final boolean isTop, final RongIMClient.ResultCallback<Boolean> callback) {
        RongIMClient.getInstance().setConversationToTop(type, id, isTop, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean bool) {
                if (callback != null)
                    callback.onSuccess(bool);

                if (bool)
                    RongContext.getInstance().getEventBus().post(new Event.ConversationTopEvent(type, id, isTop));
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                if (callback != null)
                    callback.onError(e);
            }
        });
    }

    /**
     * 设置某一会话为置顶或者取消置顶。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param isTop            是否置顶。
     * @return 是否设置成功。
     * @deprecated 此方法废弃，请使用{@link #setConversationToTop(Conversation.ConversationType, String, boolean, RongIMClient.ResultCallback)}。
     */
    @Deprecated
    public boolean setConversationToTop(Conversation.ConversationType conversationType, String targetId, boolean isTop) {
        boolean result = RongIMClient.getInstance().setConversationToTop(conversationType, targetId, isTop);

        if (result) {
            RongContext.getInstance().getEventBus().post(new Event.ConversationTopEvent(conversationType, targetId, isTop));
        }

        return result;
    }

    /**
     * 通过回调方式，获取所有未读消息数。
     *
     * @param callback 消息数的回调。
     */
    public void getTotalUnreadCount(final RongIMClient.ResultCallback<Integer> callback) {
        RongIMClient.getInstance().getTotalUnreadCount(new RongIMClient.ResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                if (callback != null)
                    callback.onSuccess(integer);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                if (callback != null)
                    callback.onError(e);
            }
        });
    }

    /**
     * 获取所有未读消息数。
     *
     * @return 未读消息数。
     * @deprecated 此方法废弃，请使用{@link #getTotalUnreadCount(RongIMClient.ResultCallback)}。
     */
    @Deprecated
    public int getTotalUnreadCount() {
        return RongIMClient.getInstance().getTotalUnreadCount();
    }

    /**
     * 根据会话类型的目标 Id,回调方式获取来自某用户（某会话）的未读消息数。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @param callback         未读消息数的回调
     */
    public void getUnreadCount(Conversation.ConversationType conversationType, String targetId, RongIMClient.ResultCallback<Integer> callback) {
        RongIMClient.getInstance().getUnreadCount(conversationType, targetId, callback);
    }

    /**
     * 获取来自某用户（某会话）的未读消息数。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @return 未读消息数。
     * @deprecated 此方法废弃，请使用{@link #getUnreadCount(Conversation.ConversationType, String, RongIMClient.ResultCallback)}。
     */
    @Deprecated
    public int getUnreadCount(Conversation.ConversationType conversationType, String targetId) {
        return RongIMClient.getInstance().getUnreadCount(conversationType, targetId);
    }


    /**
     * 回调方式获取某会话类型的未读消息数。
     *
     * @param callback          未读消息数的回调。
     * @param conversationTypes 会话类型。
     */
    public void getUnreadCount(RongIMClient.ResultCallback<Integer> callback, Conversation.ConversationType... conversationTypes) {
        RongIMClient.getInstance().getUnreadCount(callback, conversationTypes);
    }

    /**
     * 根据会话类型数组，回调方式获取某会话类型的未读消息数。
     *
     * @param conversationTypes 会话类型。
     * @return 未读消息数的回调。
     * @deprecated 此方法废弃，请使用{@link #getUnreadCount(RongIMClient.ResultCallback, Conversation.ConversationType...)}。
     */
    @Deprecated
    public int getUnreadCount(Conversation.ConversationType... conversationTypes) {
        return RongIMClient.getInstance().getUnreadCount(conversationTypes);
    }

    /**
     * 根据会话类型数组，回调方式获取某会话类型的未读消息数。
     *
     * @param conversationTypes 会话类型。
     * @param callback          未读消息数的回调。
     */
    public void getUnreadCount(Conversation.ConversationType[] conversationTypes, RongIMClient.ResultCallback<Integer> callback) {
        RongIMClient.getInstance().getUnreadCount(conversationTypes, callback);
    }

    /**
     * 获取最新消息记录。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。
     * @param count            要获取的消息数量。
     * @return 最新消息记录，按照时间顺序从新到旧排列。
     * @deprecated 此方法废弃，请使用{@link #getLatestMessages(Conversation.ConversationType, String, int, RongIMClient.ResultCallback)}。
     */
    @Deprecated
    public List<Message> getLatestMessages(Conversation.ConversationType conversationType, String targetId, int count) {
        return RongIMClient.getInstance().getLatestMessages(conversationType, targetId, count);
    }

    /**
     * 根据会话类型的目标 Id，回调方式获取最新的 N 条消息记录。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param count            要获取的消息数量。
     * @param callback         获取最新消息记录的回调，按照时间顺序从新到旧排列。
     */
    public void getLatestMessages(Conversation.ConversationType conversationType, String targetId, int count, RongIMClient.ResultCallback<List<Message>> callback) {
        RongIMClient.getInstance().getLatestMessages(conversationType, targetId, count, callback);
    }

    /**
     * 获取历史消息记录。
     *
     * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @param oldestMessageId  最后一条消息的 Id，获取此消息之前的 count 条消息，没有消息第一次调用应设置为:-1。
     * @param count            要获取的消息数量。
     * @return 历史消息记录，按照时间顺序从新到旧排列。
     * @deprecated 此方法废弃，请使用{@link #getHistoryMessages(Conversation.ConversationType, String, int, int, RongIMClient.ResultCallback)}。
     */
    @Deprecated
    public List<Message> getHistoryMessages(Conversation.ConversationType conversationType, String targetId, int oldestMessageId, int count) {
        return RongIMClient.getInstance().getHistoryMessages(conversationType, targetId, oldestMessageId, count);
    }

    /**
     * 获取历史消息记录。
     *
     * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @param objectName       消息类型标识。
     * @param oldestMessageId  最后一条消息的 Id，获取此消息之前的 count 条消息,没有消息第一次调用应设置为:-1。
     * @param count            要获取的消息数量。
     * @return 历史消息记录，按照时间顺序从新到旧排列。
     * @deprecated 此方法废弃，请使用{@link #getHistoryMessages(Conversation.ConversationType, String, String, int, int, RongIMClient.ResultCallback)}。
     */
    @Deprecated
    public List<Message> getHistoryMessages(Conversation.ConversationType conversationType, String targetId, String objectName, int oldestMessageId, int count) {
        return RongIMClient.getInstance().getHistoryMessages(conversationType, targetId, objectName, oldestMessageId, count);
    }

    /**
     * 根据会话类型的目标 Id，回调方式获取某消息类型标识的N条历史消息记录。
     *
     * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 。
     * @param objectName       消息类型标识。
     * @param oldestMessageId  最后一条消息的 Id，获取此消息之前的 count 条消息,没有消息第一次调用应设置为:-1。
     * @param count            要获取的消息数量。
     * @param callback         获取历史消息记录的回调，按照时间顺序从新到旧排列。
     */
    public void getHistoryMessages(Conversation.ConversationType conversationType, String targetId, String objectName, int oldestMessageId, int count, RongIMClient.ResultCallback<List<Message>> callback) {
        RongIMClient.getInstance().getHistoryMessages(conversationType, targetId, objectName, oldestMessageId, count, callback);
    }

    /**
     * 根据会话类型的目标 Id，回调方式获取N条历史消息记录。
     *
     * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @param oldestMessageId  最后一条消息的 Id，获取此消息之前的 count 条消息，没有消息第一次调用应设置为:-1。
     * @param count            要获取的消息数量。
     * @param callback         获取历史消息记录的回调，按照时间顺序从新到旧排列。
     */
    public void getHistoryMessages(Conversation.ConversationType conversationType, String targetId, int oldestMessageId, int count, RongIMClient.ResultCallback<List<Message>> callback) {
        RongIMClient.getInstance().getHistoryMessages(conversationType, targetId, oldestMessageId, count, callback);
    }


    /**
     * 根据会话类型的目标 Id，回调方式获取N条历史消息记录。
     *
     * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @param dataTime         从该时间点开始获取消息。即：消息中的 sendTime；第一次可传 0，获取最新 count 条。
     * @param count            要获取的消息数量，最多 20 条。
     * @param callback         获取历史消息记录的回调，按照时间顺序从新到旧排列。
     */
    public void getRemoteHistoryMessages(Conversation.ConversationType conversationType, String targetId, long dataTime, int count, RongIMClient.ResultCallback<List<Message>> callback) {
        RongIMClient.getInstance().getRemoteHistoryMessages(conversationType, targetId, dataTime, count, callback);
    }

    /**
     * 删除指定的一条或者一组消息。
     *
     * @param messageIds 要删除的消息 Id 数组。
     * @return 是否删除成功。
     * @deprecated 此方法废弃，请使用{@link #deleteMessages(int[], RongIMClient.ResultCallback)}。
     */
    @Deprecated
    public boolean deleteMessages(final int[] messageIds) {
        Boolean bool = RongIMClient.getInstance().deleteMessages(messageIds);

        if (bool)
            RongContext.getInstance().getEventBus().post(new Event.MessageDeleteEvent(messageIds));

        return bool;
    }

    /**
     * 删除指定的一条或者一组消息，回调方式获取是否删除成功。
     *
     * @param messageIds 要删除的消息 Id 数组。
     * @param callback   是否删除成功的回调。
     */
    public void deleteMessages(final int[] messageIds, final RongIMClient.ResultCallback<Boolean> callback) {
        RongIMClient.getInstance().deleteMessages(messageIds, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean bool) {
                if (bool)
                    RongContext.getInstance().getEventBus().post(new Event.MessageDeleteEvent(messageIds));

                if (callback != null)
                    callback.onSuccess(bool);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                if (callback != null)
                    callback.onError(e);
            }
        });
    }

    /**
     * <p>清除指定会话的消息</p>。
     * <p>此接口会删除指定会话中数据库的所有消息，同时，会清理数据库空间。
     * 如果数据库特别大，超过几百 M，调用该接口会有少许耗时。</p>
     *
     * @param conversationType 要删除的消息 Id 数组。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @param callback         是否删除成功的回调。
     */
    public void deleteMessages(final Conversation.ConversationType conversationType, final String targetId, final RongIMClient.ResultCallback<Boolean> callback) {
        RongIMClient.getInstance().deleteMessages(conversationType, targetId, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean bool) {
                if (bool)
                    RongContext.getInstance().getEventBus().post(new Event.MessagesClearEvent(conversationType, targetId));

                if (callback != null)
                    callback.onSuccess(bool);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                if (callback != null)
                    callback.onError(e);
            }
        });
    }

    /**
     * 删除指定的一条或者一组消息。会同时删除本地和远端消息。
     *
     * @param conversationType 会话类型。暂时不支持群组、讨论组和聊天室
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、客服 Id。
     * @param messages         要删除的消息数组, 数组大小不能超过100条。
     * @param callback         是否删除成功的回调。
     */
//    public void deleteMessages(Conversation.ConversationType conversationType, String targetId, final Message[] messages, final RongIMClient.OperationCallback callback) {
//        RongIMClient.getInstance().deleteMessages(conversationType, targetId, messages, new RongIMClient.OperationCallback() {
//            @Override
//            public void onSuccess() {
//                int[] messageIds = new int[messages.length];
//                for (int i = 0; i < messages.length; i++) {
//                    messageIds[i] = messages[i].getMessageId();
//                }
//                RongContext.getInstance().getEventBus().post(new Event.MessageDeleteEvent(messageIds));
//                if (callback != null) {
//                    callback.onSuccess();
//                }
//            }
//
//            @Override
//            public void onError(RongIMClient.ErrorCode errorCode) {
//                if (callback != null) {
//                    callback.onError(errorCode);
//                }
//            }
//        });
//    }

    /**
     * 清空某一会话的所有聊天消息记录。
     *
     * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @return 是否清空成功。
     * @deprecated 此方法废弃，请使用{@link #clearMessages(Conversation.ConversationType, String, RongIMClient.ResultCallback)}。
     */
    @Deprecated
    public boolean clearMessages(Conversation.ConversationType conversationType, String targetId) {
        boolean bool = RongIMClient.getInstance().clearMessages(conversationType, targetId);

        if (bool)
            RongContext.getInstance().getEventBus().post(new Event.MessagesClearEvent(conversationType, targetId));

        return bool;
    }

    /**
     * 根据会话类型，清空某一会话的所有聊天消息记录,回调方式获取清空是否成功。
     *
     * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @param callback         清空是否成功的回调。
     */
    public void clearMessages(final Conversation.ConversationType conversationType, final String targetId, final RongIMClient.ResultCallback<Boolean> callback) {
        RongIMClient.getInstance().clearMessages(conversationType, targetId, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean bool) {
                if (bool)
                    RongContext.getInstance().getEventBus().post(new Event.MessagesClearEvent(conversationType, targetId));

                if (callback != null)
                    callback.onSuccess(bool);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                if (callback != null)
                    callback.onError(e);
            }
        });
    }

    /**
     * 清除消息未读状态。
     *
     * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @return 是否清空成功。
     * @deprecated 此方法废弃，请使用{@link #clearMessagesUnreadStatus(Conversation.ConversationType, String, RongIMClient.ResultCallback)}。
     */
    @Deprecated
    public boolean clearMessagesUnreadStatus(Conversation.ConversationType conversationType, String targetId) {
        boolean result = RongIMClient.getInstance().clearMessagesUnreadStatus(conversationType, targetId);
        if (result) {
            RongContext.getInstance().getEventBus().post(new Event.ConversationUnreadEvent(conversationType, targetId));
        }

        return result;
    }

    /**
     * 根据会话类型，清除目标 Id 的消息未读状态，回调方式获取清除是否成功。
     *
     * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @param callback         清除是否成功的回调。
     */
    public void clearMessagesUnreadStatus(final Conversation.ConversationType conversationType, final String targetId, final RongIMClient.ResultCallback<Boolean> callback) {
        RongIMClient.getInstance().clearMessagesUnreadStatus(conversationType, targetId, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean bool) {
                if (callback != null)
                    callback.onSuccess(bool);
                RongContext.getInstance().getEventBus().post(new Event.ConversationUnreadEvent(conversationType, targetId));
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                if (callback != null)
                    callback.onError(e);
            }
        });
    }

    /**
     * 设置消息的附加信息，此信息只保存在本地。
     *
     * @param messageId 消息 Id。
     * @param value     消息附加信息，最大 1024 字节。
     * @return 是否设置成功。
     * @deprecated 此方法废弃，请使用{@link #setMessageExtra(int, String, RongIMClient.ResultCallback)}。
     */
    @Deprecated
    public boolean setMessageExtra(int messageId, String value) {
        return RongIMClient.getInstance().setMessageExtra(messageId, value);
    }

    /**
     * 设置消息的附加信息，此信息只保存在本地，回调方式获取设置是否成功。
     *
     * @param messageId 消息 Id。
     * @param value     消息附加信息，最大 1024 字节。
     * @param callback  是否设置成功的回调。
     */
    public void setMessageExtra(int messageId, String value, RongIMClient.ResultCallback<Boolean> callback) {
        RongIMClient.getInstance().setMessageExtra(messageId, value, callback);
    }

    /**
     * 设置接收到的消息状态。
     *
     * @param messageId      消息 Id。
     * @param receivedStatus 接收到的消息状态。
     * @return 是否设置成功。
     * @deprecated 此方法废弃，请使用{@link #setMessageReceivedStatus(int, Message.ReceivedStatus, RongIMClient.ResultCallback)}。
     */
    @Deprecated
    public boolean setMessageReceivedStatus(int messageId, Message.ReceivedStatus receivedStatus) {
        return RongIMClient.getInstance().setMessageReceivedStatus(messageId, receivedStatus);
    }

    /**
     * 根据消息 Id，设置接收到的消息状态，回调方式获取设置是否成功。
     *
     * @param messageId      消息 Id。
     * @param receivedStatus 接收到的消息状态。
     * @param callback       是否设置成功的回调。
     */
    public void setMessageReceivedStatus(int messageId, Message.ReceivedStatus receivedStatus, RongIMClient.ResultCallback<Boolean> callback) {
        RongIMClient.getInstance().setMessageReceivedStatus(messageId, receivedStatus, callback);
    }

    /**
     * 设置发送的消息状态。
     *
     * @param messageId  消息 Id。
     * @param sentStatus 发送的消息状态。
     * @return 是否设置成功。
     * @deprecated 此方法废弃，请使用{@link #setMessageSentStatus(int, Message.SentStatus, RongIMClient.ResultCallback)}。
     */
    @Deprecated
    public boolean setMessageSentStatus(int messageId, Message.SentStatus sentStatus) {
        boolean result = RongIMClient.getInstance().setMessageSentStatus(messageId, sentStatus);

        if (result)
            RongContext.getInstance().getEventBus().post(new Event.MessageSentStatusEvent(messageId, sentStatus));

        return result;
    }

    /**
     * 根据消息 Id，设置发送的消息状态，回调方式获取设置是否成功。
     *
     * @param messageId  消息 Id。
     * @param sentStatus 发送的消息状态。
     * @param callback   是否设置成功的回调。
     */
    public void setMessageSentStatus(final int messageId, final Message.SentStatus sentStatus, final RongIMClient.ResultCallback<Boolean> callback) {
        RongIMClient.getInstance().setMessageSentStatus(messageId, sentStatus, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean bool) {
                if (callback != null)
                    callback.onSuccess(bool);

                if (bool)
                    RongContext.getInstance().getEventBus().post(new Event.MessageSentStatusEvent(messageId, sentStatus));
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                if (callback != null)
                    callback.onError(e);
            }
        });
    }

    /**
     * 获取某一会话的文字消息草稿。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @return 草稿的文字内容。
     * @deprecated 此方法废弃，请使用{@link #getTextMessageDraft(Conversation.ConversationType, String, RongIMClient.ResultCallback)}。
     */
    @Deprecated
    public String getTextMessageDraft(Conversation.ConversationType conversationType, String targetId) {
        return RongIMClient.getInstance().getTextMessageDraft(conversationType, targetId);
    }

    /**
     * 保存文字消息草稿。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param content          草稿的文字内容。
     * @return 是否保存成功。
     * @deprecated 此方法废弃，请使用{@link #saveTextMessageDraft(Conversation.ConversationType, String, String, RongIMClient.ResultCallback)}。
     */
    @Deprecated
    public boolean saveTextMessageDraft(Conversation.ConversationType conversationType, String targetId, String content) {
        return RongIMClient.getInstance().saveTextMessageDraft(conversationType, targetId, content);
    }

    /**
     * 清除某一会话的文字消息草稿。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @return 是否清除成功。
     * @deprecated 此方法废弃，请使用{@link #clearTextMessageDraft(Conversation.ConversationType, String, RongIMClient.ResultCallback)}。
     */
    @Deprecated
    public boolean clearTextMessageDraft(Conversation.ConversationType conversationType, String targetId) {
        return RongIMClient.getInstance().clearTextMessageDraft(conversationType, targetId);
    }

    /**
     * 根据会话类型，获取某一会话的文字消息草稿。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param callback         获取草稿文字内容的回调。
     */
    public void getTextMessageDraft(Conversation.ConversationType conversationType, String targetId, RongIMClient.ResultCallback<String> callback) {
        RongIMClient.getInstance().getTextMessageDraft(conversationType, targetId, callback);
    }

    /**
     * 保存文字消息草稿，回调方式获取保存是否成功。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param content          草稿的文字内容。
     * @param callback         是否保存成功的回调。
     */
    public void saveTextMessageDraft(Conversation.ConversationType conversationType, String targetId, String content, RongIMClient.ResultCallback<Boolean> callback) {
        RongIMClient.getInstance().saveTextMessageDraft(conversationType, targetId, content, callback);
    }

    /**
     * 清除某一会话的文字消息草稿，回调方式获取清除是否成功。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param callback         是否清除成功的回调。
     */
    public void clearTextMessageDraft(Conversation.ConversationType conversationType, String targetId, RongIMClient.ResultCallback<Boolean> callback) {
        RongIMClient.getInstance().clearTextMessageDraft(conversationType, targetId, callback);
    }

    /**
     * 获取讨论组信息和设置。
     *
     * @param discussionId 讨论组 Id。
     * @param callback     获取讨论组的回调。
     */
    public void getDiscussion(String discussionId, RongIMClient.ResultCallback<Discussion> callback) {
        RongIMClient.getInstance().getDiscussion(discussionId, callback);
    }

    /**
     * 设置讨论组名称。
     *
     * @param discussionId 讨论组 Id。
     * @param name         讨论组名称。
     * @param callback     设置讨论组的回调。
     */
    public void setDiscussionName(final String discussionId, final String name, final RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().setDiscussionName(discussionId, name, new RongIMClient.OperationCallback() {

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                if (callback != null) {
                    callback.onError(errorCode);
                }
            }

            @Override
            public void onSuccess() {
                if (callback != null) {
                    RongUserInfoManager.getInstance().setDiscussionInfo(new Discussion(discussionId, name));
                    callback.onSuccess();
                }
            }
        });
    }

    /**
     * 创建讨论组。
     *
     * @param name       讨论组名称，如：当前所有成员的名字的组合。
     * @param userIdList 讨论组成员 Id 列表。
     * @param callback   创建讨论组成功后的回调。
     */
    public void createDiscussion(final String name, final List<String> userIdList, final RongIMClient.CreateDiscussionCallback callback) {
        RongIMClient.getInstance().createDiscussion(name, userIdList, new RongIMClient.CreateDiscussionCallback() {
            @Override
            public void onSuccess(String discussionId) {
                RongContext.getInstance().getEventBus().post(new Event.CreateDiscussionEvent(discussionId, name, userIdList));
                if (callback != null)
                    callback.onSuccess(discussionId);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                if (callback != null)
                    callback.onError(errorCode);
            }
        });
    }

    /**
     * 添加一名或者一组用户加入讨论组。
     *
     * @param discussionId 讨论组 Id。
     * @param userIdList   邀请的用户 Id 列表。
     * @param callback     执行操作的回调。
     */
    public void addMemberToDiscussion(final String discussionId, final List<String> userIdList, final RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().addMemberToDiscussion(discussionId, userIdList, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                RongContext.getInstance().getEventBus().post(new Event.AddMemberToDiscussionEvent(discussionId, userIdList));

                if (callback != null)
                    callback.onSuccess();

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                if (callback != null)
                    callback.onError(errorCode);
            }
        });
    }

    /**
     * <p>供创建者将某用户移出讨论组。</p>
     * <p>
     * 如果当前登陆用户不是此讨论组的创建者并且此讨论组没有开放加人权限，则会返回错误 {@link RongIMClient.ErrorCode}。
     * 不能使用此接口将自己移除，否则会返回错误 {@link RongIMClient.ErrorCode}。
     * 如果您需要退出该讨论组，可以使用 {@link #quitDiscussion(String, RongIMClient.OperationCallback)} 方法。
     * </p>
     *
     * @param discussionId 讨论组 Id。
     * @param userId       用户 Id。
     * @param callback     执行操作的回调。{@link io.rong.imlib.RongIMClient.OperationCallback}。
     */
    public void removeMemberFromDiscussion(final String discussionId, final String userId, final RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().removeMemberFromDiscussion(discussionId, userId, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                RongContext.getInstance().getEventBus().post(new Event.RemoveMemberFromDiscussionEvent(discussionId, userId));

                if (callback != null)
                    callback.onSuccess();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                if (callback != null)
                    callback.onError(errorCode);
            }
        });
    }

    /**
     * 退出当前用户所在的某讨论组。
     *
     * @param discussionId 讨论组 Id。
     * @param callback     执行操作的回调。
     */
    public void quitDiscussion(final String discussionId, final RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().quitDiscussion(discussionId, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                RongContext.getInstance().getEventBus().post(new Event.QuitDiscussionEvent(discussionId));

                if (callback != null)
                    callback.onSuccess();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

                if (callback != null)
                    callback.onError(errorCode);
            }
        });
    }

    /**
     * 向本地会话中插入一条消息。这条消息只是插入本地会话，不会实际发送给服务器和对方。该消息不一定插入本地数据库，是否入库由消息的属性决定。
     *
     * @param type         会话类型。
     * @param targetId     目标会话Id。比如私人会话时，是对方的id； 群组会话时，是群id; 讨论组会话时，则为该讨论组的id.
     * @param senderUserId 发送用户 Id。如果是模拟本人插入的消息，则该id设置为当前登录用户即可。如果要模拟对方插入消息，则该id需要设置为对方的id.
     *                     另外插入消息的时候，界面上一般不需要有发送状态显示（如正在发送，发送失败等），这时候只需要在该消息的provider
     *                     注解中设置{@link ProviderTag#showProgress()}值为false即可。
     * @param content      消息内容。如{@link TextMessage} {@link ImageMessage}等。
     * @param sentTime     消息的发送时间。
     * @param callback     获得消息发送实体的回调。
     */
    public void insertMessage(Conversation.ConversationType type, String targetId, String senderUserId, MessageContent content, final long sentTime, final RongIMClient.ResultCallback<Message> callback) {
        final MessageTag tag = content.getClass().getAnnotation(MessageTag.class);

        if (tag != null && (tag.flag() & MessageTag.ISPERSISTED) == MessageTag.ISPERSISTED) {

            RongIMClient.getInstance().insertMessage(type, targetId, senderUserId, content, sentTime, new RongIMClient.ResultCallback<Message>() {
                @Override
                public void onSuccess(Message message) {

                    if (callback != null)
                        callback.onSuccess(message);

                    RongContext.getInstance().getEventBus().post(message);
                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {

                    if (callback != null)
                        callback.onError(e);

                    RongContext.getInstance().getEventBus().post(e);
                }
            });
        } else {
            RLog.e(TAG, "insertMessage Message is missing MessageTag.ISPERSISTED");
        }
    }

    /**
     * 向本地会话中插入一条消息。这条消息只是插入本地会话，不会实际发送给服务器和对方。该消息不一定插入本地数据库，是否入库由消息的属性决定。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标会话Id。比如私人会话时，是对方的id； 群组会话时，是群id; 讨论组会话时，则为该讨论组的id.
     * @param senderUserId     发送用户 Id。如果是模拟本人插入的消息，则该id设置为当前登录用户即可。如果要模拟对方插入消息，则该id需要设置为对方的id.
     *                         另外插入消息的时候，界面上一般不需要有发送状态显示（如正在发送，发送失败等），这时候只需要在该消息的provider
     *                         注解中设置{@link ProviderTag#showProgress()}值为false即可。
     * @param content          消息内容。如{@link TextMessage} {@link ImageMessage}等。
     * @param callback         获得消息发送实体的回调。
     */
    public void insertMessage(Conversation.ConversationType conversationType, String targetId, String senderUserId, MessageContent content, final RongIMClient.ResultCallback<Message> callback) {
        insertMessage(conversationType, targetId, senderUserId, content, System.currentTimeMillis(), callback);
    }

    /**
     * 模拟消息。
     *
     * @param type         会话类型。
     * @param targetId     目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param senderUserId 发送用户 Id。
     * @param content      消息内容。
     * @return Message
     * @deprecated 此方法废弃，请使用{@link #insertMessage(Conversation.ConversationType, String, String, MessageContent, RongIMClient.ResultCallback)}。
     */
    @Deprecated
    public Message insertMessage(Conversation.ConversationType type, String targetId, String senderUserId, MessageContent content) {
        MessageTag tag = content.getClass().getAnnotation(MessageTag.class);

        Message message;

        if (tag != null && (tag.flag() & MessageTag.ISPERSISTED) == MessageTag.ISPERSISTED) {
            message = RongIMClient.getInstance().insertMessage(type, targetId, senderUserId, content);
        } else {
            message = Message.obtain(targetId, type, content);
            RLog.e(TAG, "insertMessage Message is missing MessageTag.ISPERSISTED");
        }

        RongContext.getInstance().getEventBus().post(message);

        return message;
    }

    /**
     * 发送消息。
     *
     * @param type        会话类型。
     * @param targetId    目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param content     消息内容。
     * @param pushContent 当下发 push 消息时，在通知栏里会显示这个字段。
     *                    如果发送的是自定义消息，该字段必须填写，否则无法收到 push 消息。
     *                    如果发送 sdk 中默认的消息类型，例如 RC:TxtMsg, RC:VcMsg, RC:ImgMsg，则不需要填写，默认已经指定。
     * @param pushData    push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link io.rong.push.notification.PushNotificationMessage#getPushData()} 方法获取。
     * @param callback    发送消息的回调。
     * @return Message
     * @deprecated 此方法废弃，请使用{@link #sendMessage(Message, String, String, IRongCallback.ISendMessageCallback)}。
     */
    @Deprecated
    public Message sendMessage(Conversation.ConversationType type, String targetId, MessageContent content, String pushContent, String pushData, final RongIMClient.SendMessageCallback callback) {
        final RongIMClient.ResultCallback.Result<Message> result = new RongIMClient.ResultCallback.Result<>();

        Message messageTemp = Message.obtain(targetId, type, content);

        Message temp = filterSendMessage(messageTemp);
        if (temp == null)
            return null;

        if (temp != messageTemp)
            messageTemp = temp;

        content = messageTemp.getContent();

        content = setMessageAttachedUserInfo(content);

        final Message message = RongIMClient.getInstance().sendMessage(type, targetId, content, pushContent, pushData, new RongIMClient.SendMessageCallback() {
            @Override
            public void onSuccess(Integer messageId) {
                if (result.t == null)
                    return;
                result.t.setSentStatus(Message.SentStatus.SENT);
                long tt = RongIMClient.getInstance().getSendTimeByMessageId(messageId);
                if (tt != 0) {
                    result.t.setSentTime(tt);
                }
                filterSentMessage(result.t, null);
//                mContext.getEventBus().post(result.t);

                if (callback != null)
                    callback.onSuccess(messageId);
            }

            @Override
            public void onError(Integer messageId, RongIMClient.ErrorCode errorCode) {
                if (result.t == null)
                    return;
                result.t.setSentStatus(Message.SentStatus.FAILED);
                filterSentMessage(result.t, errorCode);
//                mContext.getEventBus().post(new Event.OnMessageSendErrorEvent(result.t, errorCode));

                if (callback != null)
                    callback.onError(messageId, errorCode);
            }
        });

        MessageTag tag = content.getClass().getAnnotation(MessageTag.class);

        if (tag != null && (tag.flag() & MessageTag.ISPERSISTED) == MessageTag.ISPERSISTED) {
            RongContext.getInstance().getEventBus().post(message);
        }
        result.t = message;

        return message;
    }

    /**
     * <p>根据会话类型，发送消息。</p>
     *
     * @param type           会话类型。
     * @param targetId       目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param content        消息内容，例如 {@link TextMessage}, {@link ImageMessage}。
     * @param pushContent    当下发 push 消息时，在通知栏里会显示这个字段。
     *                       如果发送的是自定义消息，该字段必须填写，否则无法收到 push 消息。
     *                       如果发送 sdk 中默认的消息类型，例如 RC:TxtMsg, RC:VcMsg, RC:ImgMsg，则不需要填写，默认已经指定。
     * @param pushData       push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link io.rong.push.notification.PushNotificationMessage#getPushData()} 方法获取。
     * @param callback       发送消息的回调，消息经网络发送成功或失败，通过此回调返回。
     * @param resultCallback 获取发送消息实体的回调，消息存储数据库后，通过此回调返回。
     * @deprecated 此方法废弃，请使用{@link #sendMessage(Message, String, String, IRongCallback.ISendMessageCallback)}。
     */
    public void sendMessage(Conversation.ConversationType type, String targetId, MessageContent
            content, String pushContent, final String pushData, final RongIMClient.SendMessageCallback callback,
                            final RongIMClient.ResultCallback<Message> resultCallback) {
        final RongIMClient.ResultCallback.Result<Message> result = new RongIMClient.ResultCallback.Result<>();

        Message message = Message.obtain(targetId, type, content);

        Message temp = filterSendMessage(message);
        if (temp == null)
            return;

        if (temp != message)
            message = temp;

        content = message.getContent();

        content = setMessageAttachedUserInfo(content);
        RongIMClient.getInstance().sendMessage(type, targetId, content, pushContent, pushData, new RongIMClient.SendMessageCallback() {
            @Override
            public void onSuccess(Integer messageId) {
                if (result.t == null)
                    return;
                result.t.setSentStatus(Message.SentStatus.SENT);
                long tt = RongIMClient.getInstance().getSendTimeByMessageId(messageId);
                if (tt != 0) {
                    result.t.setSentTime(tt);
                }
                filterSentMessage(result.t, null);

//                mContext.getEventBus().post(result.t);

                if (callback != null)
                    callback.onSuccess(messageId);
            }

            @Override
            public void onError(Integer messageId, RongIMClient.ErrorCode errorCode) {
                if (result.t == null)
                    return;
                result.t.setSentStatus(Message.SentStatus.FAILED);
                filterSentMessage(result.t, errorCode);

//                mContext.getEventBus().post(new Event.OnMessageSendErrorEvent(result.t, errorCode));

                if (callback != null)
                    callback.onError(messageId, errorCode);

            }
        }, new RongIMClient.ResultCallback<Message>() {
            @Override
            public void onSuccess(Message message) {
                MessageTag tag = message.getContent().getClass().getAnnotation(MessageTag.class);

                if (tag != null && (tag.flag() & MessageTag.ISPERSISTED) == MessageTag.ISPERSISTED) {
                    RongContext.getInstance().getEventBus().post(message);
                }

                result.t = message;

                if (resultCallback != null)
                    resultCallback.onSuccess(message);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                RongContext.getInstance().getEventBus().post(e);

                if (resultCallback != null)
                    resultCallback.onError(e);
            }
        });
    }

    /**
     * <p>发送消息。</p>
     *
     * @param message        发送消息的实体。
     * @param pushContent    当下发 push 消息时，在通知栏里会显示这个字段。
     *                       如果发送的是自定义消息，该字段必须填写，否则无法收到 push 消息。
     *                       如果发送 sdk 中默认的消息类型，例如 RC:TxtMsg, RC:VcMsg, RC:ImgMsg，则不需要填写，默认已经指定。
     * @param pushData       push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link io.rong.push.notification.PushNotificationMessage#getPushData()} 方法获取。
     * @param callback       发送消息的回调，消息经网络发送成功或失败，通过此回调返回。
     * @param resultCallback 获取发送消息实体的回调，消息存储数据库后，通过此回调返回。
     * @deprecated 此方法废弃，请使用{@link #sendMessage(Message, String, String, IRongCallback.ISendMessageCallback)}。
     */
    public void sendMessage(Message message, String pushContent, final String pushData,
                            final RongIMClient.SendMessageCallback callback, final RongIMClient.ResultCallback<Message> resultCallback) {
        final RongIMClient.ResultCallback.Result<Message> result = new RongIMClient.ResultCallback.Result<>();

        final Message temp = filterSendMessage(message);
        if (temp == null)
            return;

        if (temp != message)
            message = temp;

        message.setContent(setMessageAttachedUserInfo(message.getContent()));

        RongIMClient.getInstance().sendMessage(message, pushContent, pushData, new RongIMClient.SendMessageCallback() {
            @Override
            public void onSuccess(Integer messageId) {
                if (result.t == null)
                    return;
                result.t.setSentStatus(Message.SentStatus.SENT);
                long tt = RongIMClient.getInstance().getSendTimeByMessageId(messageId);
                if (tt != 0) {
                    result.t.setSentTime(tt);
                }
                filterSentMessage(result.t, null);
//                mContext.getEventBus().post(result.t);
                if (callback != null)
                    callback.onSuccess(messageId);
            }

            @Override
            public void onError(Integer messageId, RongIMClient.ErrorCode errorCode) {

                if (result.t == null)
                    return;

                result.t.setSentStatus(Message.SentStatus.FAILED);
                filterSentMessage(result.t, errorCode);

//                mContext.getEventBus().post(result.t);
//                mContext.getEventBus().post(new Event.OnMessageSendErrorEvent(result.t, errorCode));
                if (callback != null)
                    callback.onError(messageId, errorCode);
            }
        }, new RongIMClient.ResultCallback<Message>() {
            @Override
            public void onSuccess(Message message) {

                result.t = message;
                MessageTag tag = message.getContent().getClass().getAnnotation(MessageTag.class);

                if (tag != null && (tag.flag() & MessageTag.ISPERSISTED) == MessageTag.ISPERSISTED) {
                    RongContext.getInstance().getEventBus().post(message);
                }

                if (resultCallback != null)
                    resultCallback.onSuccess(message);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                RongContext.getInstance().getEventBus().post(e);
                if (resultCallback != null)
                    resultCallback.onError(e);
            }
        });
    }

    /**
     * 发送消息，返回发送的消息实体。
     *
     * @param message     发送消息的实体。
     * @param pushContent 当下发 push 消息时，在通知栏里会显示这个字段。
     *                    如果发送的是自定义消息，该字段必须填写，否则无法收到 push 消息。
     *                    如果发送 sdk 中默认的消息类型，例如 RC:TxtMsg, RC:VcMsg, RC:ImgMsg，则不需要填写，默认已经指定。
     * @param pushData    push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link io.rong.push.notification.PushNotificationMessage#getPushData()} 方法获取。
     * @param callback    发送消息的回调。
     * @return 发送的消息实体。
     * @deprecated 此方法废弃，请使用{@link #sendMessage(Message, String, String, IRongCallback.ISendMessageCallback)}。
     */
    @Deprecated
    public Message sendMessage(Message message, String pushContent, final String pushData,
                               final RongIMClient.SendMessageCallback callback) {
        final RongIMClient.ResultCallback.Result<Message> result = new RongIMClient.ResultCallback.Result<>();

        final Message temp = filterSendMessage(message);

        if (temp == null)
            return null;

        if (temp != message)
            message = temp;

        message.setContent(setMessageAttachedUserInfo(message.getContent()));

        Message msg = RongIMClient.getInstance().sendMessage(message, pushContent, pushData, new RongIMClient.SendMessageCallback() {
            @Override
            public void onSuccess(Integer messageId) {
                if (result.t == null)
                    return;
                result.t.setSentStatus(Message.SentStatus.SENT);
                long tt = RongIMClient.getInstance().getSendTimeByMessageId(messageId);
                if (tt != 0) {
                    result.t.setSentTime(tt);
                }
                filterSentMessage(result.t, null);
//                mContext.getEventBus().post(result.t);
                if (callback != null)
                    callback.onSuccess(messageId);
            }

            @Override
            public void onError(Integer messageId, RongIMClient.ErrorCode errorCode) {
                if (result.t == null)
                    return;
                result.t.setSentStatus(Message.SentStatus.FAILED);
                filterSentMessage(result.t, errorCode);

//                mContext.getEventBus().post(new Event.OnMessageSendErrorEvent(result.t, errorCode));

                if (callback != null)
                    callback.onError(messageId, errorCode);
            }
        });

        MessageTag tag = message.getContent().getClass().getAnnotation(MessageTag.class);

        if (tag != null && (tag.flag() & MessageTag.ISPERSISTED) == MessageTag.ISPERSISTED) {
            EventBus.getDefault().post(msg);
        }
        result.t = msg;

        return msg;
    }

    /**
     * <p>发送消息。
     * 通过 {@link io.rong.imlib.IRongCallback.ISendMessageCallback}
     * 中的方法回调发送的消息状态及消息体。</p>
     *
     * @param message     将要发送的消息体。
     * @param pushContent 当下发 push 消息时，在通知栏里会显示这个字段。
     *                    如果发送的是自定义消息，该字段必须填写，否则无法收到 push 消息。
     *                    如果发送 sdk 中默认的消息类型，例如 RC:TxtMsg, RC:VcMsg, RC:ImgMsg，则不需要填写，默认已经指定。
     * @param pushData    push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link io.rong.push.notification.PushNotificationMessage#getPushData()} 方法获取。
     * @param callback    发送消息的回调，参考 {@link io.rong.imlib.IRongCallback.ISendMessageCallback}。
     */
    public void sendMessage(Message message, String pushContent, final String pushData, final IRongCallback.ISendMessageCallback callback) {
        final Message filterMsg = filterSendMessage(message);
        if (filterMsg == null) {
            RLog.w(TAG, "sendMessage: 因在 onSend 中消息被过滤为 null，取消发送。");
            return;
        }
        if (filterMsg != message) {
            message = filterMsg;
        }
        message.setContent(setMessageAttachedUserInfo(message.getContent()));
        RongIMClient.getInstance().sendMessage(message, pushContent, pushData, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {
                MessageTag tag = message.getContent().getClass().getAnnotation(MessageTag.class);
                if (tag != null && (tag.flag() & MessageTag.ISPERSISTED) == MessageTag.ISPERSISTED) {
                    RongContext.getInstance().getEventBus().post(message);
                }

                if (callback != null) {
                    callback.onAttached(message);
                }
            }

            @Override
            public void onSuccess(Message message) {
                filterSentMessage(message, null);
                if (callback != null) {
                    callback.onSuccess(message);
                }
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                filterSentMessage(message, errorCode);
                if (callback != null) {
                    callback.onError(message, errorCode);
                }
            }
        });
    }

    /**
     * <p>发送地理位置消息。并同时更新界面。</p>
     * <p>发送前构造 {@link Message} 消息实体，消息实体中的 content 必须为 {@link LocationMessage}, 否则返回失败。</p>
     * <p>其中的缩略图地址 scheme 只支持 file:// 和 http:// 其他暂不支持。</p>
     *
     * @param message             消息实体。
     * @param pushContent         当下发 push 消息时，在通知栏里会显示这个字段。
     *                            如果发送的是自定义消息，该字段必须填写，否则无法收到 push 消息。
     *                            如果发送 sdk 中默认的消息类型，例如 RC:TxtMsg, RC:VcMsg, RC:ImgMsg，则不需要填写，默认已经指定。
     * @param pushData            push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link io.rong.push.notification.PushNotificationMessage#getPushData()} 方法获取。
     * @param sendMessageCallback 发送消息的回调，参考 {@link io.rong.imlib.IRongCallback.ISendMessageCallback}。
     */
    public void sendLocationMessage(Message message, String pushContent, final String pushData, final IRongCallback.ISendMessageCallback sendMessageCallback) {
        final Message filterMsg = filterSendMessage(message);
        if (filterMsg == null) {
            RLog.w(TAG, "sendLocationMessage: 因在 onSend 中消息被过滤为 null，取消发送。");
            return;
        }
        if (filterMsg != message) {
            message = filterMsg;
        }
        message.setContent(setMessageAttachedUserInfo(message.getContent()));
        RongIMClient.getInstance().sendLocationMessage(message, pushContent, pushData, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {
                MessageTag tag = message.getContent().getClass().getAnnotation(MessageTag.class);
                if (tag != null && (tag.flag() & MessageTag.ISPERSISTED) == MessageTag.ISPERSISTED) {
                    RongContext.getInstance().getEventBus().post(message);
                }

                if (sendMessageCallback != null) {
                    sendMessageCallback.onAttached(message);
                }
            }

            @Override
            public void onSuccess(Message message) {
                filterSentMessage(message, null);
                if (sendMessageCallback != null) {
                    sendMessageCallback.onSuccess(message);
                }
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                filterSentMessage(message, errorCode);
                if (sendMessageCallback != null) {
                    sendMessageCallback.onError(message, errorCode);
                }
            }
        });
    }

    /**
     * <p>根据会话类型，发送图片消息。</p>
     *
     * @param type        会话类型。
     * @param targetId    目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param content     消息内容，例如 {@link TextMessage}, {@link ImageMessage}。
     * @param pushContent 当下发 push 消息时，在通知栏里会显示这个字段。
     *                    如果发送的是自定义消息，该字段必须填写，否则无法收到 push 消息。
     *                    如果发送 sdk 中默认的消息类型，例如 RC:TxtMsg, RC:VcMsg, RC:ImgMsg，则不需要填写，默认已经指定。
     * @param pushData    push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link io.rong.push.notification.PushNotificationMessage#getPushData()} 方法获取。
     * @param callback    发送消息的回调。
     */
    public void sendImageMessage(Conversation.ConversationType type, String
            targetId, MessageContent content, String pushContent, String pushData,
                                 final RongIMClient.SendImageMessageCallback callback) {

        Message message = Message.obtain(targetId, type, content);

        Message temp = filterSendMessage(message);
        if (temp == null)
            return;

        if (temp != message)
            message = temp;

        content = message.getContent();

        content = setMessageAttachedUserInfo(content);

        final RongIMClient.ResultCallback.Result<Event.OnReceiveMessageProgressEvent> result = new RongIMClient.ResultCallback.Result<>();
        result.t = new Event.OnReceiveMessageProgressEvent();

        RongIMClient.SendImageMessageCallback sendMessageCallback = new RongIMClient.SendImageMessageCallback() {

            @Override
            public void onAttached(Message message) {

                RongContext.getInstance().getEventBus().post(message);

                if (callback != null)
                    callback.onAttached(message);
            }

            @Override
            public void onProgress(Message message, int progress) {
                if (result.t == null)
                    return;
                result.t.setMessage(message);
                result.t.setProgress(progress);
                RongContext.getInstance().getEventBus().post(result.t);

                if (callback != null)
                    callback.onProgress(message, progress);
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {

                filterSentMessage(message, errorCode);

                if (callback != null)
                    callback.onError(message, errorCode);
            }

            @Override
            public void onSuccess(Message message) {

                filterSentMessage(message, null);

                if (callback != null)
                    callback.onSuccess(message);
            }
        };

        RongIMClient.getInstance().sendImageMessage(type, targetId, content, pushContent, pushData, sendMessageCallback);
    }

    /**
     * <p>发送图片消息</p>
     *
     * @param message     发送消息的实体。
     * @param pushContent 当下发 push 消息时，在通知栏里会显示这个字段。
     *                    如果发送的是自定义消息，该字段必须填写，否则无法收到 push 消息。
     *                    如果发送 sdk 中默认的消息类型，例如 RC:TxtMsg, RC:VcMsg, RC:ImgMsg，则不需要填写，默认已经指定。
     * @param pushData    push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link io.rong.push.notification.PushNotificationMessage#getPushData()} 方法获取。
     * @param callback    发送消息的回调 {@link io.rong.imlib.RongIMClient.SendImageMessageCallback}。
     */
    public void sendImageMessage(Message message, String pushContent,
                                 final String pushData, final RongIMClient.SendImageMessageCallback callback) {

        Message temp = filterSendMessage(message);

        if (temp == null)
            return;

        if (temp != message)
            message = temp;

        setMessageAttachedUserInfo(message.getContent());

        final RongIMClient.ResultCallback.Result<Event.OnReceiveMessageProgressEvent> result = new RongIMClient.ResultCallback.Result<>();
        result.t = new Event.OnReceiveMessageProgressEvent();

        RongIMClient.SendImageMessageCallback sendMessageCallback = new RongIMClient.SendImageMessageCallback() {
            @Override
            public void onAttached(Message message) {
                RongContext.getInstance().getEventBus().post(message);

                if (callback != null)
                    callback.onAttached(message);
            }

            @Override
            public void onProgress(Message message, int progress) {
                if (result.t == null)
                    return;
                result.t.setMessage(message);
                result.t.setProgress(progress);
                RongContext.getInstance().getEventBus().post(result.t);

                if (callback != null)
                    callback.onProgress(message, progress);
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {

                filterSentMessage(message, errorCode);

                if (callback != null)
                    callback.onError(message, errorCode);
            }

            @Override
            public void onSuccess(Message message) {

                filterSentMessage(message, null);

                if (callback != null)
                    callback.onSuccess(message);
            }
        };

        RongIMClient.getInstance().sendImageMessage(message, pushContent, pushData, sendMessageCallback);
    }

    /**
     * <p>发送图片消息，可以使用该方法将图片上传到自己的服务器发送，同时更新图片状态。</p>
     * <p>使用该方法在上传图片时，会回调 {@link io.rong.imlib.RongIMClient.SendImageMessageWithUploadListenerCallback}
     * 此回调中会携带 {@link RongIMClient.UploadImageStatusListener} 对象，使用者只需要调用其中的
     * {@link RongIMClient.UploadImageStatusListener#update(int)} 更新进度
     * {@link RongIMClient.UploadImageStatusListener#success(Uri)} 更新成功状态，并告知上传成功后的图片地址
     * {@link RongIMClient.UploadImageStatusListener#error()} 更新失败状态 </p>
     *
     * @param message     发送消息的实体。
     * @param pushContent 当下发 push 消息时，在通知栏里会显示这个字段。
     *                    如果发送的是自定义消息，该字段必须填写，否则无法收到 push 消息。
     *                    如果发送 sdk 中默认的消息类型，例如 RC:TxtMsg, RC:VcMsg, RC:ImgMsg，则不需要填写，默认已经指定。
     * @param pushData    push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link io.rong.push.notification.PushNotificationMessage#getPushData()} 方法获取。
     * @param callback    发送消息的回调，回调中携带 {@link RongIMClient.UploadImageStatusListener} 对象，用户调用该对象中的方法更新状态。
     *                    {@link #sendImageMessage(Message, String, String, RongIMClient.SendImageMessageCallback)}
     */
    public void sendImageMessage(Message message, String pushContent,
                                 final String pushData,
                                 final RongIMClient.SendImageMessageWithUploadListenerCallback callback) {

        Message temp = filterSendMessage(message);

        if (temp == null)
            return;

        if (temp != message)
            message = temp;

        final RongIMClient.ResultCallback.Result<Event.OnReceiveMessageProgressEvent> result = new RongIMClient.ResultCallback.Result<>();
        result.t = new Event.OnReceiveMessageProgressEvent();

        RongIMClient.SendImageMessageWithUploadListenerCallback sendMessageCallback = new RongIMClient.SendImageMessageWithUploadListenerCallback() {

            @Override
            public void onAttached(Message message, RongIMClient.UploadImageStatusListener listener) {
                RongContext.getInstance().getEventBus().post(message);

                if (callback != null)
                    callback.onAttached(message, listener);
            }

            @Override
            public void onProgress(Message message, int progress) {
                if (result.t == null)
                    return;
                result.t.setMessage(message);
                result.t.setProgress(progress);
                RongContext.getInstance().getEventBus().post(result.t);

                if (callback != null)
                    callback.onProgress(message, progress);
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {

                filterSentMessage(message, errorCode);

                if (callback != null)
                    callback.onError(message, errorCode);
            }

            @Override
            public void onSuccess(Message message) {

                filterSentMessage(message, null);

                if (callback != null)
                    callback.onSuccess(message);
            }
        };

        RongIMClient.getInstance().sendImageMessage(message, pushContent, pushData, sendMessageCallback);
    }

    /**
     * 下载文件。
     * <p/>
     * 用来获取媒体原文件时调用。如果本地缓存中包含此文件，则从本地缓存中直接获取，否则将从服务器端下载。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param mediaType        文件类型。
     * @param imageUrl         文件的 URL 地址。
     * @param callback         下载文件的回调。
     */
    public void downloadMedia(Conversation.ConversationType conversationType, String targetId, RongIMClient.MediaType mediaType, String imageUrl, final RongIMClient.DownloadMediaCallback callback) {
        RongIMClient.getInstance().downloadMedia(conversationType, targetId, mediaType, imageUrl, callback);
    }

    /**
     * 下载文件。和{@link #downloadMedia(Conversation.ConversationType, String, RongIMClient.MediaType, String, RongIMClient.DownloadMediaCallback)}的区别是，该方法支持取消操作。
     * <p/>
     * 用来获取媒体原文件时调用。如果本地缓存中包含此文件，则从本地缓存中直接获取，否则将从服务器端下载。
     *
     * @param message  文件消息。
     * @param callback 下载文件的回调。
     */
    public void downloadMediaMessage(Message message, final IRongCallback.IDownloadMediaMessageCallback callback) {
        RongIMClient.getInstance().downloadMediaMessage(message, new IRongCallback.IDownloadMediaMessageCallback() {
            @Override
            public void onSuccess(Message message) {
                EventBus.getDefault().post(new Event.FileMessageEvent(message, 100, ON_SUCCESS_CALLBACK, null));
                if (callback != null) {
                    callback.onSuccess(message);
                }
            }

            @Override
            public void onProgress(Message message, int progress) {
                EventBus.getDefault().post(new Event.FileMessageEvent(message, progress, ON_PROGRESS_CALLBACK, null));
                if (callback != null) {
                    callback.onProgress(message, progress);
                }
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode code) {
                EventBus.getDefault().post(new Event.FileMessageEvent(message, 0, ON_ERROR_CALLBACK, code));
                if (callback != null) {
                    callback.onError(message, code);
                }
            }

            @Override
            public void onCanceled(Message message) {
                EventBus.getDefault().post(new Event.FileMessageEvent(message, 0, ON_CANCEL_CALLBACK, null));
                if (callback != null) {
                    callback.onCanceled(message);
                }
            }
        });
    }

    /**
     * 下载文件。
     *
     * @param imageUrl 文件的 URL 地址。
     * @param callback 下载文件的回调。
     */
    public void downloadMedia(String imageUrl, final RongIMClient.DownloadMediaCallback callback) {

        ImageLoader.getInstance().loadImage(imageUrl, null, null, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (callback != null)
                    callback.onError(RongIMClient.ErrorCode.RC_NET_UNAVAILABLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (callback != null)
                    callback.onSuccess(imageUri);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                if (callback != null) {
                    callback.onProgress((current * 100) / total);
                }
            }
        });
    }

    /**
     * 获取会话消息提醒状态。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @param callback         获取状态的回调。
     */
    public void getConversationNotificationStatus(final Conversation.ConversationType conversationType, final String targetId, final RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus> callback) {
        RongIMClient.getInstance().getConversationNotificationStatus(conversationType, targetId, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
            @Override
            public void onSuccess(Conversation.ConversationNotificationStatus status) {

                RongContext.getInstance().setConversationNotifyStatusToCache(ConversationKey.obtain(targetId, conversationType), status);

                if (callback != null) {
                    callback.onSuccess(status);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {

                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    /**
     * 设置会话消息提醒状态。
     *
     * @param conversationType   会话类型。
     * @param targetId           目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @param notificationStatus 是否屏蔽。
     * @param callback           设置状态的回调。
     */
    public void setConversationNotificationStatus(final Conversation.ConversationType conversationType, final String targetId, final Conversation.ConversationNotificationStatus notificationStatus, final RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus> callback) {
        RongIMClient.getInstance().setConversationNotificationStatus(conversationType, targetId, notificationStatus, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                if (callback != null)
                    callback.onError(errorCode);
            }

            @Override
            public void onSuccess(Conversation.ConversationNotificationStatus status) {
                RongContext.getInstance().getEventBus().post(new Event.ConversationNotificationEvent(targetId, conversationType, notificationStatus));
                RongContext.getInstance().setConversationNotifyStatusToCache(ConversationKey.obtain(targetId, conversationType), status);

                if (callback != null)
                    callback.onSuccess(status);
            }
        });
    }

    /**
     * 设置讨论组成员邀请权限。
     *
     * @param discussionId 讨论组 id。
     * @param status       邀请状态，默认为开放。
     * @param callback     设置权限的回调。
     */
    public void setDiscussionInviteStatus(final String discussionId, final RongIMClient.DiscussionInviteStatus status, final RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().setDiscussionInviteStatus(discussionId, status, new RongIMClient.OperationCallback() {

            @Override
            public void onSuccess() {
                RongContext.getInstance().getEventBus().post(new Event.DiscussionInviteStatusEvent(discussionId, status));

                if (callback != null)
                    callback.onSuccess();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                if (callback != null)
                    callback.onError(errorCode);
            }
        });
    }

    /**
     * 同步当前用户的群组信息。
     * Warning: 已废弃，请勿使用。
     *
     * @param groups   需要同步的群组实体。
     * @param callback 同步状态的回调。
     * @deprecated 该方法已废弃，请参考 http://www.rongcloud.cn/docs/android.html#3、群组
     * http://support.rongcloud.cn/kb/MzY5 文档中的使用说明。
     */
    @Deprecated
    public void syncGroup(final List<Group> groups, final RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().syncGroup(groups, new RongIMClient.OperationCallback() {

            @Override
            public void onSuccess() {
                RongContext.getInstance().getEventBus().post(new Event.SyncGroupEvent(groups));

                if (callback != null)
                    callback.onSuccess();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

                if (callback != null)
                    callback.onError(errorCode);
            }
        });
    }

    /**
     * 加入群组。
     * Warning: 已废弃，请勿使用。
     *
     * @param groupId   群组 Id。
     * @param groupName 群组名称。
     * @param callback  加入群组状态的回调。
     * @deprecated 该方法已废弃，请参考 http://www.rongcloud.cn/docs/android.html#3、群组 和
     * http://support.rongcloud.cn/kb/MzY5 使用说明。
     */
    @Deprecated
    public void joinGroup(final String groupId, final String groupName, final RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().joinGroup(groupId, groupName, new RongIMClient.OperationCallback() {

            @Override
            public void onSuccess() {
                RongContext.getInstance().getEventBus().post(new Event.JoinGroupEvent(groupId, groupName));

                if (callback != null)
                    callback.onSuccess();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

                if (callback != null)
                    callback.onError(errorCode);
            }
        });
    }

    /**
     * 退出群组。
     * Warning: 已废弃，请勿使用。
     *
     * @param groupId  群组 Id。
     * @param callback 退出群组状态的回调。
     * @deprecated 该方法已废弃，请参考 http://www.rongcloud.cn/docs/android.html#3、群组
     * http://support.rongcloud.cn/kb/MzY5 文档中的使用说明。
     */
    @Deprecated
    public void quitGroup(final String groupId, final RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().quitGroup(groupId, new RongIMClient.OperationCallback() {

            @Override
            public void onSuccess() {
                RongContext.getInstance().getEventBus().post(new Event.QuitGroupEvent(groupId));

                if (callback != null)
                    callback.onSuccess();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

                if (callback != null)
                    callback.onError(errorCode);
            }
        });
    }

    /**
     * 获取当前连接用户的信息。
     *
     * @return 当前连接用户的信息。
     */
    public String getCurrentUserId() {
        return RongIMClient.getInstance().getCurrentUserId();
    }

    /**
     * 获取本地时间与服务器时间的差值。
     * 消息发送成功后，sdk 会与服务器同步时间，消息所在数据库中存储的时间就是服务器时间。
     *
     * @return 本地时间与服务器时间的差值。
     */
    public long getDeltaTime() {
        return RongIMClient.getInstance().getDeltaTime();
    }

    /**
     * 加入聊天室。
     * <p>如果聊天室不存在，sdk 会创建聊天室并加入，如果已存在，则直接加入</p>
     * <p>加入聊天室时，可以选择拉取聊天室消息数目。</p>
     *
     * @param chatroomId      聊天室 Id。
     * @param defMessageCount 进入聊天室拉取消息数目，-1 时不拉取任何消息，0 时拉取 10 条消息，最多只能拉取 40 条。
     * @param callback        状态回调。
     */
    public void joinChatRoom(final String chatroomId, final int defMessageCount, final RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().joinChatRoom(chatroomId, defMessageCount, new RongIMClient.OperationCallback() {

            @Override
            public void onSuccess() {
                RongContext.getInstance().getEventBus().post(new Event.JoinChatRoomEvent(chatroomId, defMessageCount));
                if (callback != null)
                    callback.onSuccess();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                if (callback != null)
                    callback.onError(errorCode);
            }
        });
    }

    /**
     * 加入已存在的聊天室。
     * <p>如果聊天室不存在，则加入失败</p>
     * <p>加入聊天室时，可以选择拉取聊天室消息数目。</p>
     *
     * @param chatroomId      聊天室 Id。
     * @param defMessageCount 进入聊天室拉取消息数目，-1 时不拉取任何消息，0 时拉取 10 条消息，最多只能拉取 40 条。
     * @param callback        状态回调。
     */
    public void joinExistChatRoom(final String chatroomId, final int defMessageCount, final RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().joinExistChatRoom(chatroomId, defMessageCount, new RongIMClient.OperationCallback() {

            @Override
            public void onSuccess() {
                RongContext.getInstance().getEventBus().post(new Event.JoinChatRoomEvent(chatroomId, defMessageCount));

                if (callback != null)
                    callback.onSuccess();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

                if (callback != null)
                    callback.onError(errorCode);
            }
        });
    }

    /**
     * 退出聊天室。
     *
     * @param chatroomId 聊天室 Id。
     * @param callback   状态回调。
     */
    public void quitChatRoom(final String chatroomId, final RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().quitChatRoom(chatroomId, new RongIMClient.OperationCallback() {

            @Override
            public void onSuccess() {
                RongContext.getInstance().getEventBus().post(new Event.QuitChatRoomEvent(chatroomId));

                if (callback != null)
                    callback.onSuccess();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

                if (callback != null)
                    callback.onError(errorCode);
            }
        });
    }

    /**
     * 清空所有会话及会话消息，回调方式通知是否清空成功。
     *
     * @param callback          是否清空成功的回调。
     * @param conversationTypes 会话类型。
     */
    public void clearConversations(final RongIMClient.ResultCallback callback, final Conversation.ConversationType... conversationTypes) {
        RongIMClient.getInstance().clearConversations(new RongIMClient.ResultCallback() {
            @Override
            public void onSuccess(Object o) {
                RongContext.getInstance().getEventBus().post(Event.ClearConversationEvent.obtain(conversationTypes));
                if (callback != null)
                    callback.onSuccess(o);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                if (callback != null)
                    callback.onError(e);
            }
        }, conversationTypes);
    }

    /**
     * 清空所有会话及会话消息。
     *
     * @param conversationTypes 会话类型。
     * @return 是否清空成功。
     * @deprecated 此方法已废弃，请使用{@link #clearConversations(RongIMClient.ResultCallback, Conversation.ConversationType...)}。
     */
    @Deprecated
    public boolean clearConversations(Conversation.ConversationType... conversationTypes) {
        return RongIMClient.getInstance().clearConversations(conversationTypes);
    }

    /**
     * 将某个用户加到黑名单中。
     * <p>当你把对方加入黑名单后，对方再发消息时，就会提示“已被加入黑名单，消息发送失败”。
     * 但你依然可以发消息个对方。</p>
     *
     * @param userId   用户 Id。
     * @param callback 加到黑名单回调。
     */
    public void addToBlacklist(final String userId, final RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().addToBlacklist(userId, new RongIMClient.OperationCallback() {

            @Override
            public void onSuccess() {
                RongContext.getInstance().getEventBus().post(new Event.AddToBlacklistEvent(userId));

                if (callback != null)
                    callback.onSuccess();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

                if (callback != null)
                    callback.onError(errorCode);
            }
        });
    }

    /**
     * 将个某用户从黑名单中移出。
     *
     * @param userId   用户 Id。
     * @param callback 移除黑名单回调。
     */
    public void removeFromBlacklist(final String userId, final RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().removeFromBlacklist(userId, new RongIMClient.OperationCallback() {

            @Override
            public void onSuccess() {
                RongContext.getInstance().getEventBus().post(new Event.RemoveFromBlacklistEvent(userId));

                if (callback != null)
                    callback.onSuccess();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

                if (callback != null)
                    callback.onError(errorCode);
            }
        });
    }

    /**
     * 获取某用户是否在黑名单中。
     *
     * @param userId   用户 Id。
     * @param callback 获取用户是否在黑名单回调。
     */
    public void getBlacklistStatus(String userId, RongIMClient.ResultCallback<RongIMClient.BlacklistStatus> callback) {
        RongIMClient.getInstance().getBlacklistStatus(userId, callback);
    }

    /**
     * 获取当前用户的黑名单列表。
     *
     * @param callback 获取黑名单回调。
     */
    public void getBlacklist(RongIMClient.GetBlacklistCallback callback) {
        RongIMClient.getInstance().getBlacklist(callback);
    }

    /**
     * 设置会话通知免打扰时间。
     *
     * @param startTime   起始时间 格式 HH:MM:SS。
     * @param spanMinutes 间隔分钟数大于 0 小于 1440。
     * @param callback    设置会话通知免打扰时间回调。
     */
    public void setNotificationQuietHours(final String startTime, final int spanMinutes, final RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().setNotificationQuietHours(startTime, spanMinutes, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                MessageNotificationManager.getInstance().saveNotificationQuietHours(mContext, startTime, spanMinutes);
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                if (callback != null) {
                    callback.onError(errorCode);
                }
            }
        });
    }

    /**
     * 移除会话通知免打扰时间。
     *
     * @param callback 移除会话通知免打扰时间回调。
     */
    public void removeNotificationQuietHours(final RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().removeNotificationQuietHours(new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                MessageNotificationManager.getInstance().saveNotificationQuietHours(mContext, "-1", -1);

                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                if (callback != null) {
                    callback.onError(errorCode);
                }
            }
        });
    }

    /**
     * 获取会话通知免打扰时间。
     *
     * @param callback 获取会话通知免打扰时间回调。
     */
    public void getNotificationQuietHours(final RongIMClient.GetNotificationQuietHoursCallback callback) {
        RongIMClient.getInstance().getNotificationQuietHours(new RongIMClient.GetNotificationQuietHoursCallback() {
            @Override
            public void onSuccess(String startTime, int spanMinutes) {
                MessageNotificationManager.getInstance().saveNotificationQuietHours(mContext, startTime, spanMinutes);

                if (callback != null) {
                    callback.onSuccess(startTime, spanMinutes);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                if (callback != null) {
                    callback.onError(errorCode);
                }
            }
        });
    }

    /**
     * 获取公众服务信息。
     *
     * @param publicServiceType 会话类型，APP_PUBLIC_SERVICE 或者 PUBLIC_SERVICE。
     * @param publicServiceId   公众服务 Id。
     * @param callback          获取公众号信息回调。
     */
    public void getPublicServiceProfile(Conversation.PublicServiceType publicServiceType, String publicServiceId, RongIMClient.ResultCallback<PublicServiceProfile> callback) {
        RongIMClient.getInstance().getPublicServiceProfile(publicServiceType, publicServiceId, callback);
    }

    /**
     * 搜索公众服务。
     *
     * @param searchType 搜索类型枚举。
     * @param keywords   搜索关键字。
     * @param callback   搜索结果回调。
     */
    public void searchPublicService(RongIMClient.SearchType searchType, String keywords, RongIMClient.ResultCallback<PublicServiceProfileList> callback) {
        RongIMClient.getInstance().searchPublicService(searchType, keywords, callback);
    }

    /**
     * 按公众服务类型搜索公众服务。
     *
     * @param publicServiceType 公众服务类型。
     * @param searchType        搜索类型枚举。
     * @param keywords          搜索关键字。
     * @param callback          搜索结果回调。
     */
    public void searchPublicServiceByType(Conversation.PublicServiceType publicServiceType, RongIMClient.SearchType searchType, final String keywords, final RongIMClient.ResultCallback<PublicServiceProfileList> callback) {
        RongIMClient.getInstance().searchPublicServiceByType(publicServiceType, searchType, keywords, callback);
    }

    /**
     * 订阅公众号。
     *
     * @param publicServiceId   公共服务 Id。
     * @param publicServiceType 公众服务类型枚举。
     * @param callback          订阅公众号回调。
     */
    public void subscribePublicService(Conversation.PublicServiceType publicServiceType, String publicServiceId, RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().subscribePublicService(publicServiceType, publicServiceId, callback);
    }

    /**
     * 取消订阅公众号。
     *
     * @param publicServiceId   公共服务 Id。
     * @param publicServiceType 公众服务类型枚举。
     * @param callback          取消订阅公众号回调。
     */
    public void unsubscribePublicService(Conversation.PublicServiceType publicServiceType, String publicServiceId, RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().unsubscribePublicService(publicServiceType, publicServiceId, callback);
    }

    /**
     * 获取己关注公共账号列表。
     *
     * @param callback 获取己关注公共账号列表回调。
     */
    public void getPublicServiceList(RongIMClient.ResultCallback<PublicServiceProfileList> callback) {
        RongIMClient.getInstance().getPublicServiceList(callback);
    }


    /**
     * 设置用户信息。
     *
     * @param userData 用户信息。
     * @param callback 设置用户信息回调。
     */
    public void syncUserData(final UserData userData, final RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().syncUserData(userData, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                if (callback != null) {
                    callback.onError(errorCode);
                }
            }
        });
    }

    /**
     * 设置请求权限的监听器。在Android 6.0 以上系统时，如果融云内部需要请求某些权限，会通过这个监听器像用户请求对应权限。
     * 用户可以在该权限监听器里调用Android 6.0相关权限请求api,进行权限处理。
     *
     * @param listener 权限监听器。
     **/
    @Deprecated
    public void setRequestPermissionListener(RequestPermissionsListener listener) {
        RongContext.getInstance().setRequestPermissionListener(listener);
    }

    /**
     * <p>记录在开发者后台使用后台推送功能时，对应的推送通知的点击事件。开发者后台的推送打开率既根据客户端上传的该事件进行相应统计和计算。
     * 2.6.0之前版本，推送打开率的使用请在知识库里搜索标签push，有相关说明。
     * 2.6.0之后版本，如果用户使用的SDK内置的通知实现，则不需要调用该方法来统计推送打开率，SDK内部已经帮用户做了统计。
     * 但是如果用户自己定义了推送时通知栏的显示，则需要在点击通知时，调用此方法，来向服务器上传推送打开事件。</p>
     *
     * @param pushId push通知的id。只有当后台广播消息和后台推送时，pushId才会有值，其余非后台情况下都为空。
     */
    public void recordNotificationEvent(String pushId) {
        RongPushClient.recordNotificationEvent(pushId);
    }

    /**
     * 请求权限监听器。
     **/
    @Deprecated
    public interface RequestPermissionsListener {
        @Deprecated
        void onPermissionRequest(String[] permissions, int requestCode);
    }

    private MessageContent setMessageAttachedUserInfo(MessageContent content) {

        if (RongContext.getInstance().getUserInfoAttachedState()) {

            if (content.getUserInfo() == null) {
                String userId = RongIM.getInstance().getCurrentUserId();

                UserInfo info = RongContext.getInstance().getCurrentUserInfo();

                if (info == null)
                    info = RongUserInfoManager.getInstance().getUserInfo(userId);

                if (info != null)
                    content.setUserInfo(info);
            }
        }

        return content;
    }

    /**
     * 对UI已发送消息进行过滤。
     *
     * @param conversationType 会话类型
     * @param targetId         会话id
     * @param messageContent   消息内容
     * @return 消息
     */
    private Message filterSendMessage(Conversation.ConversationType conversationType, String targetId, MessageContent messageContent) {
        Message message = new Message();
        message.setConversationType(conversationType);
        message.setTargetId(targetId);
        message.setContent(messageContent);

        if (RongContext.getInstance().getOnSendMessageListener() != null) {
            message = RongContext.getInstance().getOnSendMessageListener().onSend(message);
        }

        return message;
    }

    /**
     * 对 UI 已发送消息进行过滤。
     *
     * @param message 消息
     * @return 消息
     */
    private Message filterSendMessage(Message message) {

        if (RongContext.getInstance().getOnSendMessageListener() != null) {
            message = RongContext.getInstance().getOnSendMessageListener().onSend(message);
        }

        return message;
    }

    private void filterSentMessage(Message message, RongIMClient.ErrorCode errorCode) {

        SentMessageErrorCode sentMessageErrorCode = null;
        boolean isExecute = false;

        if (RongContext.getInstance().getOnSendMessageListener() != null) {

            if (errorCode != null) {
                sentMessageErrorCode = SentMessageErrorCode.setValue(errorCode.getValue());
            }

            isExecute = RongContext.getInstance().getOnSendMessageListener().onSent(message, sentMessageErrorCode);
        }

        if (errorCode != null && !isExecute) {

            if (errorCode.equals(RongIMClient.ErrorCode.NOT_IN_DISCUSSION) || errorCode.equals(RongIMClient.ErrorCode.NOT_IN_GROUP)
                    || errorCode.equals(RongIMClient.ErrorCode.NOT_IN_CHATROOM) || errorCode.equals(RongIMClient.ErrorCode.REJECTED_BY_BLACKLIST) || errorCode.equals(RongIMClient.ErrorCode.FORBIDDEN_IN_GROUP)
                    || errorCode.equals(RongIMClient.ErrorCode.FORBIDDEN_IN_CHATROOM) || errorCode.equals(RongIMClient.ErrorCode.KICKED_FROM_CHATROOM)) {

                InformationNotificationMessage informationMessage = null;

                if (errorCode.equals(RongIMClient.ErrorCode.NOT_IN_DISCUSSION)) {
                    informationMessage = InformationNotificationMessage.obtain(mContext.getString(R.string.rc_info_not_in_discussion));
                } else if (errorCode.equals(RongIMClient.ErrorCode.NOT_IN_GROUP)) {
                    informationMessage = InformationNotificationMessage.obtain(mContext.getString(R.string.rc_info_not_in_group));
                } else if (errorCode.equals(RongIMClient.ErrorCode.NOT_IN_CHATROOM)) {
                    informationMessage = InformationNotificationMessage.obtain(mContext.getString(R.string.rc_info_not_in_chatroom));
                } else if (errorCode.equals(RongIMClient.ErrorCode.REJECTED_BY_BLACKLIST)) {
                    informationMessage = InformationNotificationMessage.obtain(mContext.getString(R.string.rc_rejected_by_blacklist_prompt));
                } else if (errorCode.equals(RongIMClient.ErrorCode.FORBIDDEN_IN_GROUP)) {
                    informationMessage = InformationNotificationMessage.obtain(mContext.getString(R.string.rc_info_forbidden_to_talk));
                } else if (errorCode.equals(RongIMClient.ErrorCode.FORBIDDEN_IN_CHATROOM)) {
                    informationMessage = InformationNotificationMessage.obtain(mContext.getString(R.string.rc_forbidden_in_chatroom));
                } else if (errorCode.equals(RongIMClient.ErrorCode.KICKED_FROM_CHATROOM)) {
                    informationMessage = InformationNotificationMessage.obtain(mContext.getString(R.string.rc_kicked_from_chatroom));
                }

                insertMessage(message.getConversationType(), message.getTargetId(), "rong", informationMessage, new RongIMClient.ResultCallback<Message>() {
                    @Override
                    public void onSuccess(Message message) {

                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode e) {

                    }
                });
            }

            MessageContent content = message.getContent();
            MessageTag tag = content.getClass().getAnnotation(MessageTag.class);

            if (tag != null && (tag.flag() & MessageTag.ISPERSISTED) == MessageTag.ISPERSISTED) {
                RongContext.getInstance().getEventBus().post(new Event.OnMessageSendErrorEvent(message, errorCode));
            }

        } else {//发消息成功 onSuccess()或onProgress()
            if (message != null) {
                MessageContent content = message.getContent();

                MessageTag tag = content.getClass().getAnnotation(MessageTag.class);

                if (tag != null && (tag.flag() & MessageTag.ISPERSISTED) == MessageTag.ISPERSISTED) {
                    RongContext.getInstance().getEventBus().post(message);
                }
            }
        }
    }

    /**
     * 设置导航服务器，媒体服务器地址。
     * 此方法要在 {@link #init(Context, String)} 前使用
     *
     * @param naviServer 导航服务器地址。
     * @param fileServer 媒体服务器地址，暂未使用，可以设置为 null。
     */
    public static void setServerInfo(final String naviServer, final String fileServer) {
        if (TextUtils.isEmpty(naviServer)) {
            RLog.e(TAG, "setServerInfo naviServer should not be null.");
            throw new IllegalArgumentException("naviServer should not be null.");
        }
        RongIMClient.setServerInfo(naviServer, fileServer);
    }

    /**
     * 设置公众服务菜单点击监听。
     * 建议使用方法：在进入对应公众服务会话时，设置监听。当退出会话时，重置监听为 null，这样可以防止内存泄露。
     *
     * @param menuClickListener 监听。
     */
    public void setPublicServiceMenuClickListener(IPublicServiceMenuClickListener menuClickListener) {
        if (RongContext.getInstance() != null) {
            RongContext.getInstance().setPublicServiceMenuClickListener(menuClickListener);
        }
    }

    /**
     * 撤回消息时，无法发送 push 通知。
     *
     * @param message 将被撤回的消息
     * @deprecated 该接口废弃，请使用 {@link #recallMessage(Message, String)}
     */
    @Deprecated
    public void recallMessage(final Message message) {
        recallMessage(message, "");
    }

    /**
     * 撤回消息
     *
     * @param message     将被撤回的消息
     * @param pushContent 被撤回时，通知栏显示的信息
     */
    public void recallMessage(final Message message, String pushContent) {
        RongIMClient.getInstance().recallMessage(message, pushContent, new RongIMClient.ResultCallback<RecallNotificationMessage>() {
            @Override
            public void onSuccess(RecallNotificationMessage recallNotificationMessage) {
                RongContext.getInstance().getEventBus().post(new Event.MessageRecallEvent(message.getMessageId(), recallNotificationMessage, true));
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                RLog.d(TAG, "recallMessage errorCode = " + errorCode.getValue());
            }
        });
    }

    /**
     * <p>发送多媒体消息</p>
     * <p>发送前构造 {@link Message} 消息实体，消息实体中的 content 必须为 {@link FileMessage}, 否则返回失败。</p>
     *
     * @param message     发送消息的实体。
     * @param pushContent 当下发 push 消息时，在通知栏里会显示这个字段。
     *                    发送文件消息时，此字段必须填写，否则会收不到 push 推送。
     * @param pushData    push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link io.rong.push.notification.PushNotificationMessage#getPushData()} 方法获取。
     * @param callback    发送消息的回调 {@link io.rong.imlib.RongIMClient.SendMediaMessageCallback}。
     */
    public void sendMediaMessage(Message message, String pushContent,
                                 final String pushData, final IRongCallback.ISendMediaMessageCallback callback) {

        Message temp = filterSendMessage(message);

        if (temp == null) {
            return;
        }

        if (temp != message)
            message = temp;

        setMessageAttachedUserInfo(message.getContent());

        final RongIMClient.ResultCallback.Result<Event.OnReceiveMessageProgressEvent> result = new RongIMClient.ResultCallback.Result<>();
        result.t = new Event.OnReceiveMessageProgressEvent();

        IRongCallback.ISendMediaMessageCallback sendMessageCallback = new IRongCallback.ISendMediaMessageCallback() {
            @Override
            public void onProgress(Message message, int progress) {
                if (result.t == null)
                    return;
                result.t.setMessage(message);
                result.t.setProgress(progress);
                RongContext.getInstance().getEventBus().post(result.t);

                if (callback != null)
                    callback.onProgress(message, progress);
            }

            @Override
            public void onAttached(Message message) {
                RongContext.getInstance().getEventBus().post(message);

                if (callback != null)
                    callback.onAttached(message);
            }

            @Override
            public void onSuccess(Message message) {
                filterSentMessage(message, null);

                if (callback != null)
                    callback.onSuccess(message);
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                filterSentMessage(message, errorCode);

                if (callback != null)
                    callback.onError(message, errorCode);
            }

            @Override
            public void onCanceled(Message message) {
                filterSentMessage(message, null);
                if (callback != null) {
                    callback.onCanceled(message);
                }
            }
        };

        RongIMClient.getInstance().sendMediaMessage(message, pushContent, pushData, sendMessageCallback);
    }

    /**
     * <p>发送多媒体消息，可以使用该方法将多媒体文件上传到自己的服务器。
     * 使用该方法在上传多媒体文件时，会回调 {@link io.rong.imlib.IRongCallback.ISendMediaMessageCallbackWithUploader#onAttached(Message, IRongCallback.MediaMessageUploader)}
     * 此回调中会携带 {@link IRongCallback.MediaMessageUploader} 对象，使用者只需要调用此对象中的
     * {@link IRongCallback.MediaMessageUploader#update(int)} 更新进度
     * {@link IRongCallback.MediaMessageUploader#success(Uri)} 更新成功状态，并告知上传成功后的文件地址
     * {@link IRongCallback.MediaMessageUploader#error()} 更新失败状态
     * </p>
     *
     * @param message     发送消息的实体。
     * @param pushContent 当下发 push 消息时，在通知栏里会显示这个字段。
     *                    如果发送的是自定义消息，该字段必须填写，否则无法收到 push 消息。
     *                    如果发送 sdk 中默认的消息类型，例如 RC:TxtMsg, RC:VcMsg, RC:ImgMsg, RC:FileMsg，则不需要填写，默认已经指定。
     * @param pushData    push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link io.rong.push.notification.PushNotificationMessage#getPushData()} 方法获取。
     * @param callback    发送消息的回调，回调中携带 {@link IRongCallback.MediaMessageUploader} 对象，用户调用该对象中的方法更新状态。
     */
    public void sendMediaMessage(Message message, String pushContent, final String pushData, final IRongCallback.ISendMediaMessageCallbackWithUploader callback) {

        Message temp = filterSendMessage(message);
        if (temp == null) {
            return;
        }
        if (temp != message) {
            message = temp;
        }
        setMessageAttachedUserInfo(message.getContent());

        IRongCallback.ISendMediaMessageCallbackWithUploader sendMediaMessageCallbackWithUploader = new IRongCallback.ISendMediaMessageCallbackWithUploader() {
            @Override
            public void onAttached(Message message, IRongCallback.MediaMessageUploader uploader) {
                MessageTag tag = message.getContent().getClass().getAnnotation(MessageTag.class);
                if (tag != null && (tag.flag() & MessageTag.ISPERSISTED) == MessageTag.ISPERSISTED) {
                    RongContext.getInstance().getEventBus().post(message);
                }
                if (callback != null) {
                    callback.onAttached(message, uploader);
                }
            }

            @Override
            public void onProgress(Message message, int progress) {
                final RongIMClient.ResultCallback.Result<Event.OnReceiveMessageProgressEvent> result = new RongIMClient.ResultCallback.Result<>();
                result.t = new Event.OnReceiveMessageProgressEvent();
                result.t.setMessage(message);
                result.t.setProgress(progress);
                RongContext.getInstance().getEventBus().post(result.t);
                if (callback != null) {
                    callback.onProgress(message, progress);
                }
            }

            @Override
            public void onSuccess(Message message) {
                filterSentMessage(message, null);
                if (callback != null) {
                    callback.onSuccess(message);
                }
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                filterSentMessage(message, errorCode);
                if (callback != null) {
                    callback.onError(message, errorCode);
                }
            }

            @Override
            public void onCanceled(Message message) {
                filterSentMessage(message, null);
                if (callback != null) {
                    callback.onCanceled(message);
                }
            }
        };

        RongIMClient.getInstance().sendMediaMessage(message, pushContent, pushData, sendMediaMessageCallbackWithUploader);
    }

    /**
     * 取消下载多媒体文件。
     *
     * @param message  包含多媒体文件的消息，即{@link MessageContent}为 FileMessage, ImageMessage 等。
     * @param callback 取消下载多媒体文件时的回调。
     */
    public void cancelDownloadMediaMessage(Message message, RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().cancelDownloadMediaMessage(message, callback);
    }

    /**
     * 取消发送多媒体文件。
     *
     * @param message  包含多媒体文件的消息，即{@link MessageContent}为 FileMessage, ImageMessage 等。
     * @param callback 取消发送多媒体文件时的回调。
     */
    public void cancelSendMediaMessage(Message message, RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().cancelSendMediaMessage(message, callback);
    }

    /**
     * 设置发送消息回执的会话类型。目前只支持私聊，群组和讨论组。
     * 默认支持私聊。
     *
     * @param types 包含在types里的会话类型中将会发送消息回执。
     */
    public void setReadReceiptConversationTypeList(Conversation.ConversationType... types) {
        if (RongContext.getInstance() != null) {
            RongContext.getInstance().setReadReceiptConversationTypeList(types);
        }
    }

    /**
     * <p>发送定向消息。向会话中特定的某些用户发送消息，会话中其他用户将不会收到此消息。
     * 通过 {@link io.rong.imlib.IRongCallback.ISendMessageCallback} 中的方法回调发送的消息状态及消息体。</p>
     *
     * @param type        会话类型。
     * @param targetId    目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param content     消息内容，例如 {@link TextMessage}, {@link ImageMessage}。
     * @param pushContent 当下发 push 消息时，在通知栏里会显示这个字段。
     *                    如果发送的是自定义消息，该字段必须填写，否则无法收到 push 消息。
     *                    如果发送 sdk 中默认的消息类型，例如 RC:TxtMsg, RC:VcMsg, RC:ImgMsg，则不需要填写，默认已经指定。
     * @param pushData    push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link io.rong.push.notification.PushNotificationMessage#getPushData()} 方法获取。
     * @param userIds     会话中将会接收到此消息的用户列表。
     * @param callback    发送消息的回调，参考 {@link io.rong.imlib.IRongCallback.ISendMessageCallback}。
     */
    public void sendDirectionalMessage(Conversation.ConversationType type, String targetId, MessageContent content, final String[] userIds, String pushContent, final String pushData, final IRongCallback.ISendMessageCallback callback) {
        Message message = Message.obtain(targetId, type, content);
        final Message filterMsg = filterSendMessage(message);
        if (filterMsg == null) {
            RLog.w(TAG, "sendDirectionalMessage: 因在 onSend 中消息被过滤为 null，取消发送。");
            return;
        }
        if (filterMsg != message) {
            message = filterMsg;
        }
        message.setContent(setMessageAttachedUserInfo(message.getContent()));
        RongIMClient.getInstance().sendDirectionalMessage(type, targetId, content, userIds, pushContent, pushData, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {
                MessageTag tag = message.getContent().getClass().getAnnotation(MessageTag.class);
                if (tag != null && (tag.flag() & MessageTag.ISPERSISTED) == MessageTag.ISPERSISTED) {
                    RongContext.getInstance().getEventBus().post(message);
                }

                if (callback != null) {
                    callback.onAttached(message);
                }
            }

            @Override
            public void onSuccess(Message message) {
                filterSentMessage(message, null);
                if (callback != null) {
                    callback.onSuccess(message);
                }
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                filterSentMessage(message, errorCode);
                if (callback != null) {
                    callback.onError(message, errorCode);
                }
            }
        });
    }
}
