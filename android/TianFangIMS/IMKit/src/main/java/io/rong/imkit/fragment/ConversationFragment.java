package io.rong.imkit.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imkit.IExtensionClickListener;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.IPublicServiceMenuClickListener;
import io.rong.imkit.InputMenu;
import io.rong.imkit.R;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongKitIntent;
import io.rong.imkit.RongKitReceiver;
import io.rong.imkit.manager.AudioPlayManager;
import io.rong.imkit.manager.AudioRecordManager;
import io.rong.imkit.manager.InternalModuleManager;
import io.rong.imkit.manager.SendImageManager;
import io.rong.imkit.mention.RongMentionManager;
import io.rong.imkit.model.ConversationInfo;
import io.rong.imkit.model.Event;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.plugin.DefaultLocationPlugin;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.plugin.location.AMapRealTimeActivity;
import io.rong.imkit.plugin.location.IRealTimeLocationStateListener;
import io.rong.imkit.plugin.location.IUserInfoProvider;
import io.rong.imkit.plugin.location.LocationManager;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imkit.utilities.PromptPopupDialog;
import io.rong.imkit.widget.AutoRefreshListView;
import io.rong.imkit.widget.CSEvaluateDialog;
import io.rong.imkit.widget.SingleChoiceDialog;
import io.rong.imkit.widget.adapter.MessageListAdapter;
import io.rong.imkit.widget.provider.EvaluatePlugin;
import io.rong.imlib.CustomServiceConfig;
import io.rong.imlib.ICustomServiceListener;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.location.RealTimeLocationConstant;
import io.rong.imlib.model.CSCustomServiceInfo;
import io.rong.imlib.model.CSGroupItem;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.CustomServiceMode;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.PublicServiceMenu;
import io.rong.imlib.model.PublicServiceMenuItem;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.ReadReceiptInfo;
import io.rong.imlib.model.UserInfo;
import io.rong.message.CSPullLeaveMessage;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.PublicServiceCommandMessage;
import io.rong.message.ReadReceiptMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import io.rong.push.RongPushClient;

