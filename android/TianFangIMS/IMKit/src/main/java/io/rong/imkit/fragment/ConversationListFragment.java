package io.rong.imkit.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imkit.R;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.manager.InternalModuleManager;
import io.rong.imkit.model.Event;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ReadReceiptMessage;
import io.rong.push.RongPushClient;

public class ConversationListFragment extends UriFragment implements
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener,
        ConversationListAdapter.OnPortraitItemClick {

    private String TAG = "ConversationListFragment";

    private List<ConversationConfig> mConversationsConfig;
    private ConversationListFragment mThis;

    private ConversationListAdapter mAdapter;
    private ListView mList;
    private LinearLayout mNotificationBar;
    private ImageView mNotificationBarImage;
    private TextView mNotificationBarText;
    private boolean isShowWithoutConnected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThis = this;
        TAG = this.getClass().getSimpleName();
        mConversationsConfig = new ArrayList<>();
        EventBus.getDefault().register(this);
        InternalModuleManager.getInstance().onLoaded();
    }

    @Override
    protected void initFragment(Uri uri) {
        RLog.d(TAG, "initFragment " + uri);

        Conversation.ConversationType[] defConversationType = {
                Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP,
                Conversation.ConversationType.DISCUSSION,
                Conversation.ConversationType.SYSTEM,
                Conversation.ConversationType.CUSTOMER_SERVICE,
                Conversation.ConversationType.CHATROOM,
                Conversation.ConversationType.PUBLIC_SERVICE,
                Conversation.ConversationType.APP_PUBLIC_SERVICE
        };

        //ConversationListFragment config
        for (Conversation.ConversationType conversationType : defConversationType) {
            if (uri.getQueryParameter(conversationType.getName()) != null) {
                ConversationConfig config = new ConversationConfig();
                config.conversationType = conversationType;
                config.isGathered = uri.getQueryParameter(conversationType.getName()).equals("true");
                mConversationsConfig.add(config);
            }
        }

        //SubConversationListFragment config
        if (mConversationsConfig.size() == 0) {
            String type = uri.getQueryParameter("type");
            for (Conversation.ConversationType conversationType : defConversationType) {
                if (conversationType.getName().equals(type)) {
                    ConversationConfig config = new ConversationConfig();
                    config.conversationType = conversationType;
                    config.isGathered = false;
                    mConversationsConfig.add(config);
                    break;
                }
            }
        }

        mAdapter.clear();

        if (RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
            RLog.d(TAG, "RongCloud haven't been connected yet, so the conversation list display blank !!!");
            isShowWithoutConnected = true;
            return;
        }

        getConversationList(getConfigConversationTypes());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rc_fr_conversationlist, container, false);
        mNotificationBar = findViewById(view, R.id.rc_status_bar);
        mNotificationBar.setVisibility(View.GONE);
        mNotificationBarImage = findViewById(view, R.id.rc_status_bar_image);
        mNotificationBarText = findViewById(view, R.id.rc_status_bar_text);
        View emptyView = findViewById(view, R.id.rc_conversation_list_empty_layout);
        TextView emptyText = findViewById(view, R.id.rc_empty_tv);
        emptyText.setText(getActivity().getResources().getString(R.string.rc_conversation_list_empty_prompt));

        mList = findViewById(view, R.id.rc_list);
        mList.setEmptyView(emptyView);
        mList.setOnItemClickListener(this);
        mList.setOnItemLongClickListener(this);

        if (mAdapter == null) {
            mAdapter = onResolveAdapter(getActivity());
        }
        mAdapter.setOnPortraitItemClick(this);
        mList.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        RLog.d(TAG, "onResume " + RongIM.getInstance().getCurrentConnectionStatus());
        RongPushClient.clearAllPushNotifications(getActivity());
        setNotificationBarVisibility(RongIM.getInstance().getCurrentConnectionStatus());
    }

    private void getConversationList(Conversation.ConversationType[] conversationTypes) {
        getConversationList(conversationTypes, new IHistoryDataResultCallback<List<Conversation>>() {
            @Override
            public void onResult(List<Conversation> data) {
                if (data != null && data.size() > 0) {
                    makeUiConversationList(data);
                    mAdapter.notifyDataSetChanged();
                } else {
                    RLog.w(TAG, "getConversationList return null " + RongIMClient.getInstance().getCurrentConnectionStatus());
                    isShowWithoutConnected = true;
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    /**
     * 开发者可以重写此方法，来填充自定义数据到会话列表界面。
     * 如果需要同时显示 sdk 中默认会话列表数据，在重写时可使用 super.getConversationList()。反之，不需要调用 super 方法。
     * <p>
     * 注意：通过 callback 返回的数据要保证在 UI 线程返回。
     *
     * @param conversationTypes 当前会话列表已配置显示的会话类型。
     */
    public void getConversationList(ConversationType[] conversationTypes, final IHistoryDataResultCallback<List<Conversation>> callback) {
        RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                if (callback != null) {
                    callback.onResult(conversations);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                if (callback != null) {
                    callback.onError();
                }
            }
        }, conversationTypes);
    }

    /**
     * 定位会话列表中的某一条未读会话。
     * 如果有多条未读会话,每调用一次此接口,就会从上往下逐个未读会话定位。
     */
    public void focusUnreadItem() {
        int first = mList.getFirstVisiblePosition();
        int last = mList.getLastVisiblePosition();
        int visibleCount = last - first + 1;
        int count = mList.getCount();
        if (visibleCount < count) {
            int index;
            if (last < count - 1) {
                index = first + 1;
            } else {
                index = 0;
            }

            if (!selectNextUnReadItem(index, count)) {
                selectNextUnReadItem(0, count);
            }
        }
    }

    private boolean selectNextUnReadItem(int startIndex, int totalCount) {
        int index = -1;
        for (int i = startIndex; i < totalCount; i++) {
            UIConversation uiConversation = mAdapter.getItem(i);
            if (uiConversation.getUnReadMessageCount() > 0) {
                index = i;
                break;
            }
        }
        if (index >= 0 && index < totalCount) {
            mList.setSelection(index);
            return true;
        }
        return false;
    }

    private void setNotificationBarVisibility(RongIMClient.ConnectionStatusListener.ConnectionStatus status) {
        if (!getResources().getBoolean(R.bool.rc_is_show_warning_notification)) {
            RLog.e(TAG, "rc_is_show_warning_notification is disabled.");
            return;
        }

        String content = null;
        if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE)) {
            content = getResources().getString(R.string.rc_notice_network_unavailable);
        } else if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT)) {
            content = getResources().getString(R.string.rc_notice_tick);
        } else if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
            mNotificationBar.setVisibility(View.GONE);
        } else if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
            content = getResources().getString(R.string.rc_notice_disconnect);
        } else if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING)) {
            content = getResources().getString(R.string.rc_notice_connecting);
        }
        if (content != null) {
            if (mNotificationBar.getVisibility() == View.GONE) {
                final String text = content;
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
                            mNotificationBar.setVisibility(View.VISIBLE);
                            mNotificationBarText.setText(text);
                            if (RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING)) {
                                mNotificationBarImage.setImageResource(R.drawable.rc_notification_connecting_animated);
                            } else {
                                mNotificationBarImage.setImageResource(R.drawable.rc_notification_network_available);
                            }
                        }
                    }
                }, 4000);
            } else {
                mNotificationBarText.setText(content);
                if (RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING)) {
                    mNotificationBarImage.setImageResource(R.drawable.rc_notification_connecting_animated);
                } else {
                    mNotificationBarImage.setImageResource(R.drawable.rc_notification_network_available);
                }
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    /**
     * 设置 ListView 的 Adapter 适配器。
     *
     * @param adapter 适配器
     * @deprecated 此方法已经废弃，可以使用 {@link #onResolveAdapter(Context)} 代替
     */
    @Deprecated
    public void setAdapter(ConversationListAdapter adapter) {
        mAdapter = adapter;
        if (mList != null) {
            mList.setAdapter(adapter);
        }
    }

    /**
     * 提供 ListView 的 Adapter 适配器。
     * 使用时，需要继承 {@link ConversationListFragment} 并重写此方法。
     * 注意：提供的适配器，要继承自 {@link ConversationListAdapter}
     *
     * @return 适配器
     */
    public ConversationListAdapter onResolveAdapter(Context context) {
        mAdapter = new ConversationListAdapter(context);
        return mAdapter;
    }

    public void onEventMainThread(Event.SyncReadStatusEvent event) {
        ConversationType conversationType = event.getConversationType();
        String targetId = event.getTargetId();
        RLog.d(TAG, "SyncReadStatusEvent " + conversationType + " " + targetId);

        final int first = mList.getFirstVisiblePosition();
        final int last = mList.getLastVisiblePosition();
        int position;
        if (getGatherState(conversationType)) {
            position = mAdapter.findGatheredItem(conversationType);
        } else {
            position = mAdapter.findPosition(conversationType, targetId);
        }
        if (position >= 0) {
            UIConversation uiConversation = mAdapter.getItem(position);
            uiConversation.clearUnRead(conversationType, targetId);
            if (position >= first && position <= last) {
                mAdapter.getView(position, mList.getChildAt(position - mList.getFirstVisiblePosition()), mList);
            }
        }
    }

    /**
     * 仅处理非聚合状态，显示已读回执标志。
     *
     * @param event 已读回执事件
     */
    public void onEventMainThread(final Event.ReadReceiptEvent event) {
        ConversationType conversationType = event.getMessage().getConversationType();
        String targetId = event.getMessage().getTargetId();
        int originalIndex = mAdapter.findPosition(conversationType, targetId);
        boolean gatherState = getGatherState(conversationType);
        if (!gatherState && originalIndex >= 0) {
            UIConversation conversation = mAdapter.getItem(originalIndex);
            ReadReceiptMessage content = (ReadReceiptMessage) event.getMessage().getContent();
            if (content.getLastMessageSendTime() >= conversation.getUIConversationTime()
                    && conversation.getConversationSenderId().equals(RongIMClient.getInstance().getCurrentUserId())) {
                conversation.setSentStatus(Message.SentStatus.READ);
                mAdapter.getView(originalIndex, mList.getChildAt(originalIndex - mList.getFirstVisiblePosition()), mList);
            }
        }
    }

    public void onEventMainThread(Event.AudioListenedEvent event) {
        Message message = event.getMessage();
        ConversationType conversationType = message.getConversationType();
        String targetId = message.getTargetId();
        RLog.d(TAG, "Message: " + message.getObjectName() + " " + conversationType + " " + message.getSentStatus());

        if (isConfigured(conversationType)) {
            boolean gathered = getGatherState(conversationType);
            int position = gathered ? mAdapter.findGatheredItem(conversationType) : mAdapter.findPosition(conversationType, targetId);
            if (position >= 0) {
                UIConversation uiConversation = mAdapter.getItem(position);
                if (message.getMessageId() == uiConversation.getLatestMessageId()) {
                    uiConversation.updateConversation(message, gathered);
                    mAdapter.getView(position, mList.getChildAt(position - mList.getFirstVisiblePosition()), mList);
                }
            }
        }
    }

    /**
     * 接收到消息，先调用此方法，检查是否可以更新消息对应的会话。
     * 如果可以更新，则返回 true，否则返回 false。
     * 注意：开发者可以重写此方法，来控制是否更新对应的会话。
     *
     * @param message 接收到的消息体。
     * @param left    剩余的消息数量。
     * @return 根据返回值确定是否更新对应会话信息。
     */
    public boolean shouldUpdateConversation(Message message, int left) {
        return true;
    }

    public void onEventMainThread(final Event.OnReceiveMessageEvent event) {
        Message message = event.getMessage();
        String targetId = message.getTargetId();
        ConversationType conversationType = message.getConversationType();
        int first = mList.getFirstVisiblePosition();
        int last = mList.getLastVisiblePosition();
        if (isConfigured(message.getConversationType()) && shouldUpdateConversation(event.getMessage(), event.getLeft())) {
            if (message.getMessageId() > 0) {
                int position;
                boolean gathered = getGatherState(conversationType);
                if (gathered) {
                    position = mAdapter.findGatheredItem(conversationType);
                } else {
                    position = mAdapter.findPosition(conversationType, targetId);
                }
                UIConversation uiConversation;
                if (position < 0) {
                    uiConversation = UIConversation.obtain(message, gathered);
                    int index = getPosition(uiConversation);
                    mAdapter.add(uiConversation, index);
                    mAdapter.notifyDataSetChanged();
                } else {
                    uiConversation = mAdapter.getItem(position);
                    if (event.getMessage().getSentTime() > uiConversation.getUIConversationTime()) {
                        uiConversation.updateConversation(message, gathered);

                        mAdapter.remove(position);
                        int index = getPosition(uiConversation);
                        if (index == position) {
                            mAdapter.add(uiConversation, index);
                            if (index >= first && index <= last)
                                mAdapter.getView(index, mList.getChildAt(index - mList.getFirstVisiblePosition()), mList);
                        } else {
                            mAdapter.add(uiConversation, index);
                            if (index >= first && index <= last) mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        RLog.i(TAG, "ignore update message " + event.getMessage().getObjectName());
                    }
                }
                RLog.i(TAG, "conversation unread count : " + uiConversation.getUnReadMessageCount() + " " + conversationType + " " + targetId);
            }
            if (event.getLeft() == 0) {
                syncUnreadCount();
            }
            RLog.d(TAG, "OnReceiveMessageEvent: " + message.getObjectName() + " " + event.getLeft() + " " + conversationType + " " + targetId);
        }
    }

    public void onEventMainThread(Event.MessageLeftEvent event) {
        if (event.left == 0) {
            syncUnreadCount();
        }
    }

    private void syncUnreadCount() {
        if (mAdapter.getCount() > 0) {
            final int first = mList.getFirstVisiblePosition();
            final int last = mList.getLastVisiblePosition();
            for (int i = 0; i < mAdapter.getCount(); i++) {
                final UIConversation uiConversation = mAdapter.getItem(i);
                ConversationType conversationType = uiConversation.getConversationType();
                String targetId = uiConversation.getConversationTargetId();
                if (getGatherState(conversationType)) {
                    final int position = mAdapter.findGatheredItem(conversationType);
                    RongIMClient.getInstance().getUnreadCount(new RongIMClient.ResultCallback<Integer>() {
                        @Override
                        public void onSuccess(Integer integer) {
                            uiConversation.setUnReadMessageCount(integer);
                            if (position >= first && position <= last) {
                                mAdapter.getView(position, mList.getChildAt(position - mList.getFirstVisiblePosition()), mList);
                            }
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {

                        }
                    }, conversationType);
                } else {
                    final int position = mAdapter.findPosition(conversationType, targetId);
                    RongIMClient.getInstance().getUnreadCount(conversationType, targetId, new RongIMClient.ResultCallback<Integer>() {
                        @Override
                        public void onSuccess(Integer integer) {
                            uiConversation.setUnReadMessageCount(integer);
                            if (position >= first && position <= last) {
                                mAdapter.getView(position, mList.getChildAt(position - mList.getFirstVisiblePosition()), mList);
                            }
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {

                        }
                    });
                }
            }
        }
    }


    public void onEventMainThread(Event.MessageRecallEvent event) {
        RLog.d(TAG, "MessageRecallEvent");

        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            UIConversation uiConversation = mAdapter.getItem(i);
            if (event.getMessageId() == uiConversation.getLatestMessageId()) {
                final boolean gatherState = mAdapter.getItem(i).getConversationGatherState();
                if (gatherState) {
                    RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                        @Override
                        public void onSuccess(List<Conversation> conversationList) {
                            if (conversationList != null && conversationList.size() > 0) {
                                UIConversation uiConversation = makeUIConversation(conversationList);
                                int oldPos = mAdapter.findPosition(uiConversation.getConversationType(), uiConversation.getConversationTargetId());
                                if (oldPos >= 0) {
                                    mAdapter.remove(oldPos);
                                }
                                int newIndex = getPosition(uiConversation);
                                mAdapter.add(uiConversation, newIndex);
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {

                        }
                    }, uiConversation.getConversationType());

                } else {
                    RongIM.getInstance().getConversation(uiConversation.getConversationType(),
                            uiConversation.getConversationTargetId(),
                            new RongIMClient.ResultCallback<Conversation>() {
                                @Override
                                public void onSuccess(Conversation conversation) {
                                    if (conversation != null) {
                                        UIConversation temp = UIConversation.obtain(conversation, false);
                                        int pos = mAdapter.findPosition(conversation.getConversationType(), conversation.getTargetId());
                                        if (pos >= 0) {
                                            mAdapter.remove(pos);
                                        }
                                        int newPosition = getPosition(temp);
                                        mAdapter.add(temp, newPosition);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode e) {
                                }
                            });
                }
                break;
            }
        }
    }

    public void onEventMainThread(Event.RemoteMessageRecallEvent event) {
        RLog.d(TAG, "RemoteMessageRecallEvent");

        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            UIConversation uiConversation = mAdapter.getItem(i);
            if (event.getMessageId() == uiConversation.getLatestMessageId()) {
                final boolean gatherState = uiConversation.getConversationGatherState();
                if (gatherState) {
                    RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                        @Override
                        public void onSuccess(List<Conversation> conversationList) {
                            if (conversationList != null && conversationList.size() > 0) {
                                UIConversation uiConversation = makeUIConversation(conversationList);
                                int oldPos = mAdapter.findPosition(uiConversation.getConversationType(), uiConversation.getConversationTargetId());
                                if (oldPos >= 0) {
                                    mAdapter.remove(oldPos);
                                }
                                int newIndex = getPosition(uiConversation);
                                mAdapter.add(uiConversation, newIndex);
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {

                        }
                    }, mAdapter.getItem(i).getConversationType());
                } else {
                    RongIM.getInstance().getConversation(uiConversation.getConversationType(),
                            uiConversation.getConversationTargetId(),
                            new RongIMClient.ResultCallback<Conversation>() {
                                @Override
                                public void onSuccess(Conversation conversation) {
                                    if (conversation != null) {
                                        UIConversation temp = UIConversation.obtain(conversation, false);

                                        int pos = mAdapter.findPosition(conversation.getConversationType(), conversation.getTargetId());
                                        if (pos >= 0) {
                                            mAdapter.remove(pos);
                                        }
                                        int newPosition = getPosition(temp);
                                        mAdapter.add(temp, newPosition);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode e) {
                                }
                            });
                }
                break;
            }
        }
    }

    public void onEventMainThread(Message message) {
        ConversationType conversationType = message.getConversationType();
        String targetId = message.getTargetId();
        RLog.d(TAG, "Message: " + message.getObjectName() + " " + message.getMessageId() + " " + conversationType + " " + message.getSentStatus());

        boolean gathered = getGatherState(conversationType);
        if (isConfigured(conversationType) && message.getMessageId() > 0) {
            int position = gathered ? mAdapter.findGatheredItem(conversationType) : mAdapter.findPosition(conversationType, targetId);
            UIConversation uiConversation;
            if (position < 0) {
                uiConversation = UIConversation.obtain(message, gathered);
                int index = getPosition(uiConversation);
                mAdapter.add(uiConversation, index);
                mAdapter.notifyDataSetChanged();
            } else {
                uiConversation = mAdapter.getItem(position);
                mAdapter.remove(position);
                uiConversation.updateConversation(message, gathered);
                int index = getPosition(uiConversation);
                mAdapter.add(uiConversation, index);
                if (position == index) {
                    mAdapter.getView(index, mList.getChildAt(index - mList.getFirstVisiblePosition()), mList);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public void onEventMainThread(final RongIMClient.ConnectionStatusListener.ConnectionStatus status) {
        RLog.d(TAG, "ConnectionStatus, " + status.toString());

        setNotificationBarVisibility(status);
    }

    public void onEventMainThread(Event.ConnectEvent event) {
        if (isShowWithoutConnected) {
            getConversationList(getConfigConversationTypes());
            isShowWithoutConnected = false;
        }
    }

    public void onEventMainThread(final Event.CreateDiscussionEvent createDiscussionEvent) {
        RLog.d(TAG, "createDiscussionEvent");
        final String targetId = createDiscussionEvent.getDiscussionId();
        if (isConfigured(ConversationType.DISCUSSION)) {
            RongIMClient.getInstance().getConversation(ConversationType.DISCUSSION, targetId, new RongIMClient.ResultCallback<Conversation>() {
                @Override
                public void onSuccess(Conversation conversation) {
                    if (conversation != null) {
                        UIConversation uiConversation;
                        int position;
                        if (getGatherState(ConversationType.DISCUSSION)) {
                            position = mAdapter.findGatheredItem(ConversationType.DISCUSSION);
                        } else {
                            position = mAdapter.findPosition(ConversationType.DISCUSSION, targetId);
                        }
                        conversation.setConversationTitle(createDiscussionEvent.getDiscussionName());
                        if (position < 0) {
                            uiConversation = UIConversation.obtain(conversation, getGatherState(ConversationType.DISCUSSION));
                            int index = getPosition(uiConversation);
                            mAdapter.add(uiConversation, index);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            uiConversation = mAdapter.getItem(position);
                            uiConversation.updateConversation(conversation, getGatherState(ConversationType.DISCUSSION));
                            mAdapter.getView(position, mList.getChildAt(position - mList.getFirstVisiblePosition()), mList);
                        }
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {

                }
            });
        }
    }

    public void onEventMainThread(final Event.DraftEvent draft) {
        final ConversationType conversationType = draft.getConversationType();
        final String targetId = draft.getTargetId();
        RLog.i(TAG, "Draft : " + conversationType);

        if (isConfigured(conversationType)) {
            final boolean gathered = getGatherState(conversationType);
            final int position = gathered ? mAdapter.findGatheredItem(conversationType) : mAdapter.findPosition(conversationType, targetId);
            RongIMClient.getInstance().getConversation(conversationType, targetId, new RongIMClient.ResultCallback<Conversation>() {
                @Override
                public void onSuccess(Conversation conversation) {
                    if (conversation != null) {
                        UIConversation uiConversation;
                        if (position < 0) {
                            if (!TextUtils.isEmpty(draft.getContent())) {
                                uiConversation = UIConversation.obtain(conversation, gathered);
                                int index = getPosition(uiConversation);
                                mAdapter.add(uiConversation, index);
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            uiConversation = mAdapter.getItem(position);
                            if ((TextUtils.isEmpty(draft.getContent()) && !TextUtils.isEmpty(uiConversation.getDraft()))
                                    || (!TextUtils.isEmpty(draft.getContent()) && TextUtils.isEmpty(uiConversation.getDraft()))
                                    || (!TextUtils.isEmpty(draft.getContent()) && !TextUtils.isEmpty(uiConversation.getDraft())
                                    && !draft.getContent().equals(uiConversation.getDraft()))) {
                                uiConversation.updateConversation(conversation, gathered);
                                mAdapter.getView(position, mList.getChildAt(position - mList.getFirstVisiblePosition()), mList);
                            }
                        }
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {

                }
            });
        }
    }

    public void onEventMainThread(Group groupInfo) {
        RLog.d(TAG, "Group: " + groupInfo.getName() + " " + groupInfo.getId());

        int count = mAdapter.getCount();
        if (groupInfo.getName() == null) {
            return;
        }
        int last = mList.getLastVisiblePosition();
        int first = mList.getFirstVisiblePosition();
        for (int i = 0; i < count; i++) {
            UIConversation uiConversation = mAdapter.getItem(i);
            uiConversation.updateConversation(groupInfo);
            if (i >= first && i <= last) {
                mAdapter.getView(i, mList.getChildAt(i - first), mList);
            }
        }
    }

    public void onEventMainThread(Discussion discussion) {
        RLog.d(TAG, "Discussion: " + discussion.getName() + " " + discussion.getId());

        if (isConfigured(ConversationType.DISCUSSION)) {
            int last = mList.getLastVisiblePosition();
            int first = mList.getFirstVisiblePosition();
            int position;
            if (getGatherState(ConversationType.DISCUSSION)) {
                position = mAdapter.findGatheredItem(ConversationType.DISCUSSION);
            } else {
                position = mAdapter.findPosition(ConversationType.DISCUSSION, discussion.getId());
            }
            if (position >= 0) {
                for (int i = 0; i == position; i++) {
                    UIConversation uiConversation = mAdapter.getItem(i);
                    uiConversation.updateConversation(discussion);
                    if (i >= first && i <= last) {
                        mAdapter.getView(i, mList.getChildAt(i - mList.getFirstVisiblePosition()), mList);
                    }
                }
            }
        }
    }

    public void onEventMainThread(GroupUserInfo groupUserInfo) {
        RLog.d(TAG, "GroupUserInfo " + groupUserInfo.getGroupId() + " " + groupUserInfo.getUserId() + " " + groupUserInfo.getNickname());
        if (groupUserInfo.getNickname() == null || groupUserInfo.getGroupId() == null) {
            return;
        }
        int count = mAdapter.getCount();
        int last = mList.getLastVisiblePosition();
        int first = mList.getFirstVisiblePosition();
        for (int i = 0; i < count; i++) {
            UIConversation uiConversation = mAdapter.getItem(i);
            if (!getGatherState(ConversationType.GROUP)
                    && uiConversation.getConversationTargetId().equals(groupUserInfo.getGroupId())
                    && uiConversation.getConversationSenderId().equals(groupUserInfo.getUserId())) {
                uiConversation.updateConversation(groupUserInfo);
                if (i >= first && i <= last) {
                    mAdapter.getView(i, mList.getChildAt(i - mList.getFirstVisiblePosition()), mList);
                }
            }
        }
    }

    public void onEventMainThread(UserInfo userInfo) {
        RLog.i(TAG, "UserInfo " + userInfo.getUserId() + " " + userInfo.getName());

        int count = mAdapter.getCount();
        int last = mList.getLastVisiblePosition();
        int first = mList.getFirstVisiblePosition();
        for (int i = 0; i < count && userInfo.getName() != null; i++) {
            UIConversation uiConversation = mAdapter.getItem(i);
            if (uiConversation.hasNickname(userInfo.getUserId())) {
                RLog.i(TAG, "has nick name");
                continue;
            }
            uiConversation.updateConversation(userInfo);
            if (i >= first && i <= last) {
                mAdapter.getView(i, mList.getChildAt(i - first), mList);
            }
        }
    }

    public void onEventMainThread(PublicServiceProfile profile) {
        RLog.d(TAG, "PublicServiceProfile");
        int count = mAdapter.getCount();
        boolean gatherState = getGatherState(profile.getConversationType());
        for (int i = 0; i < count; i++) {
            UIConversation uiConversation = mAdapter.getItem(i);
            if (uiConversation.getConversationType().equals(profile.getConversationType())
                    && uiConversation.getConversationTargetId().equals(profile.getTargetId())
                    && !gatherState) {
                uiConversation.setUIConversationTitle(profile.getName());
                uiConversation.setIconUrl(profile.getPortraitUri());
                mAdapter.getView(i, mList.getChildAt(i - mList.getFirstVisiblePosition()), mList);
                break;
            }
        }

    }

    public void onEventMainThread(Event.PublicServiceFollowableEvent event) {
        RLog.d(TAG, "PublicServiceFollowableEvent");
        if (!event.isFollow()) {
            int originalIndex = mAdapter.findPosition(event.getConversationType(), event.getTargetId());
            if (originalIndex >= 0) {
                mAdapter.remove(originalIndex);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void onEventMainThread(final Event.ConversationUnreadEvent unreadEvent) {
        RLog.d(TAG, "ConversationUnreadEvent");

        ConversationType conversationType = unreadEvent.getType();
        String targetId = unreadEvent.getTargetId();
        int position = getGatherState(conversationType) ? mAdapter.findGatheredItem(conversationType) : mAdapter.findPosition(conversationType, targetId);
        if (position >= 0) {
            int first = mList.getFirstVisiblePosition();
            int last = mList.getLastVisiblePosition();
            UIConversation uiConversation = mAdapter.getItem(position);
            uiConversation.clearUnRead(conversationType, targetId);
            if (position >= first && position <= last) {
                mAdapter.getView(position, mList.getChildAt(position - mList.getFirstVisiblePosition()), mList);
            }
        }
    }

    /**
     * 不处理聚合情况下的置顶事件，保持按照时间排序规则。
     *
     * @param setTopEvent 置顶事件。
     */
    public void onEventMainThread(final Event.ConversationTopEvent setTopEvent) {
        RLog.d(TAG, "ConversationTopEvent");
        ConversationType conversationType = setTopEvent.getConversationType();
        String targetId = setTopEvent.getTargetId();
        int position = mAdapter.findPosition(conversationType, targetId);
        if (position >= 0 && !getGatherState(conversationType)) {
            UIConversation uiConversation = mAdapter.getItem(position);
            if (uiConversation.isTop() != setTopEvent.isTop()) {
                uiConversation.setTop(!uiConversation.isTop());
                mAdapter.remove(position);
                int index = getPosition(uiConversation);
                mAdapter.add(uiConversation, index);
                if (index == position) {
                    mAdapter.getView(index, mList.getChildAt(index - mList.getFirstVisiblePosition()), mList);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }


    public void onEventMainThread(final Event.ConversationRemoveEvent removeEvent) {
        RLog.d(TAG, "ConversationRemoveEvent");

        ConversationType conversationType = removeEvent.getType();
        removeConversation(conversationType, removeEvent.getTargetId());
    }

    public void onEventMainThread(final Event.ClearConversationEvent clearConversationEvent) {
        RLog.d(TAG, "ClearConversationEvent");

        List<Conversation.ConversationType> typeList = clearConversationEvent.getTypes();
        for (int i = mAdapter.getCount() - 1; i >= 0; i--) {
            if (typeList.indexOf(mAdapter.getItem(i).getConversationType()) >= 0) {
                mAdapter.remove(i);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public void onEventMainThread(Event.MessageDeleteEvent event) {
        RLog.d(TAG, "MessageDeleteEvent");

        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            if (event.getMessageIds().contains(mAdapter.getItem(i).getLatestMessageId())) {
                final boolean gatherState = mAdapter.getItem(i).getConversationGatherState();
                final int index = i;
                if (gatherState) {
                    RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                        @Override
                        public void onSuccess(List<Conversation> conversationList) {
                            if (conversationList == null || conversationList.size() == 0)
                                return;
                            UIConversation uiConversation = makeUIConversation(conversationList);
                            int oldPos = mAdapter.findPosition(uiConversation.getConversationType(), uiConversation.getConversationTargetId());
                            if (oldPos >= 0) {
                                mAdapter.remove(oldPos);
                            }
                            int newIndex = getPosition(uiConversation);
                            mAdapter.add(uiConversation, newIndex);
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {

                        }
                    }, mAdapter.getItem(index).getConversationType());

                } else {
                    RongIM.getInstance().getConversation(mAdapter.getItem(index).getConversationType(), mAdapter.getItem(index).getConversationTargetId(),
                            new RongIMClient.ResultCallback<Conversation>() {
                                @Override
                                public void onSuccess(Conversation conversation) {
                                    if (conversation == null) {
                                        RLog.d(TAG, "onEventMainThread getConversation : onSuccess, conversation = null");
                                        return;
                                    }
                                    UIConversation temp = UIConversation.obtain(conversation, false);

                                    int pos = mAdapter.findPosition(conversation.getConversationType(), conversation.getTargetId());
                                    if (pos >= 0) {
                                        mAdapter.remove(pos);
                                    }
                                    int newIndex = getPosition(temp);
                                    mAdapter.add(temp, newIndex);
                                    mAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode e) {
                                }
                            });
                }
                break;
            }
        }
    }

    public void onEventMainThread(Event.ConversationNotificationEvent notificationEvent) {
        int originalIndex = mAdapter.findPosition(notificationEvent.getConversationType(), notificationEvent.getTargetId());
        if (originalIndex >= 0) {
            mAdapter.getView(originalIndex, mList.getChildAt(originalIndex - mList.getFirstVisiblePosition()), mList);
        }
    }


    /**
     * 清除消息后，会话时间不会改变，依然跟随上一条消息的时间。
     * 那么会话所在的顺序也不会改变，仅需要清除会话列表内容
     *
     * @param clearMessagesEvent 清除消息事件。
     */
    public void onEventMainThread(Event.MessagesClearEvent clearMessagesEvent) {
        RLog.d(TAG, "MessagesClearEvent");
        ConversationType conversationType = clearMessagesEvent.getType();
        String targetId = clearMessagesEvent.getTargetId();
        int position = getGatherState(conversationType) ? mAdapter.findGatheredItem(conversationType) : mAdapter.findPosition(conversationType, targetId);
        if (position >= 0) {
            UIConversation uiConversation = mAdapter.getItem(position);
            uiConversation.clearLastMessage();
            mAdapter.getView(position, mList.getChildAt(position - mList.getFirstVisiblePosition()), mList);
        }
    }

    public void onEventMainThread(Event.OnMessageSendErrorEvent sendErrorEvent) {
        Message message = sendErrorEvent.getMessage();
        ConversationType conversationType = message.getConversationType();
        String targetId = message.getTargetId();
        if (isConfigured(conversationType)) {
            int first = mList.getFirstVisiblePosition();
            int last = mList.getLastVisiblePosition();
            boolean gathered = getGatherState(conversationType);
            int index = gathered ? mAdapter.findGatheredItem(conversationType) : mAdapter.findPosition(conversationType, targetId);
            if (index >= 0) {
                UIConversation uiConversation = mAdapter.getItem(index);
                message.setSentStatus(Message.SentStatus.FAILED);
                uiConversation.updateConversation(message, gathered);
                if (index >= first && index <= last) {
                    mAdapter.getView(index, mList.getChildAt(index - mList.getFirstVisiblePosition()), mList);
                }
            }
        }
    }

    public void onEventMainThread(Event.QuitDiscussionEvent event) {
        RLog.d(TAG, "QuitDiscussionEvent");
        removeConversation(ConversationType.DISCUSSION, event.getDiscussionId());
    }

    public void onEventMainThread(Event.QuitGroupEvent event) {
        RLog.d(TAG, "QuitGroupEvent");
        removeConversation(ConversationType.GROUP, event.getGroupId());
    }

    private void removeConversation(final ConversationType conversationType, String targetId) {
        boolean gathered = getGatherState(conversationType);
        if (gathered) {
            int index = mAdapter.findGatheredItem(conversationType);
            if (index >= 0) {
                RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                    @Override
                    public void onSuccess(List<Conversation> conversationList) {
                        int oldPos = mAdapter.findGatheredItem(conversationType);
                        if (oldPos >= 0) {
                            mAdapter.remove(oldPos);
                            if (conversationList != null && conversationList.size() > 0) {
                                UIConversation uiConversation = makeUIConversation(conversationList);
                                int newIndex = getPosition(uiConversation);
                                mAdapter.add(uiConversation, newIndex);
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode e) {

                    }
                }, conversationType);
            }
        } else {
            int index = mAdapter.findPosition(conversationType, targetId);
            if (index >= 0) {
                mAdapter.remove(index);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onPortraitItemClick(View v, UIConversation data) {
        ConversationType type = data.getConversationType();
        if (getGatherState(type)) {
            RongIM.getInstance().startSubConversationList(getActivity(), type);
        } else {
            if (RongContext.getInstance().getConversationListBehaviorListener() != null) {
                boolean isDefault = RongContext.getInstance().getConversationListBehaviorListener().onConversationPortraitClick(getActivity(), type, data.getConversationTargetId());
                if (isDefault)
                    return;
            }
            data.setUnReadMessageCount(0);
            RongIM.getInstance().startConversation(getActivity(), type, data.getConversationTargetId(), data.getUIConversationTitle());
        }

    }

    @Override
    public boolean onPortraitItemLongClick(View v, UIConversation data) {
        ConversationType type = data.getConversationType();

        if (RongContext.getInstance().getConversationListBehaviorListener() != null) {
            boolean isDealt = RongContext.getInstance().getConversationListBehaviorListener().onConversationPortraitLongClick(getActivity(), type, data.getConversationTargetId());
            Log.e("触发", "---boolean:" + isDealt);
            if (isDealt)
                return true;
        }
        if (!getGatherState(type)) {
            buildMultiDialog(data);
            return true;
        } else {
            buildSingleDialog(data);
            return true;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UIConversation uiConversation = mAdapter.getItem(position);
        ConversationType conversationType = uiConversation.getConversationType();
        if (getGatherState(conversationType)) {
            RongIM.getInstance().startSubConversationList(getActivity(), conversationType);
        } else {
            if (RongContext.getInstance().getConversationListBehaviorListener() != null
                    && RongContext.getInstance().getConversationListBehaviorListener().onConversationClick(getActivity(), view, uiConversation)) {
                return;
            }
            uiConversation.setUnReadMessageCount(0);
            RongIM.getInstance().startConversation(getActivity(), conversationType, uiConversation.getConversationTargetId(), uiConversation.getUIConversationTitle());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        UIConversation uiConversation = mAdapter.getItem(position);

        if (RongContext.getInstance().getConversationListBehaviorListener() != null) {
            boolean isDealt = RongContext.getInstance().getConversationListBehaviorListener().onConversationLongClick(getActivity(), view, uiConversation);
            if (isDealt)
                return true;
        }
        if (!getGatherState(uiConversation.getConversationType())) {
            buildMultiDialog(uiConversation);
            return true;
        } else {
            buildSingleDialog(uiConversation);
            return true;
        }
    }

    int startX = 0;
    int endX = 0;

    @Override
    public boolean OnFlinglistber(View view, UIConversation data, MotionEvent event) {
        boolean result = false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startX = (int) event.getX();
            Log.e("startX", "" + startX);
            result = false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            endX = (int) event.getX();
            if (startX - endX > 120) {
                Log.e("触发", "左划");
                ConversationType type = data.getConversationType();
//                if (getGatherState(type)) {
//                    RongIM.getInstance().startSubConversationList(getActivity(), type);
//                } else {
//                    if (RongContext.getInstance().getConversationListBehaviorListener() != null) {
//                        boolean isDefault = RongContext.getInstance().getConversationListBehaviorListener().onConversationPortraitClick(getActivity(), type, data.getConversationTargetId());
//                        if (isDefault){
//                            return;
//                        }
//                    }
//                    data.setUnReadMessageCount(0);
                RongIM.getInstance().startConversation(getActivity(), type, data.getConversationTargetId(), data.getUIConversationTitle());
//                }
                result = true;
            }
            if (endX - startX > 120) {
                Log.e("触发", "右划");
                result = true;
            }
        }
        Log.e("fanhuizhi", "--:" + result);
        return result;
    }

    private void buildMultiDialog(final UIConversation uiConversation) {

        String[] items = new String[2];

        if (uiConversation.isTop())
            items[0] = RongContext.getInstance().getString(R.string.rc_conversation_list_dialog_cancel_top);
        else
            items[0] = RongContext.getInstance().getString(R.string.rc_conversation_list_dialog_set_top);

        items[1] = RongContext.getInstance().getString(R.string.rc_conversation_list_dialog_remove);

        OptionsPopupDialog.newInstance(getActivity(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
            @Override
            public void onOptionsItemClicked(int which) {
                if (which == 0) {
                    RongIM.getInstance().setConversationToTop(
                            uiConversation.getConversationType(),
                            uiConversation.getConversationTargetId(),
                            !uiConversation.isTop(),
                            new RongIMClient.ResultCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {
                                    if (uiConversation.isTop()) {
                                        Toast.makeText(RongContext.getInstance(), getString(R.string.rc_conversation_list_popup_cancel_top), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(RongContext.getInstance(), getString(R.string.rc_conversation_list_dialog_set_top), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode e) {

                                }
                            });
                } else if (which == 1) {
                    RongIM.getInstance().removeConversation(uiConversation.getConversationType(), uiConversation.getConversationTargetId(), null);
                }
            }
        }).show();
    }

    private void buildSingleDialog(final UIConversation uiConversation) {

        String[] items = new String[1];
        items[0] = RongContext.getInstance().getString(R.string.rc_conversation_list_dialog_remove);

        OptionsPopupDialog.newInstance(getActivity(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
            @Override
            public void onOptionsItemClicked(int which) {
                RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                    @Override
                    public void onSuccess(List<Conversation> conversations) {
                        if (conversations != null && conversations.size() > 0) {
                            for (Conversation conversation : conversations) {
                                RongIMClient.getInstance().removeConversation(conversation.getConversationType(), conversation.getTargetId(), null);
                            }
                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                    }

                }, uiConversation.getConversationType());

                int position = mAdapter.findGatheredItem(uiConversation.getConversationType());
                mAdapter.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        }).show();
    }

    // conversationList排序规律：
    // 1. 首先是top会话，按时间顺序排列。
    // 2. 然后非top会话也是按时间排列。
    private void makeUiConversationList(List<Conversation> conversationList) {
        UIConversation uiConversation;
        for (Conversation conversation : conversationList) {
            ConversationType conversationType = conversation.getConversationType();
            String targetId = conversation.getTargetId();
            boolean gatherState = getGatherState(conversationType);
            int originalIndex;
            if (gatherState) {
                originalIndex = mAdapter.findGatheredItem(conversationType);
                if (originalIndex >= 0) {
                    uiConversation = mAdapter.getItem(originalIndex);
                    uiConversation.updateConversation(conversation, true);
                } else {
                    uiConversation = UIConversation.obtain(conversation, true);
                    mAdapter.add(uiConversation);
                }
            } else {
                originalIndex = mAdapter.findPosition(conversationType, targetId);
                if (originalIndex < 0) {
                    uiConversation = UIConversation.obtain(conversation, false);
                    mAdapter.add(uiConversation);
                } else {
                    uiConversation = mAdapter.getItem(originalIndex);
                    uiConversation.setUnReadMessageCount(conversation.getUnreadMessageCount());
                }
            }
        }
    }

    /**
     * 根据conversations列表，构建新的会话。如：聚合情况下，删掉某条子会话时，根据剩余会话构建新的UI会话。
     */
    private UIConversation makeUIConversation(List<Conversation> conversations) {
        int unreadCount = 0;
        boolean topFlag = false;
        boolean isMentioned = false;
        Conversation newest = conversations.get(0);

        for (Conversation conversation : conversations) {
            if (newest.isTop()) {
                if (conversation.isTop() && conversation.getSentTime() > newest.getSentTime()) {
                    newest = conversation;
                }
            } else {
                if (conversation.isTop() || conversation.getSentTime() > newest.getSentTime()) {
                    newest = conversation;
                }
            }
            if (conversation.isTop()) {
                topFlag = true;
            }
            if (conversation.getMentionedCount() > 0) {
                isMentioned = true;
            }

            unreadCount = unreadCount + conversation.getUnreadMessageCount();
        }

        UIConversation uiConversation = UIConversation.obtain(newest, getGatherState(newest.getConversationType()));
        uiConversation.setUnReadMessageCount(unreadCount);
        //聚合模式，才会调用此方法，top 为 false。
        uiConversation.setTop(false);
        uiConversation.setMentionedFlag(isMentioned);
        return uiConversation;
    }

    private int getPosition(UIConversation uiConversation) {
        int count = mAdapter.getCount();
        int i, position = 0;

        for (i = 0; i < count; i++) {
            if (uiConversation.isTop()) {
                if (mAdapter.getItem(i).isTop() && mAdapter.getItem(i).getUIConversationTime() > uiConversation.getUIConversationTime())
                    position++;
                else
                    break;
            } else {
                if (mAdapter.getItem(i).isTop() || mAdapter.getItem(i).getUIConversationTime() > uiConversation.getUIConversationTime())
                    position++;
                else
                    break;
            }
        }

        return position;
    }

    private boolean isConfigured(ConversationType conversationType) {
        for (int i = 0; i < mConversationsConfig.size(); i++) {
            if (conversationType.equals(mConversationsConfig.get(i).conversationType)) {
                return true;
            }
        }
        return false;
    }

    public boolean getGatherState(Conversation.ConversationType conversationType) {
        for (ConversationConfig config : mConversationsConfig) {
            if (config.conversationType.equals(conversationType)) {
                return config.isGathered;
            }
        }
        return false;
    }

    private Conversation.ConversationType[] getConfigConversationTypes() {
        Conversation.ConversationType[] conversationTypes = new Conversation.ConversationType[mConversationsConfig.size()];
        for (int i = 0; i < mConversationsConfig.size(); i++) {
            conversationTypes[i] = mConversationsConfig.get(i).conversationType;
        }
        return conversationTypes;
    }

    private class ConversationConfig {
        Conversation.ConversationType conversationType;
        boolean isGathered;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(mThis);
        super.onDestroyView();
    }
}
