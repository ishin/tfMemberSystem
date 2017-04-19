package io.rong.imkit.manager;

import java.util.ArrayList;
import java.util.List;

import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imkit.model.Event;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by yuejunhong on 16/9/13.
 */
public class UnReadMessageManager {
    private final static String TAG = "UnReadMessageManager";
    private List<MultiConversationUnreadMsgInfo> mMultiConversationUnreadInfos;
    private int left;

    private UnReadMessageManager() {
        mMultiConversationUnreadInfos = new ArrayList<>();
        EventBus.getDefault().register(this);
    }

    private static class SingletonHolder {
        static UnReadMessageManager sInstance = new UnReadMessageManager();
    }

    public static UnReadMessageManager getInstance() {
        return SingletonHolder.sInstance;
    }

    public void onEventMainThread(final Event.OnReceiveMessageEvent event)  {
        RLog.d(TAG, "OnReceiveMessageEvent " + event.getLeft());
        left = event.getLeft();
        syncUnreadCount(event.getLeft());
    }

    public void onEventMainThread(final Event.MessageLeftEvent event)  {
        RLog.d(TAG, "MessageLeftEvent " + event.left);
        left = event.left;
        syncUnreadCount(event.left);
    }

    private void syncUnreadCount(int left) {
        for (final MultiConversationUnreadMsgInfo msgInfo : mMultiConversationUnreadInfos) {
            if (left == 0) {
                RongIMClient.getInstance().getUnreadCount(msgInfo.conversationTypes, new RongIMClient.ResultCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        RLog.d(TAG, "get result: " + integer);
                        msgInfo.count = integer;
                        msgInfo.observer.onCountChanged(integer);
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode e) {

                    }
                });
            } else {
                msgInfo.count++;
                msgInfo.observer.onCountChanged(msgInfo.count);
            }
        }
    }

    public void onEventMainThread(final Event.ConversationRemoveEvent removeEvent) {
        Conversation.ConversationType conversationType = removeEvent.getType();
        for (final MultiConversationUnreadMsgInfo msgInfo : mMultiConversationUnreadInfos) {
            for (Conversation.ConversationType ct : msgInfo.conversationTypes) {
                if (ct.equals(conversationType)) {
                    RongIMClient.getInstance().getUnreadCount(msgInfo.conversationTypes, new RongIMClient.ResultCallback<Integer>() {
                        @Override
                        public void onSuccess(Integer integer) {
                            msgInfo.count = integer;
                            msgInfo.observer.onCountChanged(integer);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {

                        }
                    });
                    break;
                }
            }
        }
    }

    public void onEventMainThread(final Event.ConversationUnreadEvent unreadEvent) {
        Conversation.ConversationType conversationType = unreadEvent.getType();
        for (final MultiConversationUnreadMsgInfo msgInfo : mMultiConversationUnreadInfos) {
            for (Conversation.ConversationType ct : msgInfo.conversationTypes) {
                if (ct.equals(conversationType)) {
                    RongIMClient.getInstance().getUnreadCount(msgInfo.conversationTypes, new RongIMClient.ResultCallback<Integer>() {
                        @Override
                        public void onSuccess(Integer integer) {
                            msgInfo.count = integer;
                            msgInfo.observer.onCountChanged(integer);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {

                        }
                    });
                    break;
                }
            }
        }
    }

    public void addObserver(Conversation.ConversationType[] conversationTypes, final IUnReadMessageObserver observer) {
        final MultiConversationUnreadMsgInfo msgInfo = new MultiConversationUnreadMsgInfo();
        msgInfo.conversationTypes = conversationTypes;
        msgInfo.observer = observer;
        mMultiConversationUnreadInfos.add(msgInfo);
        RongIMClient.getInstance().getUnreadCount(conversationTypes, new RongIMClient.ResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                msgInfo.count = integer;
                msgInfo.observer.onCountChanged(integer);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {

            }
        });
    }

    public void removeObserver(final IUnReadMessageObserver observer) {
        MultiConversationUnreadMsgInfo result = null;
        for (final MultiConversationUnreadMsgInfo msgInfo : mMultiConversationUnreadInfos) {
            if (msgInfo.observer == observer) {
                result = msgInfo;
                break;
            }
        }
        if (result != null) {
            mMultiConversationUnreadInfos.remove(result);
        }
    }

    public void clearObserver() {
        mMultiConversationUnreadInfos.clear();
    }

    public void onEventMainThread(Event.SyncReadStatusEvent event) {
        RLog.d(TAG, "SyncReadStatusEvent " + left);
        if (left == 0) {
            Conversation.ConversationType conversationType = event.getConversationType();
            for (final MultiConversationUnreadMsgInfo msgInfo : mMultiConversationUnreadInfos) {
                for (Conversation.ConversationType ct : msgInfo.conversationTypes) {
                    if (ct.equals(conversationType)) {
                        RongIMClient.getInstance().getUnreadCount(msgInfo.conversationTypes, new RongIMClient.ResultCallback<Integer>() {
                            @Override
                            public void onSuccess(Integer integer) {
                                msgInfo.count = integer;
                                msgInfo.observer.onCountChanged(integer);
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode e) {

                            }
                        });
                        break;
                    }
                }
            }
        }
    }

    private class MultiConversationUnreadMsgInfo {
        Conversation.ConversationType[] conversationTypes;
        int count;
        IUnReadMessageObserver observer;
    }
}