public class ConversationFragment extends UriFragment implements
        AbsListView.OnScrollListener,
        IExtensionClickListener,
        IUserInfoProvider,
        CSEvaluateDialog.EvaluateClickListener {
    private static final String TAG = "ConversationFragment";
    private PublicServiceProfile mPublicServiceProfile;

    private RongExtension mRongExtension;
    private boolean mEnableMention;
    private float mLastTouchY;
    private boolean mUpDirection;
    private float mOffsetLimit;

    private CSCustomServiceInfo mCustomUserInfo;
    private ConversationInfo mCurrentConversationInfo;
    private String mDraft;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 100;
    private static final int REQUEST_CODE_LOCATION_SHARE = 101;
    private static final int REQUEST_CS_LEAVEL_MESSAGE = 102;

    public final static int SCROLL_MODE_NORMAL = 1;
    public final static int SCROLL_MODE_TOP = 2;
    public final static int SCROLL_MODE_BOTTOM = 3;

    private final static int DEFAULT_HISTORY_MESSAGE_COUNT = 30;
    private final static int DEFAULT_REMOTE_MESSAGE_COUNT = 10;
    private final static int TIP_DEFAULT_MESSAGE_COUNT = 2;

    private String mTargetId;
    private Conversation.ConversationType mConversationType;

    private boolean mReadRec;
    private boolean mSyncReadStatus;
    private int mNewMessageCount;

    private AutoRefreshListView mList;
    private Button mUnreadBtn;
    private ImageButton mNewMessageBtn;
    private TextView mNewMessageTextView;
    private MessageListAdapter mListAdapter;
    private View mMsgListView;
    private LinearLayout mNotificationContainer;

    private boolean mHasMoreLocalMessages;
    private int mLastMentionMsgId;
    private long mSyncReadStatusMsgTime;
    private boolean mCSNeedToQuit = false;

    private List<String> mLocationShareParticipants;
    private CustomServiceConfig mCustomServiceConfig;
    private CSEvaluateDialog mEvaluateDialg;

    private RongKitReceiver mKitReceiver;

    private final int CS_HUMAN_MODE_CUSTOMER_EXPIRE = 0;
    private final int CS_HUMAN_MODE_SEAT_EXPIRE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RLog.i(TAG, "onCreate");
        InternalModuleManager.getInstance().onLoaded();
        try {
            mEnableMention = getActivity().getResources().getBoolean(R.bool.rc_enable_mentioned_message);
        } catch (Resources.NotFoundException e) {
            RLog.e(TAG, "rc_enable_mentioned_message not found in rc_config.xml");
        }

        try {
            mReadRec = getResources().getBoolean(R.bool.rc_read_receipt);
            mSyncReadStatus = getResources().getBoolean(R.bool.rc_enable_sync_read_status);
        } catch (Resources.NotFoundException e) {
            RLog.e(TAG, "rc_read_receipt not found in rc_config.xml");
            e.printStackTrace();
        }

        mKitReceiver = new RongKitReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        try {
            getActivity().registerReceiver(mKitReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rc_fr_conversation, container, false);
        mRongExtension = (RongExtension) view.findViewById(R.id.rc_extension);
        mRongExtension.setExtensionClickListener(this);
        mRongExtension.setFragment(this);
        mOffsetLimit = 70 * getActivity().getResources().getDisplayMetrics().density;
        view.findViewById(R.id.rc_extension);
        mMsgListView = findViewById(view, R.id.rc_layout_msg_list);
        mList = findViewById(mMsgListView, R.id.rc_list);
        mList.requestDisallowInterceptTouchEvent(true);
        mList.setMode(AutoRefreshListView.Mode.START);
        mList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mListAdapter = onResolveAdapter(getActivity());
        mList.setAdapter(mListAdapter);

        mList.setOnRefreshListener(new AutoRefreshListView.OnRefreshListener() {
            @Override
            public void onRefreshFromStart() {
                if (mHasMoreLocalMessages) {
                    getHistoryMessage(mConversationType, mTargetId, DEFAULT_HISTORY_MESSAGE_COUNT, SCROLL_MODE_NORMAL);
                } else {
                    getRemoteHistoryMessages(mConversationType, mTargetId, DEFAULT_REMOTE_MESSAGE_COUNT);
                }
            }

            @Override
            public void onRefreshFromEnd() {

            }
        });

        mList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE &&
                        mList.getCount() - mList.getHeaderViewsCount() == 0) {
                    if (mHasMoreLocalMessages) {
                        getHistoryMessage(mConversationType, mTargetId, DEFAULT_HISTORY_MESSAGE_COUNT, SCROLL_MODE_NORMAL);
                    } else {
                        if (mList.getRefreshState() != AutoRefreshListView.State.REFRESHING) {
                            getRemoteHistoryMessages(mConversationType, mTargetId, DEFAULT_REMOTE_MESSAGE_COUNT);
                        }
                    }
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP && mRongExtension != null && mRongExtension.isExtensionExpanded()) {
                    mRongExtension.collapseExtension();
                }
                return false;
            }
        });

        if (RongContext.getInstance().getNewMessageState()) {
            mNewMessageTextView = findViewById(view, R.id.rc_new_message_number);
            mNewMessageBtn = findViewById(view, R.id.rc_new_message_count);
            mNewMessageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mList.smoothScrollToPosition(mList.getCount() + 1);
                    mNewMessageBtn.setVisibility(View.GONE);
                    mNewMessageTextView.setVisibility(View.GONE);
                    mNewMessageCount = 0;
                }
            });
        }
        if (RongContext.getInstance().getUnreadMessageState()) {
            mUnreadBtn = findViewById(mMsgListView, R.id.rc_unread_message_count);
        }

        mList.addOnScrollListener(this);

        mListAdapter.setOnItemHandlerListener(new MessageListAdapter.OnItemHandlerListener() {

            @Override
            public boolean onWarningViewClick(final int position, final io.rong.imlib.model.Message data, final View v) {
                if (!ConversationFragment.this.onResendItemClick(data)) {
                    RongIMClient.getInstance().deleteMessages(new int[]{data.getMessageId()}, new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            if (aBoolean) {
                                mListAdapter.remove(position);
                                data.setMessageId(0);
                                if (data.getContent() instanceof ImageMessage) {
                                    RongIM.getInstance().sendImageMessage(data, null, null, (RongIMClient.SendImageMessageCallback) null);
                                } else if (data.getContent() instanceof LocationMessage) {
                                    RongIM.getInstance().sendLocationMessage(data, null, null, null);
                                } else if (data.getContent() instanceof FileMessage) {
                                    RongIM.getInstance().sendMediaMessage(data, null, null, (IRongCallback.ISendMediaMessageCallback) null);
                                } else {
                                    RongIM.getInstance().sendMessage(data, null, null, (IRongCallback.ISendMessageCallback) null);
                                }
                            }
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {

                        }
                    });
                }
                return true;
            }

            @Override
            public void onReadReceiptStateClick(io.rong.imlib.model.Message message) {
                ConversationFragment.this.onReadReceiptStateClick(message);
            }
        });
        return view;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
            if (mRongExtension != null) mRongExtension.collapseExtension();
        } else if (scrollState == SCROLL_STATE_IDLE) {
            int last = mList.getLastVisiblePosition();
            // 当最后 2 条消息不可见时，设置 ListView 为 TRANSCRIPT_MODE_NORMAL 模式
            // 防止再次接收消息时，自动滚到动底部。
            if (mList.getCount() - last > TIP_DEFAULT_MESSAGE_COUNT) {
                mList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
            } else {
                mList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            }
            if (mNewMessageBtn != null && last == mList.getCount() - 1) {
                mNewMessageCount = 0;
                mNewMessageBtn.setVisibility(View.GONE);
                mNewMessageTextView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onResume() {
        RongPushClient.clearAllPushNotifications(getActivity());
        super.onResume();
    }

    @Override
    final public void getUserInfo(String userId, UserInfoCallback callback) {
        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(userId);
        if (userInfo != null) {
            callback.onGotUserInfo(userInfo);
        }
    }

    /**
     * 提供 ListView 的 Adapter 适配器。
     * 使用时，需要继承 {@link ConversationFragment} 并重写此方法。
     * 注意：提供的适配器，要继承自 {@link MessageListAdapter}
     *
     * @return 适配器
     */
    public MessageListAdapter onResolveAdapter(Context context) {
        return new MessageListAdapter(context);
    }

    @Override
    protected void initFragment(final Uri uri) {
        RLog.d(TAG, "initFragment : " + uri + ",this=" + this);
        if (uri != null) {
            String typeStr = uri.getLastPathSegment().toUpperCase(Locale.US);
            mConversationType = Conversation.ConversationType.valueOf(typeStr);
            mTargetId = uri.getQueryParameter("targetId");

            //优先设置 Extension 会话属性
            mRongExtension.setConversation(mConversationType, mTargetId);
            RongIMClient.getInstance().getTextMessageDraft(mConversationType, mTargetId, new RongIMClient.ResultCallback<String>() {
                @Override
                public void onSuccess(String s) {
                    mDraft = s;
                    if (mRongExtension != null) {
                        EditText editText = mRongExtension.getInputEditText();
                        editText.setText(s);
                        editText.setSelection(editText.length());
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {
                }
            });

            mCurrentConversationInfo = ConversationInfo.obtain(mConversationType, mTargetId);
            RongContext.getInstance().registerConversationInfo(mCurrentConversationInfo);
            mNotificationContainer = (LinearLayout) mMsgListView.findViewById(R.id.rc_notification_container);

            if (mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)
                    && getActivity() != null
                    && getActivity().getIntent() != null
                    && getActivity().getIntent().getData() != null) {
                mCustomUserInfo = getActivity().getIntent().getParcelableExtra("customServiceInfo");
            }

            LocationManager.getInstance().bindConversation(getActivity(), mConversationType, mTargetId);
            LocationManager.getInstance().setUserInfoProvider(this);
            LocationManager.getInstance().setParticipantChangedListener(new IRealTimeLocationStateListener() {

                private View mRealTimeBar;
                private TextView mRealTimeText;

                @Override
                public void onParticipantChanged(List<String> userIdList) {
                    if (ConversationFragment.this.isDetached()) {
                        return;
                    }
                    if (mRealTimeBar == null) {
                        mRealTimeBar = inflateNotificationView(R.layout.rc_notification_realtime_location);
                        mRealTimeText = (TextView) mRealTimeBar.findViewById(R.id.real_time_location_text);
                        mRealTimeBar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RealTimeLocationConstant.RealTimeLocationStatus status = RongIMClient.getInstance().getRealTimeLocationCurrentState(mConversationType, mTargetId);
                                if (status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_INCOMING) {
                                    PromptPopupDialog dialog = PromptPopupDialog.newInstance(ConversationFragment.this.getActivity(), "", getResources().getString(R.string.rc_real_time_join_notification));
                                    dialog.setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
                                        @Override
                                        public void onPositiveButtonClicked() {
                                            int result = LocationManager.getInstance().joinLocationSharing();
                                            if(result == 0) {
                                                Intent intent = new Intent(ConversationFragment.this.getActivity(), AMapRealTimeActivity.class);
                                                if (mLocationShareParticipants != null) {
                                                    intent.putStringArrayListExtra("participants", (ArrayList<String>) mLocationShareParticipants);
                                                }
                                                startActivity(intent);
                                            }else if(result == 1){
                                                Toast.makeText(getActivity(), R.string.rc_network_exception, Toast.LENGTH_SHORT).show();
                                            }else if((result == 2)){
                                                Toast.makeText(getActivity(), R.string.rc_location_sharing_exceed_max, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    dialog.show();
                                } else {
                                    Intent intent = new Intent(ConversationFragment.this.getActivity(), AMapRealTimeActivity.class);
                                    if (mLocationShareParticipants != null) {
                                        intent.putStringArrayListExtra("participants", (ArrayList<String>) mLocationShareParticipants);
                                    }
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                    mLocationShareParticipants = userIdList;
                    if (userIdList != null) {
                        if (userIdList.size() == 0) {
                            hideNotificationView(mRealTimeBar);
                        } else {
                            if (userIdList.size() == 1 && userIdList.contains(RongIM.getInstance().getCurrentUserId())) {
                                mRealTimeText.setText(getResources().getString(R.string.rc_you_are_sharing_location));
                            } else if (userIdList.size() == 1 && !userIdList.contains(RongIM.getInstance().getCurrentUserId())) {
                                mRealTimeText.setText(String.format(getResources().getString(R.string.rc_other_is_sharing_location), getNameFromCache(userIdList.get(0))));
                            } else {
                                mRealTimeText.setText(String.format(getResources().getString(R.string.rc_others_are_sharing_location), userIdList.size()));
                            }
                            showNotificationView(mRealTimeBar);
                        }
                    } else {
                        hideNotificationView(mRealTimeBar);
                    }
                }

                @Override
                public void onErrorException() {
                    if (!ConversationFragment.this.isDetached()) {
                        hideNotificationView(mRealTimeBar);
                        if (mLocationShareParticipants != null) {
                            mLocationShareParticipants.clear();
                            mLocationShareParticipants = null;
                        }
                    }
                }
            });


            if (mConversationType.equals(Conversation.ConversationType.CHATROOM)) {
                boolean createIfNotExist = getActivity() != null && getActivity().getIntent().getBooleanExtra("createIfNotExist", true);
                int pullCount = getResources().getInteger(R.integer.rc_chatroom_first_pull_message_count);
                if (createIfNotExist)
                    RongIMClient.getInstance().joinChatRoom(mTargetId, pullCount, new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            RLog.i(TAG, "joinChatRoom onSuccess : " + mTargetId);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            RLog.e(TAG, "joinChatRoom onError : " + errorCode);
                            if (getActivity() != null) {
                                if (errorCode == RongIMClient.ErrorCode.RC_NET_UNAVAILABLE || errorCode == RongIMClient.ErrorCode.RC_NET_CHANNEL_INVALID) {
                                    onWarningDialog(getString(R.string.rc_notice_network_unavailable));
                                } else {
                                    onWarningDialog(getString(R.string.rc_join_chatroom_failure));
                                }
                            }
                        }
                    });
                else
                    RongIMClient.getInstance().joinExistChatRoom(mTargetId, pullCount, new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            RLog.i(TAG, "joinExistChatRoom onSuccess : " + mTargetId);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            RLog.e(TAG, "joinExistChatRoom onError : " + errorCode);
                            if (getActivity() != null) {
                                if (errorCode == RongIMClient.ErrorCode.RC_NET_UNAVAILABLE || errorCode == RongIMClient.ErrorCode.RC_NET_CHANNEL_INVALID) {
                                    onWarningDialog(getString(R.string.rc_notice_network_unavailable));
                                } else {
                                    onWarningDialog(getString(R.string.rc_join_chatroom_failure));
                                }
                            }
                        }
                    });
            } else if (mConversationType == Conversation.ConversationType.APP_PUBLIC_SERVICE ||
                    mConversationType == Conversation.ConversationType.PUBLIC_SERVICE) {
                PublicServiceCommandMessage msg = new PublicServiceCommandMessage();
                msg.setCommand(PublicServiceMenu.PublicServiceMenuItemType.Entry.getMessage());
                io.rong.imlib.model.Message message = io.rong.imlib.model.Message.obtain(mTargetId, mConversationType, msg);
                RongIMClient.getInstance().sendMessage(message, null, null, new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(io.rong.imlib.model.Message message) {

                    }

                    @Override
                    public void onSuccess(io.rong.imlib.model.Message message) {

                    }

                    @Override
                    public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode) {

                    }
                });
                Conversation.PublicServiceType publicServiceType;
                if (mConversationType == Conversation.ConversationType.PUBLIC_SERVICE) {
                    publicServiceType = Conversation.PublicServiceType.PUBLIC_SERVICE;
                } else {
                    publicServiceType = Conversation.PublicServiceType.APP_PUBLIC_SERVICE;
                }
                RongIM.getInstance().getPublicServiceProfile(publicServiceType, mTargetId, new RongIMClient.ResultCallback<PublicServiceProfile>() {
                    @Override
                    public void onSuccess(PublicServiceProfile publicServiceProfile) {
                        List<InputMenu> inputMenuList = new ArrayList<>();
                        PublicServiceMenu menu = publicServiceProfile.getMenu();
                        ArrayList<PublicServiceMenuItem> items = menu != null ? menu.getMenuItems() : null;
                        if (items != null && mRongExtension != null) {
                            mPublicServiceProfile = publicServiceProfile;
                            for (PublicServiceMenuItem item : items) {
                                InputMenu inputMenu = new InputMenu();
                                inputMenu.title = item.getName();
                                inputMenu.subMenuList = new ArrayList<>();
                                for (PublicServiceMenuItem i : item.getSubMenuItems()) {
                                    inputMenu.subMenuList.add(i.getName());
                                }
                                inputMenuList.add(inputMenu);
                            }
                            mRongExtension.setInputMenu(inputMenuList, true);
                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode e) {

                    }
                });
            } else if (mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
                onStartCustomService(mTargetId);
            } else if (mEnableMention
                    && (mConversationType.equals(Conversation.ConversationType.DISCUSSION)
                    || mConversationType.equals(Conversation.ConversationType.GROUP))) {
                RongMentionManager.getInstance().createInstance(mConversationType, mTargetId, mRongExtension.getInputEditText());
            }
        }

        RongIMClient.getInstance().getConversation(mConversationType, mTargetId, new RongIMClient.ResultCallback<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                if (conversation != null && getActivity() != null) {
                    final int unreadCount = conversation.getUnreadMessageCount();
                    if (unreadCount > 0) {
                        if (mReadRec
                                && mConversationType == Conversation.ConversationType.PRIVATE
                                && RongContext.getInstance().isReadReceiptConversationType(Conversation.ConversationType.PRIVATE)) {
                            RongIMClient.getInstance().sendReadReceiptMessage(mConversationType, mTargetId, conversation.getSentTime());
                        }

                        if (mSyncReadStatus && (mConversationType == Conversation.ConversationType.PRIVATE
                                || mConversationType == Conversation.ConversationType.GROUP
                                || mConversationType == Conversation.ConversationType.DISCUSSION)) {
                            RongIMClient.getInstance().syncConversationReadStatus(mConversationType, mTargetId, conversation.getSentTime(), null);
                        }
                    }
                    if (conversation.getMentionedCount() > 0) {
                        getLastMentionedMessageId(mConversationType, mTargetId);
                    }

                    if (unreadCount > 10 && mUnreadBtn != null) {
                        if (unreadCount > 150) {
                            mUnreadBtn.setText(String.format("%s%s", "150+", getActivity().getResources().getString(R.string.rc_new_messages)));
                        } else {
                            mUnreadBtn.setText(String.format("%s%s", unreadCount, getActivity().getResources().getString(R.string.rc_new_messages)));
                        }
                        mUnreadBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mUnreadBtn.setClickable(false);
                                TranslateAnimation animation = new TranslateAnimation(0, 500, 0, 0);
                                animation.setDuration(500);
                                mUnreadBtn.startAnimation(animation);
                                animation.setFillAfter(true);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        mUnreadBtn.setVisibility(View.GONE);
                                        if (unreadCount <= DEFAULT_HISTORY_MESSAGE_COUNT) {
                                            if (mList.getCount() < DEFAULT_HISTORY_MESSAGE_COUNT) {
                                                mList.smoothScrollToPosition(mList.getCount() - unreadCount);
                                            } else {
                                                mList.smoothScrollToPosition(DEFAULT_HISTORY_MESSAGE_COUNT - unreadCount);
                                            }
                                        } else if (unreadCount > DEFAULT_HISTORY_MESSAGE_COUNT) {
                                            getHistoryMessage(mConversationType, mTargetId, unreadCount - DEFAULT_HISTORY_MESSAGE_COUNT - 1, SCROLL_MODE_TOP);
                                        }
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                            }
                        });
                        TranslateAnimation translateAnimation = new TranslateAnimation(300, 0, 0, 0);
                        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                        translateAnimation.setDuration(1000);
                        alphaAnimation.setDuration(2000);
                        AnimationSet set = new AnimationSet(true);
                        set.addAnimation(translateAnimation);
                        set.addAnimation(alphaAnimation);
                        mUnreadBtn.setVisibility(View.VISIBLE);
                        mUnreadBtn.startAnimation(set);
                        set.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                getHandler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        TranslateAnimation animation = new TranslateAnimation(0, 700, 0, 0);
                                        animation.setDuration(700);
                                        animation.setFillAfter(true);
                                        mUnreadBtn.startAnimation(animation);
                                    }
                                }, 4000);//进去6s 没做任何操作 未读条目淡出
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {

            }
        });

        getHistoryMessage(mConversationType, mTargetId, DEFAULT_HISTORY_MESSAGE_COUNT, SCROLL_MODE_BOTTOM);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    /**
     * 隐藏调用showNotificationView所显示的通知view
     *
     * @param notificationView
     */
    public void hideNotificationView(View notificationView) {
        if (notificationView == null) {
            return;
        }
        View view = mNotificationContainer.findViewById(notificationView.getId());
        if (view != null) {
            mNotificationContainer.removeView(view);
            if (mNotificationContainer.getChildCount() == 0) {
                mNotificationContainer.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 在通知区域显示一个view
     *
     * @param notificationView
     */
    public void showNotificationView(View notificationView) {
        if (notificationView == null) {
            return;
        }
        View view = mNotificationContainer.findViewById(notificationView.getId());
        if (view != null) {
            // do nothing, we already add the view, and the view would update automatically
            return;
        }
        mNotificationContainer.addView(notificationView);
        mNotificationContainer.setVisibility(View.VISIBLE);
    }

    /**
     * 用来生成需要显示在会话界面的通知view
     *
     * @return
     */
    public View inflateNotificationView(@LayoutRes int layout) {
        return LayoutInflater.from(getActivity()).inflate(layout, mNotificationContainer, false);
    }

    /**
     * 重发按钮点击事件.
     * 用户可以通过集成ConversaitonFragment然后重写此方法的方式自定义
     *
     * @param message
     * @return false: 走默认流程; true: 走自定义流程
     */

    public boolean onResendItemClick(io.rong.imlib.model.Message message) {
        return false;
    }

    /**
     * 回执详情按钮点击事件.
     * 用户可以通过集成ConversaitonFragment然后重写此方法的方式自定义
     *
     * @param message
     */
    public void onReadReceiptStateClick(io.rong.imlib.model.Message message) {

    }

    /**
     * 如果客服后台有分组,会弹出此对话框选择分组
     * 可以通过自定义类继承自 ConversationFragment 并重写此方法来自定义弹窗
     */
    public void onSelectCustomerServiceGroup(final List<CSGroupItem> groupList) {
        if (getActivity() == null) {
            return;
        }
        final SingleChoiceDialog singleChoiceDialog;
        List<String> singleDataList = new ArrayList<String>();
        singleDataList.clear();
        for (int i = 0; i < groupList.size(); i++) {
            if (groupList.get(i).getOnline()) {
                singleDataList.add(groupList.get(i).getName());
            }
        }
        if (singleDataList.size() == 0) {
            RongIMClient.getInstance().selectCustomServiceGroup(mTargetId, null);
            return;
        }
        singleChoiceDialog = new SingleChoiceDialog(getActivity(), singleDataList);
        singleChoiceDialog.setTitle(getActivity().getResources().getString(R.string.rc_cs_select_group));
        singleChoiceDialog.setOnOKButtonListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selItem = singleChoiceDialog.getSelectItem();
                RongIMClient.getInstance().selectCustomServiceGroup(mTargetId, groupList.get(selItem).getId());
            }

        });
        singleChoiceDialog.setOnCancelButtonListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RongIMClient.getInstance().selectCustomServiceGroup(mTargetId, null);
            }
        });
        singleChoiceDialog.show();
    }

    private boolean robotType = true;
    private long csEnterTime;
    private boolean csEvaluate = true;


    ICustomServiceListener customServiceListener = new ICustomServiceListener() {
        @Override
        public void onSuccess(CustomServiceConfig config) {
            mCustomServiceConfig = config;

            if (config.isBlack) {
                onCustomServiceWarning(getString(R.string.rc_blacklist_prompt), false, robotType);
            }
            if (config.robotSessionNoEva) {
                csEvaluate = false;
                mListAdapter.setEvaluateForRobot(true);
            }

            if (mRongExtension != null) {
                if (config.evaEntryPoint.equals(CustomServiceConfig.CSEvaEntryPoint.EVA_EXTENSION)) {
                    mRongExtension.addPlugin(new EvaluatePlugin(mCustomServiceConfig.isReportResolveStatus));
                }

                if (config.isDisableLocation) {
                    List<IPluginModule> defaultPlugins = mRongExtension.getPluginModules();
                    IPluginModule location = null;
                    for (int i = 0; i < defaultPlugins.size(); i++) {
                        if (defaultPlugins.get(i) instanceof DefaultLocationPlugin) {
                            location = defaultPlugins.get(i);
                        }
                    }
                    if (location != null) {
                        mRongExtension.removePlugin(location);
                    }
                }
            }

            if (config.quitSuspendType.equals(CustomServiceConfig.CSQuitSuspendType.NONE)) {
                try {
                    mCSNeedToQuit = RongContext.getInstance().getResources().getBoolean(R.bool.rc_stop_custom_service_when_quit);
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                mCSNeedToQuit = config.quitSuspendType.equals(CustomServiceConfig.CSQuitSuspendType.SUSPEND);
            }

            for (int i = 0; i < mListAdapter.getCount(); i++) {
                UIMessage uiMessage = mListAdapter.getItem(i);
                if (uiMessage.getContent() instanceof CSPullLeaveMessage) {
                    uiMessage.setCsConfig(config);
                }
            }
            mListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onError(int code, String msg) {
            onCustomServiceWarning(msg, false, robotType);
        }

        @Override
        public void onModeChanged(CustomServiceMode mode) {
            if (mRongExtension == null) {
                return;
            }
            mRongExtension.setExtensionBarMode(mode);
            if (mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN)
                    || mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN_FIRST)) {
                if (mCustomServiceConfig.userTipTime > 0 && !TextUtils.isEmpty(mCustomServiceConfig.userTipWord)) {
                    startTimer(CS_HUMAN_MODE_CUSTOMER_EXPIRE, mCustomServiceConfig.userTipTime * 60 * 1000);
                }
                if (mCustomServiceConfig.adminTipTime > 0 && !TextUtils.isEmpty(mCustomServiceConfig.adminTipWord)) {
                    startTimer(CS_HUMAN_MODE_SEAT_EXPIRE, mCustomServiceConfig.adminTipTime * 60 * 1000);
                }

                robotType = false;
                csEvaluate = true;
            } else if (mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_NO_SERVICE)) {
                csEvaluate = false;
            }
        }

        @Override
        public void onQuit(String msg) {
            RLog.i(TAG, "CustomService onQuit.");
            stopTimer(CS_HUMAN_MODE_CUSTOMER_EXPIRE);
            stopTimer(CS_HUMAN_MODE_SEAT_EXPIRE);
            if (mEvaluateDialg == null) {
                onCustomServiceWarning(msg, mCustomServiceConfig.quitSuspendType == CustomServiceConfig.CSQuitSuspendType.NONE, robotType);
            } else {
                mEvaluateDialg.destroy();
            }

            if (!mCustomServiceConfig.quitSuspendType.equals(CustomServiceConfig.CSQuitSuspendType.NONE)) {
                RongContext.getInstance().getEventBus().post(new Event.CSTerminateEvent(getActivity(), msg));
            }
        }

        @Override
        public void onPullEvaluation(String dialogId) {
            if (mEvaluateDialg == null) {
                onCustomServiceEvaluation(true, dialogId, robotType, csEvaluate);
            }
        }

        @Override
        public void onSelectGroup(List<CSGroupItem> groups) {
            onSelectCustomerServiceGroup(groups);
        }
    };

    /**
     * 当收取大量离线消息时，{@link #onDestroy()} 会被延迟调用，{@link RongExtension} 中
     * {@link IExtensionModule#onDetachedFromExtension()} 也会被延迟。频繁进入会话界面，
     * {@link IExtensionModule#onAttachedToExtension(RongExtension)} 两者时序错乱。
     * 通过在 {@link Activity#isFinishing()} 判断 Activity 是否将要结束，决定 RongExtension 的销毁。
     */
    @Override
    public void onPause() {
        if (getActivity().isFinishing()) {
            RongIM.getInstance().clearMessagesUnreadStatus(mConversationType, mTargetId, null);
            stopTimer(CS_HUMAN_MODE_SEAT_EXPIRE);
            stopTimer(CS_HUMAN_MODE_CUSTOMER_EXPIRE);

            if (mEnableMention
                    && (mConversationType.equals(Conversation.ConversationType.DISCUSSION)
                    || (mConversationType.equals(Conversation.ConversationType.GROUP)))) {
                RongMentionManager.getInstance().destroyInstance(mConversationType, mTargetId);
            }
            if (mConversationType.equals(Conversation.ConversationType.CHATROOM)) {
                SendImageManager.getInstance().cancelSendingImages(mConversationType, mTargetId);
                RongIM.getInstance().quitChatRoom(mTargetId, null);
            }
            if (mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE) && mCSNeedToQuit) {
                onStopCustomService(mTargetId);
            }
            if (mSyncReadStatus
                    && mSyncReadStatusMsgTime > 0
                    && ((mConversationType.equals(Conversation.ConversationType.DISCUSSION))
                    || mConversationType.equals(Conversation.ConversationType.GROUP))) {
                RongIMClient.getInstance().syncConversationReadStatus(mConversationType, mTargetId, mSyncReadStatusMsgTime, null);
            }

            EventBus.getDefault().unregister(this);
            AudioPlayManager.getInstance().stopPlay();
            AudioRecordManager.getInstance().destroyRecord();
            try {
                if (mKitReceiver != null) {
                    getActivity().unregisterReceiver(mKitReceiver);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            RongContext.getInstance().unregisterConversationInfo(mCurrentConversationInfo);
            LocationManager.getInstance().quitLocationSharing();
            LocationManager.getInstance().setParticipantChangedListener(null);
            LocationManager.getInstance().setUserInfoProvider(null);
            LocationManager.getInstance().unBindConversation();
            destroyExtension();
        }
        super.onPause();
    }

    private void destroyExtension() {
        final String text = mRongExtension.getInputEditText().getText().toString();
        if ((TextUtils.isEmpty(text) && !TextUtils.isEmpty(mDraft))
                || (!TextUtils.isEmpty(text) && TextUtils.isEmpty(mDraft))
                || (!TextUtils.isEmpty(text) && !TextUtils.isEmpty(mDraft) && !text.equals(mDraft))) {
            RongIMClient.getInstance().saveTextMessageDraft(mConversationType, mTargetId, text, null);
            Event.DraftEvent draft = new Event.DraftEvent(mConversationType, mTargetId, text);
            RongContext.getInstance().getEventBus().post(draft);
        }
        mRongExtension.onDestroy();
        mRongExtension = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean isLocationSharing() {
        return LocationManager.getInstance().isSharing();
    }

    public void showQuitLocationSharingDialog(final Activity activity) {
        PromptPopupDialog.newInstance(activity, getString(R.string.rc_ext_warning), getString(R.string.rc_real_time_exit_notification), getString(R.string.rc_action_bar_ok))
                .setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
                    @Override
                    public void onPositiveButtonClicked() {
                        activity.finish();
                    }
                }).show();
    }

    @Override
    public boolean onBackPressed() {
        if (mRongExtension != null && mRongExtension.isExtensionExpanded()) {
            mRongExtension.collapseExtension();
            return true;
        }
        if (mConversationType != null && mCustomServiceConfig != null
                && mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)
                && mCustomServiceConfig.quitSuspendType.equals(CustomServiceConfig.CSQuitSuspendType.NONE))
            return onCustomServiceEvaluation(false, "", robotType, csEvaluate);
        else
            return false;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case CS_HUMAN_MODE_CUSTOMER_EXPIRE: {
                if (getActivity() == null) {
                    return true;
                }
                InformationNotificationMessage info = new InformationNotificationMessage(mCustomServiceConfig.userTipWord);
                RongIM.getInstance().insertMessage(Conversation.ConversationType.CUSTOMER_SERVICE, mTargetId, mTargetId, info, System.currentTimeMillis(), null);
                return true;
            }
            case CS_HUMAN_MODE_SEAT_EXPIRE: {
                if (getActivity() == null) {
                    return true;
                }
                InformationNotificationMessage info = new InformationNotificationMessage(mCustomServiceConfig.adminTipWord);
                RongIM.getInstance().insertMessage(Conversation.ConversationType.CUSTOMER_SERVICE, mTargetId, mTargetId, info, System.currentTimeMillis(), null);
                return true;
            }
        }

        return false;
    }

    /**
     * 提示dialog.
     * 例如"加入聊天室失败"的dialog
     * 用户自定义此dialog的步骤:
     * 1.定义一个类继承自 ConversationFragment
     * 2.重写 onWarningDialog
     *
     * @param msg dialog 提示
     */
    public void onWarningDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.rc_cs_alert_warning);
        TextView tv = (TextView) window.findViewById(R.id.rc_cs_msg);
        tv.setText(msg);

        window.findViewById(R.id.rc_btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                FragmentManager fm = getChildFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                    getActivity().finish();
                }
            }
        });
    }

    /**
     * <p>弹出客服提示信息</p>
     * 通过重写此方法可以自定义弹出提示的窗口
     *
     * @param msg       提示的内容
     * @param evaluate  是否需要评价. true 表示还需要弹出评价窗口进行评价, false 表示仅需要提示不需要评价
     * @param robotType 是否是机器人模式
     */
    public void onCustomServiceWarning(String msg, final boolean evaluate, final boolean robotType) {
        if (getActivity() == null)
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.rc_cs_alert_warning);
        TextView tv = (TextView) window.findViewById(R.id.rc_cs_msg);
        tv.setText(msg);

        window.findViewById(R.id.rc_btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                alertDialog.dismiss();
                if (evaluate) {
                    onCustomServiceEvaluation(false, "", robotType, evaluate);
                } else {
                    FragmentManager fm = getChildFragmentManager();
                    if (fm.getBackStackEntryCount() > 0)
                        fm.popBackStack();
                    else
                        getActivity().finish();
                }
            }
        });
    }

    /**
     * <p>客服弹出评价并提交评价</p>
     * 通过重写此方法 App 可以自定义评价的弹出窗口和评价的提交
     *
     * @param isPullEva 是否是客服后台主动拉评价，如果为 false,则需要判断在客服界面停留的时间是否超过60秒,
     *                  超过这个时间则弹出评价窗口,否则不弹; 如果为 true,则不论停留时间为多少都要弹出评价窗口
     * @param dialogId  会话 Id. 客服后台主动拉评价的时候这个参数有效
     * @param robotType 是否为机器人模式,true 表示机器人模式,false 表示人工模式
     * @param evaluate  是否需要评价. true 表示需要弹出评价窗口进行评价, false 不需要弹出评价窗口
     *                  例如有些客服不需要针对整个会话评价,只需要针对每条回复评价,这个时候 evaluate 为 false
     * @return true: 已弹出评价, false:未弹出评价
     */
    public boolean onCustomServiceEvaluation(boolean isPullEva, final String dialogId, final boolean robotType, boolean evaluate) {
        if (evaluate && getActivity() != null) {
            long currentTime = System.currentTimeMillis();
            int interval = 60;
            try {
                interval = getActivity().getResources().getInteger(R.integer.rc_custom_service_evaluation_interval);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
            if ((currentTime - csEnterTime < interval * 1000) && !isPullEva) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && imm.isActive() && getActivity().getCurrentFocus() != null) {
                    if (getActivity().getCurrentFocus().getWindowToken() != null) {
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                FragmentManager fm = getChildFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                    getActivity().finish();
                }
                return false;
            } else {
                mEvaluateDialg = new CSEvaluateDialog(getContext(), mTargetId);
                mEvaluateDialg.setClickListener(this);
                mEvaluateDialg.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (mEvaluateDialg != null) {
                            mEvaluateDialg = null;
                        }
                    }
                });
                if (mCustomServiceConfig.evaluateType.equals(CustomServiceConfig.CSEvaType.EVA_UNIFIED)) {
                    mEvaluateDialg.showStarMessage(mCustomServiceConfig.isReportResolveStatus);
                } else if (robotType) {
                    mEvaluateDialg.showRobot(true);
                } else {
                    mEvaluateDialg.showStar(dialogId);
                }
            }
        }
        return true;
    }

    @Override
    public void onSendToggleClick(View v, String text) {
        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(text.trim())) {
            RLog.e(TAG, "text content must not be null");
            return;
        }

        TextMessage textMessage = TextMessage.obtain(text);
        MentionedInfo mentionedInfo = RongMentionManager.getInstance().onSendButtonClick();
        if (mentionedInfo != null) {
            textMessage.setMentionedInfo(mentionedInfo);
        }
        io.rong.imlib.model.Message message = io.rong.imlib.model.Message.obtain(mTargetId, mConversationType, textMessage);
        RongIM.getInstance().sendMessage(message, null, null, (IRongCallback.ISendMessageCallback) null);
    }

    @Override
    public void onImageResult(List<Uri> selectedImages, boolean origin) {
        SendImageManager.getInstance().sendImages(mConversationType, mTargetId, selectedImages, origin);
        if (mConversationType.equals(Conversation.ConversationType.PRIVATE)) {
            RongIMClient.getInstance().sendTypingStatus(mConversationType, mTargetId, "RC:ImgMsg");
        }
    }

    @Override
    public void onEditTextClick(EditText editText) {

    }

    @Override
    public void onLocationResult(double lat, double lng, String poi, Uri thumb) {
        LocationMessage locationMessage = LocationMessage.obtain(lat, lng, poi, thumb);
        io.rong.imlib.model.Message message = io.rong.imlib.model.Message.obtain(mTargetId, mConversationType, locationMessage);
        RongIM.getInstance().sendLocationMessage(message, null, null, null);
        if (mConversationType.equals(Conversation.ConversationType.PRIVATE)) {
            RongIMClient.getInstance().sendTypingStatus(mConversationType, mTargetId, "RC:LBSMsg");
        }
    }

    @Override
    public void onSwitchToggleClick(View v, ViewGroup inputBoard) {
        if (robotType) {
            RongIMClient.getInstance().switchToHumanMode(mTargetId);
        }
    }

    @Override
    public void onVoiceInputToggleTouch(View v, MotionEvent event) {
        String[] permissions = {Manifest.permission.RECORD_AUDIO};
        if (!PermissionCheckUtil.checkPermissions(getActivity(), permissions)) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                PermissionCheckUtil.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            AudioPlayManager.getInstance().stopPlay();
            AudioRecordManager.getInstance().startRecord(v.getRootView(), mConversationType, mTargetId);
            mLastTouchY = event.getY();
            mUpDirection = false;
            ((Button) v).setText(R.string.rc_audio_input_hover);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (mLastTouchY - event.getY() > mOffsetLimit && !mUpDirection) {
                AudioRecordManager.getInstance().willCancelRecord();
                mUpDirection = true;
                ((Button) v).setText(R.string.rc_audio_input);
            } else if (event.getY() - mLastTouchY > -mOffsetLimit && mUpDirection) {
                AudioRecordManager.getInstance().continueRecord();
                mUpDirection = false;
                ((Button) v).setText(R.string.rc_audio_input_hover);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            AudioRecordManager.getInstance().stopRecord();
            ((Button) v).setText(R.string.rc_audio_input);
        }
        if (mConversationType.equals(Conversation.ConversationType.PRIVATE)) {
            RongIMClient.getInstance().sendTypingStatus(mConversationType, mTargetId, "RC:VcMsg");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), getResources().getString(R.string.rc_permission_grant_needed), Toast.LENGTH_SHORT).show();
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onEmoticonToggleClick(View v, ViewGroup extensionBoard) {

    }

    @Override
    public void onPluginToggleClick(View v, ViewGroup extensionBoard) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int cursor, offset;
        if (count == 0) {
            cursor = start + before;
            offset = -before;
        } else {
            cursor = start;
            offset = count;
        }
        if (mConversationType.equals(Conversation.ConversationType.GROUP) || mConversationType.equals(Conversation.ConversationType.DISCUSSION)) {
            RongMentionManager.getInstance().onTextEdit(mConversationType, mTargetId, cursor, offset, s.toString());
        } else if (mConversationType.equals(Conversation.ConversationType.PRIVATE) && offset != 0) {
            RongIMClient.getInstance().sendTypingStatus(mConversationType, mTargetId, "RC:TxtMsg");
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
            EditText editText = (EditText) v;
            int cursorPos = editText.getSelectionStart();
            RongMentionManager.getInstance().onDeleteClick(mConversationType, mTargetId, editText, cursorPos);
        }
        return false;
    }

    @Override
    public void onMenuClick(int root, int sub) {
        if (mPublicServiceProfile != null) {
            PublicServiceMenuItem item = mPublicServiceProfile.getMenu().getMenuItems().get(root);
            if (sub >= 0) {
                item = item.getSubMenuItems().get(sub);
            }
            if (item.getType().equals(PublicServiceMenu.PublicServiceMenuItemType.View)) {
                IPublicServiceMenuClickListener menuClickListener = RongContext.getInstance().getPublicServiceMenuClickListener();
                if (menuClickListener == null || !menuClickListener.onClick(mConversationType, mTargetId, item)) {
                    String action = RongKitIntent.RONG_INTENT_ACTION_WEBVIEW;
                    Intent intent = new Intent(action);
                    intent.setPackage(getActivity().getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("url", item.getUrl());
                    getActivity().startActivity(intent);
                }
            }

            PublicServiceCommandMessage msg = PublicServiceCommandMessage.obtain(item);
            RongIMClient.getInstance().sendMessage(mConversationType, mTargetId, msg, null, null, new IRongCallback.ISendMessageCallback() {
                @Override
                public void onAttached(io.rong.imlib.model.Message message) {

                }

                @Override
                public void onSuccess(io.rong.imlib.model.Message message) {

                }

                @Override
                public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode) {

                }
            });

        }
    }

    @Override
    public void onPluginClicked(IPluginModule pluginModule, int position) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果结束界面是客服留言界面，则同步结束会话界面
        if (requestCode == REQUEST_CS_LEAVEL_MESSAGE) {
            getActivity().finish();
        } else {
            mRongExtension.onActivityPluginResult(requestCode, resultCode, data);
        }
    }

    private String getNameFromCache(String targetId) {
        UserInfo info = RongContext.getInstance().getUserInfoFromCache(targetId);
        return info == null ? targetId : info.getName();
    }


    final public void onEventMainThread(Event.ReadReceiptRequestEvent event) {
        RLog.d(TAG, "ReadReceiptRequestEvent");

        if (mConversationType.equals(Conversation.ConversationType.GROUP) || mConversationType.equals(Conversation.ConversationType.DISCUSSION)) {
            if (RongContext.getInstance().isReadReceiptConversationType(event.getConversationType())) {
                if (event.getConversationType().equals(mConversationType) && event.getTargetId().equals(mTargetId)) {
                    for (int i = 0; i < mListAdapter.getCount(); i++) {
                        if (mListAdapter.getItem(i).getUId().equals(event.getMessageUId())) {
                            final UIMessage uiMessage = mListAdapter.getItem(i);
                            ReadReceiptInfo readReceiptInfo = uiMessage.getReadReceiptInfo();
                            if (readReceiptInfo == null) {
                                readReceiptInfo = new ReadReceiptInfo();
                                uiMessage.setReadReceiptInfo(readReceiptInfo);
                            }
                            if (readReceiptInfo.isReadReceiptMessage() && readReceiptInfo.hasRespond()) {
                                return;
                            }
                            readReceiptInfo.setIsReadReceiptMessage(true);
                            readReceiptInfo.setHasRespond(false);
                            List<io.rong.imlib.model.Message> messageList = new ArrayList<>();
                            messageList.add((mListAdapter.getItem(i)).getMessage());
                            RongIMClient.getInstance().sendReadReceiptResponse(event.getConversationType(), event.getTargetId(), messageList, new RongIMClient.OperationCallback() {
                                @Override
                                public void onSuccess() {
                                    uiMessage.getReadReceiptInfo().setHasRespond(true);
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    RLog.e(TAG, "sendReadReceiptResponse failed, errorCode = " + errorCode);
                                }
                            });
                            break;
                        }
                    }
                }
            }
        }
    }

    final public void onEventMainThread(Event.ReadReceiptResponseEvent event) {
        RLog.d(TAG, "ReadReceiptResponseEvent");

        if (mConversationType.equals(Conversation.ConversationType.GROUP) || mConversationType.equals(Conversation.ConversationType.DISCUSSION)) {
            if (RongContext.getInstance().isReadReceiptConversationType(event.getConversationType()) &&
                    event.getConversationType().equals(mConversationType) &&
                    event.getTargetId().equals(mTargetId)) {
                for (int i = 0; i < mListAdapter.getCount(); i++) {
                    if (mListAdapter.getItem(i).getUId().equals(event.getMessageUId())) {
                        UIMessage uiMessage = mListAdapter.getItem(i);
                        ReadReceiptInfo readReceiptInfo = uiMessage.getReadReceiptInfo();
                        if (readReceiptInfo == null) {
                            readReceiptInfo = new ReadReceiptInfo();
                            readReceiptInfo.setIsReadReceiptMessage(true);
                            uiMessage.setReadReceiptInfo(readReceiptInfo);
                        }
                        readReceiptInfo.setRespondUserIdList(event.getResponseUserIdList());
                        int first = mList.getFirstVisiblePosition();
                        int last = mList.getLastVisiblePosition();
                        int position = getPositionInListView(i);
                        if (position >= first && position <= last) {
                            mListAdapter.getView(i, getListViewChildAt(i), mList);
                        }
                        break;
                    }
                }
            }
        }
    }

    final public void onEventMainThread(Event.MessageDeleteEvent deleteEvent) {
        RLog.d(TAG, "MessageDeleteEvent");

        if (deleteEvent.getMessageIds() != null) {
            for (long messageId : deleteEvent.getMessageIds()) {
                int position = mListAdapter.findPosition(messageId);
                if (position >= 0) {
                    mListAdapter.remove(position);
                }
            }
            mListAdapter.notifyDataSetChanged();
        }
    }

    final public void onEventMainThread(Event.PublicServiceFollowableEvent event) {
        RLog.d(TAG, "PublicServiceFollowableEvent");

        if (event != null && !event.isFollow()) {
            getActivity().finish();
        }
    }

    final public void onEventMainThread(Event.MessagesClearEvent clearEvent) {
        RLog.d(TAG, "MessagesClearEvent");
        if (clearEvent.getTargetId().equals(mTargetId) && clearEvent.getType().equals(mConversationType)) {
            mListAdapter.clear();
            mListAdapter.notifyDataSetChanged();
        }
    }

    final public void onEventMainThread(Event.MessageRecallEvent event) {
        RLog.d(TAG, "MessageRecallEvent");

        if (event.isRecallSuccess()) {
            RecallNotificationMessage recallNotificationMessage = event.getRecallNotificationMessage();
            int position = mListAdapter.findPosition(event.getMessageId());
            if (position != -1) {
                mListAdapter.getItem(position).setContent(recallNotificationMessage);
                int first = mList.getFirstVisiblePosition();
                int last = mList.getLastVisiblePosition();
                int listPos = getPositionInListView(position);
                if (listPos >= first && listPos <= last) {
                    mListAdapter.getView(position, getListViewChildAt(position), mList);
                }
            }
        } else {
            Toast.makeText(getActivity(), R.string.rc_recall_failed, Toast.LENGTH_SHORT).show();
        }
    }

    final public void onEventMainThread(Event.RemoteMessageRecallEvent event) {
        RLog.d(TAG, "RemoteMessageRecallEvent");

        int position = mListAdapter.findPosition(event.getMessageId());
        int first = mList.getFirstVisiblePosition();
        int last = mList.getLastVisiblePosition();
        if (position >= 0) {
            UIMessage uiMessage = mListAdapter.getItem(position);
            if (uiMessage.getMessage().getContent() instanceof VoiceMessage) {
                AudioPlayManager.getInstance().stopPlay();
            }
            if (uiMessage.getMessage().getContent() instanceof FileMessage) {
                RongIM.getInstance().cancelDownloadMediaMessage(uiMessage.getMessage(), null);
            }
            uiMessage.setContent(event.getRecallNotificationMessage());
            int listPos = getPositionInListView(position);
            if (listPos >= first && listPos <= last) {
                mListAdapter.getView(position, getListViewChildAt(position), mList);
            }
        }
    }

    final public void onEventMainThread(io.rong.imlib.model.Message msg) {
        RLog.d(TAG, "Event message : " + msg.getMessageId() + ", " + msg.getObjectName() + ", " + msg.getSentStatus());

        if (mTargetId.equals(msg.getTargetId())
                && mConversationType.equals(msg.getConversationType())
                && msg.getMessageId() > 0) {
            int position = mListAdapter.findPosition(msg.getMessageId());
            if (position >= 0) {
                mListAdapter.getItem(position).setMessage(msg);
                mListAdapter.getView(position, getListViewChildAt(position), mList);
            } else {
                UIMessage uiMessage = UIMessage.obtain(msg);
                if (msg.getContent() instanceof CSPullLeaveMessage) {
                    uiMessage.setCsConfig(mCustomServiceConfig);
                }
                mListAdapter.add(uiMessage);
                mListAdapter.notifyDataSetChanged();
            }
            if (msg.getSenderUserId() != null && msg.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId())
                    && mList.getLastVisiblePosition() - 1 != mList.getCount()) {
                mList.smoothScrollToPosition(mList.getCount());
            }

            if (mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)
                    && msg.getMessageDirection() == io.rong.imlib.model.Message.MessageDirection.SEND
                    && !robotType
                    && mCustomServiceConfig.userTipTime > 0
                    && !TextUtils.isEmpty(mCustomServiceConfig.userTipWord)) {
                startTimer(CS_HUMAN_MODE_CUSTOMER_EXPIRE, mCustomServiceConfig.userTipTime * 60 * 1000);
            }

        }
    }

    final public void onEventMainThread(Event.FileMessageEvent event) {
        io.rong.imlib.model.Message msg = event.getMessage();
        RLog.d(TAG, "FileMessageEvent message : " + msg.getMessageId() + ", " + msg.getObjectName() + ", " + msg.getSentStatus());

        if (mTargetId.equals(msg.getTargetId())
                && mConversationType.equals(msg.getConversationType())
                && msg.getMessageId() > 0) {
            int position = mListAdapter.findPosition(msg.getMessageId());
            if (position >= 0) {
                UIMessage uiMessage = mListAdapter.getItem(position);
                uiMessage.setMessage(msg);
                uiMessage.setProgress(event.getProgress());
                mListAdapter.getItem(position).setMessage(msg);
                mListAdapter.getView(position, getListViewChildAt(position), mList);
            } else {
                UIMessage uiMessage = UIMessage.obtain(msg);
                uiMessage.setProgress(event.getProgress());
                mListAdapter.add(uiMessage);
                mListAdapter.notifyDataSetChanged();
            }
            if (msg.getSenderUserId() != null && msg.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId())
                    && mList.getLastVisiblePosition() - 1 != mList.getCount()) {
                mList.smoothScrollToPosition(mList.getCount());
            }

            if (mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)
                    && msg.getMessageDirection() == io.rong.imlib.model.Message.MessageDirection.SEND
                    && !robotType
                    && mCustomServiceConfig.userTipTime > 0
                    && !TextUtils.isEmpty(mCustomServiceConfig.userTipWord)) {
                startTimer(CS_HUMAN_MODE_CUSTOMER_EXPIRE, mCustomServiceConfig.userTipTime * 60 * 1000);
            }

        }
    }

    final public void onEventMainThread(GroupUserInfo groupUserInfo) {
        RLog.d(TAG, "GroupUserInfoEvent " + groupUserInfo.getGroupId() + " " + groupUserInfo.getUserId() + " " + groupUserInfo.getNickname());
        if (groupUserInfo.getNickname() == null || groupUserInfo.getGroupId() == null) {
            return;
        }
        int count = mListAdapter.getCount();
        int first = mList.getFirstVisiblePosition();
        int last = mList.getLastVisiblePosition();
        for (int i = 0; i < count; i++) {
            UIMessage uiMessage = mListAdapter.getItem(i);
            if (uiMessage.getSenderUserId().equals(groupUserInfo.getUserId())) {
                uiMessage.setNickName(true);
                UserInfo userInfo = uiMessage.getUserInfo();
                if (userInfo != null) {
                    userInfo.setName(groupUserInfo.getNickname());
                } else {
                    userInfo = new UserInfo(groupUserInfo.getUserId(), groupUserInfo.getNickname(), null);
                }
                uiMessage.setUserInfo(userInfo);
                int pos = getPositionInListView(i);
                if (pos >= first && pos <= last) {
                    mListAdapter.getView(i, getListViewChildAt(i), mList);
                }
            }
        }
    }

    private View getListViewChildAt(int adapterIndex) {
        int header = mList.getHeaderViewsCount();
        int first = mList.getFirstVisiblePosition();
        return mList.getChildAt(adapterIndex + header - first);
    }

    private int getPositionInListView(int adapterIndex) {
        int header = mList.getHeaderViewsCount();
        return adapterIndex + header;
    }

    private int getPositionInAdapter(int listIndex) {
        int header = mList.getHeaderViewsCount();
        return listIndex <= 0 ? 0 : listIndex - header;
    }

    final public void onEventMainThread(Event.OnMessageSendErrorEvent event) {
        onEventMainThread(event.getMessage());
    }

    final public void onEventMainThread(Event.OnReceiveMessageEvent event) {
        io.rong.imlib.model.Message message = event.getMessage();
        RLog.i(TAG, "OnReceiveMessageEvent, " + message.getMessageId() + ", " + message.getObjectName() + ", " + message.getReceivedStatus().toString());
        Conversation.ConversationType conversationType = message.getConversationType();
        String targetId = message.getTargetId();
        if (mConversationType.equals(conversationType)
                && mTargetId.equals(targetId)
                && shouldUpdateMessage(message, event.getLeft())) {
            if (event.getLeft() == 0) {
                if (message.getConversationType().equals(Conversation.ConversationType.PRIVATE)
                        && RongContext.getInstance().isReadReceiptConversationType(Conversation.ConversationType.PRIVATE)
                        && message.getMessageDirection().equals(io.rong.imlib.model.Message.MessageDirection.RECEIVE)) {
                    if (mReadRec) {
                        RongIMClient.getInstance().sendReadReceiptMessage(message.getConversationType(), message.getTargetId(), message.getSentTime());
                    }
                    /**
                     * 只在单聊中同步多端未读数，是为了减少群组和讨论组中的消息量。会在会话页面销毁的时候同步未读消息数
                     */
                    if (mSyncReadStatus) {
                        RongIMClient.getInstance().syncConversationReadStatus(message.getConversationType(), message.getTargetId(), message.getSentTime(), null);
                    }
                }
            }
            if (mSyncReadStatus) {
                mSyncReadStatusMsgTime = message.getSentTime();
            }
            if (message.getMessageId() > 0) {
                io.rong.imlib.model.Message.ReceivedStatus status = message.getReceivedStatus();
                status.setRead();
                message.setReceivedStatus(status);
                RongIMClient.getInstance().setMessageReceivedStatus(message.getMessageId(), status, null);
                if (mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)
                        && !robotType
                        && mCustomServiceConfig.adminTipTime > 0
                        && !TextUtils.isEmpty(mCustomServiceConfig.adminTipWord)) {
                    startTimer(CS_HUMAN_MODE_SEAT_EXPIRE, mCustomServiceConfig.adminTipTime * 60 * 1000);
                }

            }

            if (mNewMessageBtn != null
                    && mList.getCount() - mList.getLastVisiblePosition() > TIP_DEFAULT_MESSAGE_COUNT
                    && io.rong.imlib.model.Message.MessageDirection.SEND != message.getMessageDirection()) {

                if (message.getConversationType() != Conversation.ConversationType.CHATROOM
                        && message.getConversationType() != Conversation.ConversationType.CUSTOMER_SERVICE
                        && message.getConversationType() != Conversation.ConversationType.APP_PUBLIC_SERVICE
                        && message.getConversationType() != Conversation.ConversationType.PUBLIC_SERVICE) {

                    mNewMessageCount++;
                    if (mNewMessageCount > 0) {
                        mNewMessageBtn.setVisibility(View.VISIBLE);
                        mNewMessageTextView.setVisibility(View.VISIBLE);
                    }
                    if (mNewMessageCount > 99) {
                        mNewMessageTextView.setText("99+");
                    } else {
                        mNewMessageTextView.setText(mNewMessageCount + "");
                    }
                }
            }

            onEventMainThread(event.getMessage());
        }
    }

    // 解决连续播放，如果不切换线程，会导致递归调用
    final public void onEventBackgroundThread(final Event.PlayAudioEvent event) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                handleAudioPlayEvent(event);
            }
        });
    }

    private void handleAudioPlayEvent(Event.PlayAudioEvent event) {
        RLog.i(TAG, "PlayAudioEvent");

        int first = mList.getFirstVisiblePosition();
        int last = mList.getLastVisiblePosition();
        int position = mListAdapter.findPosition(event.messageId);
        if (event.continuously && position >= 0) {
            while (first <= last) {
                position++;
                first++;
                UIMessage uiMessage = mListAdapter.getItem(position);
                if (uiMessage != null && (uiMessage.getContent() instanceof VoiceMessage)
                        && (uiMessage.getMessageDirection().equals(io.rong.imlib.model.Message.MessageDirection.RECEIVE) && !uiMessage.getReceivedStatus().isListened())) {
                    uiMessage.continuePlayAudio = true;
                    mListAdapter.getView(position, getListViewChildAt(position), mList);
                    break;
                }
            }
        }
    }

    final public void onEventMainThread(Event.OnReceiveMessageProgressEvent event) {
        if (mList != null) {
            int first = mList.getFirstVisiblePosition();
            int last = mList.getLastVisiblePosition();
            while (first <= last) {
                int position = getPositionInAdapter(first);
                UIMessage uiMessage = mListAdapter.getItem(position);
                if (uiMessage.getMessageId() == event.getMessage().getMessageId()) {
                    uiMessage.setProgress(event.getProgress());
                    if (isResumed()) {
                        mListAdapter.getView(position, getListViewChildAt(position), mList);
                    }
                    break;
                }
                first++;
            }
        }
    }

    final public void onEventMainThread(UserInfo userInfo) {
        RLog.i(TAG, "userInfo " + userInfo.getUserId());
        int first = mList.getFirstVisiblePosition();
        int last = mList.getLastVisiblePosition();

        for (int i = 0; i < mListAdapter.getCount(); i++) {
            UIMessage uiMessage = mListAdapter.getItem(i);
            if (userInfo.getUserId().equals(uiMessage.getSenderUserId())
                    && !uiMessage.isNickName()) {
                if (uiMessage.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE)
                        && uiMessage.getMessage() != null
                        && uiMessage.getMessage().getContent() != null
                        && uiMessage.getMessage().getContent().getUserInfo() != null) {
                    uiMessage.setUserInfo(uiMessage.getMessage().getContent().getUserInfo());
                } else {
                    uiMessage.setUserInfo(userInfo);
                }
                int position = getPositionInListView(i);
                if (position >= first && position <= last) {
                    mListAdapter.getView(i, getListViewChildAt(i), mList);
                }
            }
        }
    }

    final public void onEventMainThread(PublicServiceProfile publicServiceProfile) {
        RLog.i(TAG, "publicServiceProfile");
        int first = mList.getFirstVisiblePosition();
        int last = mList.getLastVisiblePosition();
        while (first <= last) {
            int position = getPositionInAdapter(first);
            UIMessage message = mListAdapter.getItem(position);
            if (message != null && (TextUtils.isEmpty(message.getTargetId())
                    || publicServiceProfile.getTargetId().equals(message.getTargetId()))) {
                mListAdapter.getView(position, getListViewChildAt(position), mList);
            }
            first++;
        }
    }

    final public void onEventMainThread(final Event.ReadReceiptEvent event) {
        RLog.i(TAG, "ReadReceiptEvent");
        if (RongContext.getInstance().isReadReceiptConversationType(event.getMessage().getConversationType())) {
            if (mTargetId.equals(event.getMessage().getTargetId())
                    && mConversationType.equals(event.getMessage().getConversationType())
                    && event.getMessage().getMessageDirection().equals(io.rong.imlib.model.Message.MessageDirection.RECEIVE)) {
                ReadReceiptMessage content = (ReadReceiptMessage) event.getMessage().getContent();
                long ntfTime = content.getLastMessageSendTime();
                for (int i = mListAdapter.getCount() - 1; i >= 0; i--) {
                    UIMessage uiMessage = mListAdapter.getItem(i);
                    if (uiMessage.getMessageDirection().equals(io.rong.imlib.model.Message.MessageDirection.SEND)
                            && (uiMessage.getSentStatus() == io.rong.imlib.model.Message.SentStatus.SENT)
                            && ntfTime >= uiMessage.getSentTime()) {
                        uiMessage.setSentStatus(io.rong.imlib.model.Message.SentStatus.READ);
                        int first = mList.getFirstVisiblePosition();
                        int last = mList.getLastVisiblePosition();
                        int position = getPositionInListView(i);
                        if (position >= first && position <= last) {
                            mListAdapter.getView(i, getListViewChildAt(i), mList);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取会话界面消息展示适配器。
     *
     * @return 消息适配器
     */
    public MessageListAdapter getMessageAdapter() {
        return mListAdapter;
    }

    /**
     * 接收到消息，先调用此方法，检查是否可以更新该消息。
     * 如果可以更新，则返回 true，否则返回 false。
     * 注意：开发者可以重写此方法，来控制是否更新。
     *
     * @param message 接收到的消息体。
     * @param left    剩余的消息数量。
     * @return 根据返回值确定是否更新对应会话信息。
     */
    public boolean shouldUpdateMessage(io.rong.imlib.model.Message message, int left) {
        return true;
    }

    /**
     * 加载本地历史消息。
     * 开发者可以通过重新此方法，加入自己需要在界面上要展示的消息数据。
     * 重写此方法后，如果需要同时显示 sdk 中的消息，必须执行 super.getHistoryMessage().
     * <p/>
     * 注意：通过 callback 返回的数据要保证在 UI 线程返回
     *
     * @param conversationType 会话类型
     * @param targetId         会话 id
     * @param lastMessageId    最后一条消息 id
     * @param reqCount         加载数量
     * @param callback         数据加载后，通过回调返回数据
     */
    public void getHistoryMessage(final Conversation.ConversationType conversationType, final String targetId, int lastMessageId, final int reqCount, final IHistoryDataResultCallback<List<io.rong.imlib.model.Message>> callback) {
        RongIMClient.getInstance().getHistoryMessages(conversationType, targetId, lastMessageId, reqCount, new RongIMClient.ResultCallback<List<io.rong.imlib.model.Message>>() {
            @Override
            public void onSuccess(List<io.rong.imlib.model.Message> messages) {
                if (callback != null) {
                    callback.onResult(messages);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                RLog.e(TAG, "getHistoryMessages " + e);
                if (callback != null) {
                    callback.onResult(null);
                }
            }
        });
    }

    private void getHistoryMessage(final Conversation.ConversationType conversationType, final String targetId, final int reqCount, final int scrollMode) {
        mList.onRefreshStart(AutoRefreshListView.Mode.START);
        if (conversationType.equals(Conversation.ConversationType.CHATROOM)) {
            mList.onRefreshComplete(0, 0, false);
            RLog.w(TAG, "Should not get local message in chatroom");
            return;
        }
        mList.onRefreshStart(AutoRefreshListView.Mode.START);
        int last = mListAdapter.getCount() == 0 ? -1 : mListAdapter.getItem(0).getMessageId();
        getHistoryMessage(conversationType, targetId, last, reqCount, new IHistoryDataResultCallback<List<io.rong.imlib.model.Message>>() {
            @Override
            public void onResult(List<io.rong.imlib.model.Message> messages) {
                RLog.i(TAG, "getHistoryMessage " + (messages != null ? messages.size() : 0));
                mHasMoreLocalMessages = (messages != null ? messages.size() : 0) == reqCount;
                mList.onRefreshComplete(reqCount, reqCount, false);
                if (messages != null && messages.size() > 0) {
                    for (io.rong.imlib.model.Message message : messages) {
                        boolean contains = false;
                        for (int i = 0; i < mListAdapter.getCount(); i++) {
                            contains = mListAdapter.getItem(i).getMessageId() == message.getMessageId();
                            if (contains) break;
                        }
                        if (!contains) {
                            UIMessage uiMessage = UIMessage.obtain(message);
                            if (message.getContent() instanceof CSPullLeaveMessage) {
                                uiMessage.setCsConfig(mCustomServiceConfig);
                            }
                            mListAdapter.add(uiMessage, 0);
                        }
                    }
                    if (scrollMode == SCROLL_MODE_BOTTOM) {
                        mList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                    } else {
                        mList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
                    }
                    mListAdapter.notifyDataSetChanged();
                    if (mLastMentionMsgId > 0) {
                        int index = mListAdapter.findPosition(mLastMentionMsgId);
                        mList.smoothScrollToPosition(index);
                        mLastMentionMsgId = 0;
                    } else {
                        if (SCROLL_MODE_TOP == scrollMode) {
                            mList.setSelection(0);
                        } else if (scrollMode == SCROLL_MODE_BOTTOM) {
                            mList.setSelection(mList.getCount());
                        } else {
                            mList.setSelection(messages.size() + 1);
                        }
                    }
                    sendReadReceiptResponseIfNeeded(messages);
                } else {
                    mList.onRefreshComplete(reqCount, reqCount, false);
                }
            }

            @Override
            public void onError() {
                mList.onRefreshComplete(reqCount, reqCount, false);
            }
        });
    }

    /**
     * 加载服务器远端历史消息。
     * 此功能需要开通 “历史消息云存储” 服务后，才可以使用。
     * 开发者可以通过重新此方法，加入自己需要在界面上要展示的消息数据。
     * 重写此方法后，如果不显示 sdk 中的消息，可以通过不执行 super.getRemoteHistoryMessages()。
     * <p/>
     * 注意：通过 callback 返回的数据要保证在 UI 线程返回。
     *
     * @param conversationType 会话类型
     * @param targetId         会话 id
     * @param dateTime         从该时间点开始获取消息。即：消息中的 sentTime；第一次可传 0，获取最新 count 条。
     * @param reqCount         加载数量
     * @param callback         数据加载后，通过回调返回数据
     */
    public void getRemoteHistoryMessages(final Conversation.ConversationType conversationType, final String targetId, final long dateTime, final int reqCount, final IHistoryDataResultCallback<List<io.rong.imlib.model.Message>> callback) {
        RongIMClient.getInstance().getRemoteHistoryMessages(conversationType, targetId, dateTime, reqCount, new RongIMClient.ResultCallback<List<io.rong.imlib.model.Message>>() {
            @Override
            public void onSuccess(List<io.rong.imlib.model.Message> messages) {
                if (callback != null) {
                    callback.onResult(messages);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                RLog.e(TAG, "getRemoteHistoryMessages " + e);
                if (callback != null) {
                    callback.onResult(null);
                }
            }
        });
    }

    private void getRemoteHistoryMessages(final Conversation.ConversationType conversationType, final String targetId, final int reqCount) {
        mList.onRefreshStart(AutoRefreshListView.Mode.START);
        if (mConversationType.equals(Conversation.ConversationType.CHATROOM)) {
            mList.onRefreshComplete(0, 0, false);
            RLog.w(TAG, "Should not get remote message in chatroom");
            return;
        }
        long dateTime = mListAdapter.getCount() == 0 ? 0 : mListAdapter.getItem(0).getSentTime();
        getRemoteHistoryMessages(conversationType, targetId, dateTime, reqCount, new IHistoryDataResultCallback<List<io.rong.imlib.model.Message>>() {
            @Override
            public void onResult(List<io.rong.imlib.model.Message> messages) {
                RLog.i(TAG, "getRemoteHistoryMessages " + (messages == null ? 0 : messages.size()));
                mList.onRefreshComplete(messages == null ? 0 : messages.size(), reqCount, false);
                if (messages != null && messages.size() > 0) {
                    List<UIMessage> remoteList = new ArrayList<>();
                    for (io.rong.imlib.model.Message message : messages) {
                        if (message.getMessageId() > 0) {
                            UIMessage uiMessage = UIMessage.obtain(message);
                            if (message.getContent() instanceof CSPullLeaveMessage) {
                                uiMessage.setCsConfig(mCustomServiceConfig);
                            }
                            remoteList.add(uiMessage);
                        }
                    }
                    remoteList = filterMessage(remoteList);
                    if (remoteList != null && remoteList.size() > 0) {
                        for (UIMessage uiMessage : remoteList) {
                            mListAdapter.add(uiMessage, 0);
                        }
                        mList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
                        mListAdapter.notifyDataSetChanged();
                        mList.setSelection(messages.size() + 1);
                        sendReadReceiptResponseIfNeeded(messages);
                    }
                } else {
                    mList.onRefreshComplete(0, reqCount, false);
                }
            }

            @Override
            public void onError() {
                mList.onRefreshComplete(0, reqCount, false);
            }
        });
    }

    private List<UIMessage> filterMessage(List<UIMessage> srcList) {
        List<UIMessage> destList;
        if (mListAdapter.getCount() > 0) {
            destList = new ArrayList<>();
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                for (UIMessage msg : srcList) {
                    if (destList.contains(msg)) continue;
                    if (msg.getMessageId() != mListAdapter.getItem(i).getMessageId()) {
                        destList.add(msg);
                    }
                }
            }
        } else {
            destList = srcList;
        }
        return destList;
    }

    private void getLastMentionedMessageId(Conversation.ConversationType conversationType, String targetId) {
        RongIMClient.getInstance().getUnreadMentionedMessages(conversationType, targetId, new RongIMClient.ResultCallback<List<io.rong.imlib.model.Message>>() {
            @Override
            public void onSuccess(List<io.rong.imlib.model.Message> messages) {
                if (messages != null && messages.size() > 0) {
                    mLastMentionMsgId = messages.get(0).getMessageId();
                    int index = mListAdapter.findPosition(mLastMentionMsgId);
                    RLog.i(TAG, "getLastMentionedMessageId " + mLastMentionMsgId + " " + index);
                    if (mLastMentionMsgId > 0 && index >= 0) {
                        mList.smoothScrollToPosition(index);
                        mLastMentionMsgId = 0;
                    }
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {

            }
        });
    }

    private void sendReadReceiptResponseIfNeeded(List<io.rong.imlib.model.Message> messages) {
        if (mReadRec &&
                (mConversationType.equals(Conversation.ConversationType.GROUP) ||
                        mConversationType.equals(Conversation.ConversationType.DISCUSSION)) &&
                RongContext.getInstance().isReadReceiptConversationType(mConversationType)) {
            List<io.rong.imlib.model.Message> responseMessageList = new ArrayList<>();
            for (io.rong.imlib.model.Message message : messages) {
                ReadReceiptInfo readReceiptInfo = message.getReadReceiptInfo();
                if (readReceiptInfo == null) {
                    continue;
                }
                if (readReceiptInfo.isReadReceiptMessage() && !readReceiptInfo.hasRespond()) {
                    responseMessageList.add(message);
                }
            }
            if (responseMessageList.size() > 0) {
                RongIMClient.getInstance().sendReadReceiptResponse(mConversationType, mTargetId, responseMessageList, null);
            }
        }
    }

    @Override
    public void onExtensionCollapsed() {

    }

    @Override
    public void onExtensionExpanded(int h) {
        mList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mList.smoothScrollToPosition(mList.getCount());
    }

    /**
     * 开启客服，进入客服会话界面时，会回调此方法
     * 开发者可以重写此方法，修改启动客服的行为
     *
     * @param targetId 客服 id
     */
    public void onStartCustomService(String targetId) {
        csEnterTime = System.currentTimeMillis();
        mRongExtension.setExtensionBarMode(CustomServiceMode.CUSTOM_SERVICE_MODE_NO_SERVICE);
        RongIMClient.getInstance().startCustomService(targetId, customServiceListener, mCustomUserInfo);
    }

    /**
     * 会话结束时，回调此方法。
     * 开发者可以重写此方法，修改结束客服的行为
     *
     * @param targetId 客服 id
     */
    public void onStopCustomService(String targetId) {
        RongIMClient.getInstance().stopCustomService(targetId);
    }

    @Override
    final public void onEvaluateSubmit() {
        if (mEvaluateDialg != null) {
            mEvaluateDialg.destroy();
            mEvaluateDialg = null;
        }
        if (mCustomServiceConfig.quitSuspendType.equals(CustomServiceConfig.CSQuitSuspendType.NONE)) {
            getActivity().finish();
        }
    }

    @Override
    final public void onEvaluateCanceled() {
        if (mEvaluateDialg != null) {
            mEvaluateDialg.destroy();
            mEvaluateDialg = null;
        }
        if (mCustomServiceConfig.quitSuspendType.equals(CustomServiceConfig.CSQuitSuspendType.NONE)) {
            getActivity().finish();
        }

    }

    private void startTimer(int event, int interval) {
        getHandler().removeMessages(event);
        getHandler().sendEmptyMessageDelayed(event, interval);
    }

    private void stopTimer(int event) {
        getHandler().removeMessages(event);
    }

    public Conversation.ConversationType getConversationType() {
        return mConversationType;
    }

    public String getTargetId() {
        return mTargetId;
    }
}
