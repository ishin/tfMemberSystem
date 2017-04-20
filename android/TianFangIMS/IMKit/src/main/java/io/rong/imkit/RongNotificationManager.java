package io.rong.imkit;

import android.text.Spannable;
import android.text.TextUtils;

import java.util.concurrent.ConcurrentHashMap;

import io.rong.common.RLog;
import io.rong.imkit.model.ConversationKey;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.UserInfo;
import io.rong.message.RecallNotificationMessage;
import io.rong.push.RongPushClient;
import io.rong.push.notification.PushNotificationMessage;

public class RongNotificationManager {
    private final static String TAG = "RongNotificationManager";
    private static RongNotificationManager sS;
    RongContext mContext;
    ConcurrentHashMap<String, Message> messageMap = new ConcurrentHashMap<>();

    static {
        sS = new RongNotificationManager();
    }

    private RongNotificationManager() {
    }

    public void init(RongContext context) {
        mContext = context;
        messageMap.clear();
        if (!context.getEventBus().isRegistered(this)) {
            context.getEventBus().register(this);
        }
    }

    public static RongNotificationManager getInstance() {
        if (sS == null) {
            sS = new RongNotificationManager();
        }
        return sS;
    }

    public void onReceiveMessageFromApp(Message message) {
        Conversation.ConversationType type = message.getConversationType();
        String targetName = null;
        String userName = "";
        PushNotificationMessage pushMsg;

        IContainerItemProvider.MessageProvider provider = RongContext.getInstance().getMessageTemplate(message.getContent().getClass());
        if (provider == null)
            return;

        Spannable content = provider.getContentSummary(message.getContent());
        ConversationKey targetKey = ConversationKey.obtain(message.getTargetId(), message.getConversationType());
        if (targetKey == null) {
            RLog.e(TAG, "onReceiveMessageFromApp targetKey is null");
        }
        RLog.i(TAG, "onReceiveMessageFromApp start");

        if (content == null) {
            RLog.i(TAG, "onReceiveMessageFromApp Content is null. Return directly.");
            return;
        }

        if (type.equals(Conversation.ConversationType.PRIVATE) || type.equals(Conversation.ConversationType.CUSTOMER_SERVICE)
                || type.equals(Conversation.ConversationType.CHATROOM) || type.equals(Conversation.ConversationType.SYSTEM)) {
            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getTargetId());
            if (userInfo != null)
                targetName = userInfo.getName();
            if (!TextUtils.isEmpty(targetName)) {
                pushMsg = new PushNotificationMessage();
                pushMsg.setPushContent(content.toString());
                pushMsg.setConversationType(RongPushClient.ConversationType.setValue(message.getConversationType().getValue()));
                pushMsg.setTargetId(message.getTargetId());
                pushMsg.setTargetUserName(targetName);
                pushMsg.setSenderId(message.getTargetId());
                pushMsg.setSenderName(targetName);
                pushMsg.setObjectName(message.getObjectName());
                pushMsg.setPushFlag("false");
                RongPushClient.sendNotification(mContext, pushMsg);
            } else {
                if (targetKey != null) {
                    messageMap.put(targetKey.getKey(), message);
                }
            }
        } else if (type.equals(Conversation.ConversationType.GROUP)) {
            Group groupInfo = RongUserInfoManager.getInstance().getGroupInfo(message.getTargetId());
            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
            if (groupInfo != null) {
                targetName = groupInfo.getName();
            }
            if (userInfo != null) {
                userName = userInfo.getName();
            }
            if (!TextUtils.isEmpty(targetName) && !TextUtils.isEmpty(userName)) {
                String notificationContent;
                if (isMentionedMessage(message)) {
                    if (TextUtils.isEmpty(message.getContent().getMentionedInfo().getMentionedContent())) {
                        notificationContent = mContext.getString(R.string.rc_message_content_mentioned) + userName + " : " + content.toString();
                    } else {
                        notificationContent = message.getContent().getMentionedInfo().getMentionedContent();
                    }
                } else if (message.getContent() instanceof RecallNotificationMessage){
                    notificationContent = content.toString();
                }else {
                    notificationContent = userName + " : " + content.toString();
                }
                pushMsg = new PushNotificationMessage();
                pushMsg.setPushContent(notificationContent);
                pushMsg.setConversationType(RongPushClient.ConversationType.setValue(message.getConversationType().getValue()));
                pushMsg.setTargetId(message.getTargetId());
                pushMsg.setTargetUserName(targetName);
                pushMsg.setObjectName(message.getObjectName());
                pushMsg.setPushFlag("false");
                RongPushClient.sendNotification(mContext, pushMsg);
            } else {
                if (TextUtils.isEmpty(targetName)) {
                    if (targetKey != null) {
                        messageMap.put(targetKey.getKey(), message);
                    }
                }
                if (TextUtils.isEmpty(userName)) {
                    ConversationKey senderKey = ConversationKey.obtain(message.getSenderUserId(), type);
                    if (senderKey != null) {
                        messageMap.put(senderKey.getKey(), message);
                    } else {
                        RLog.e(TAG, "onReceiveMessageFromApp senderKey is null");
                    }
                }
            }
        } else if (type.equals(Conversation.ConversationType.DISCUSSION)) {
            Discussion discussionInfo = RongUserInfoManager.getInstance().getDiscussionInfo(message.getTargetId());
            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());

            if (discussionInfo != null) {
                targetName = discussionInfo.getName();
            }
            if (userInfo != null) {
                userName = userInfo.getName();
            }
            if (!TextUtils.isEmpty(targetName) && !TextUtils.isEmpty(userName)) {
                String notificationContent;
                if (isMentionedMessage(message)) {
                    if (TextUtils.isEmpty(message.getContent().getMentionedInfo().getMentionedContent())) {
                        notificationContent = mContext.getString(R.string.rc_message_content_mentioned) + userName + " : " + content.toString();
                    } else {
                        notificationContent = message.getContent().getMentionedInfo().getMentionedContent();
                    }
                } else {
                    notificationContent = userName + " : " + content.toString();
                }
                pushMsg = new PushNotificationMessage();
                pushMsg.setPushContent(notificationContent);
                pushMsg.setConversationType(RongPushClient.ConversationType.setValue(message.getConversationType().getValue()));
                pushMsg.setTargetId(message.getTargetId());
                pushMsg.setTargetUserName(targetName);
                pushMsg.setObjectName(message.getObjectName());
                pushMsg.setPushFlag("false");
                RongPushClient.sendNotification(mContext, pushMsg);
            } else {
                if (TextUtils.isEmpty(targetName)) {
                    if (targetKey != null) {
                        messageMap.put(targetKey.getKey(), message);
                    }
                }
                if (TextUtils.isEmpty(userName)) {
                    ConversationKey senderKey = ConversationKey.obtain(message.getSenderUserId(), type);
                    if (senderKey != null) {
                        messageMap.put(senderKey.getKey(), message);
                    } else {
                        RLog.e(TAG, "onReceiveMessageFromApp senderKey is null");
                    }
                }
            }
        } else if (type.getName().equals(Conversation.ConversationType.PUBLIC_SERVICE.getName()) ||
                   type.getName().equals(Conversation.PublicServiceType.APP_PUBLIC_SERVICE.getName())) {
            if (targetKey != null) {
                PublicServiceProfile info = RongContext.getInstance().getPublicServiceInfoFromCache(targetKey.getKey());
                if (info != null) {
                    targetName = info.getName();
                }
            }
            if (!TextUtils.isEmpty(targetName)) {
                pushMsg = new PushNotificationMessage();
                pushMsg.setPushContent(content.toString());
                pushMsg.setConversationType(RongPushClient.ConversationType.setValue(message.getConversationType().getValue()));
                pushMsg.setTargetId(message.getTargetId());
                pushMsg.setTargetUserName(targetName);
                pushMsg.setObjectName(message.getObjectName());
                pushMsg.setPushFlag("false");
                RongPushClient.sendNotification(mContext, pushMsg);
            } else {
                if (targetKey != null) {
                    messageMap.put(targetKey.getKey(), message);
                }
            }
        }
    }

    public void onEventMainThread(UserInfo userInfo) {
        Message message;
        PushNotificationMessage pushMsg;

        Conversation.ConversationType[] types = new Conversation.ConversationType[] {
            Conversation.ConversationType.PRIVATE, Conversation.ConversationType.GROUP,
            Conversation.ConversationType.DISCUSSION, Conversation.ConversationType.CUSTOMER_SERVICE,
            Conversation.ConversationType.CHATROOM, Conversation.ConversationType.SYSTEM
        };

        for (Conversation.ConversationType type : types) {
            String key = ConversationKey.obtain(userInfo.getUserId(), type).getKey();

            if (messageMap.containsKey(key)) {
                message = messageMap.get(key);
                String targetName = "";
                String notificationContent = "";
                Spannable content = RongContext.getInstance().getMessageTemplate(message.getContent().getClass())
                                    .getContentSummary(message.getContent());

                messageMap.remove(key);

                if (type.equals(Conversation.ConversationType.GROUP)) {
                    Group groupInfo = RongUserInfoManager.getInstance().getGroupInfo(message.getTargetId());
                    if (groupInfo != null) {
                        targetName = groupInfo.getName();
                    }

                    if (isMentionedMessage(message)) {
                        if (TextUtils.isEmpty(message.getContent().getMentionedInfo().getMentionedContent())) {
                            notificationContent = mContext.getString(R.string.rc_message_content_mentioned) + userInfo.getName() + " : " + content.toString();
                        } else {
                            notificationContent = message.getContent().getMentionedInfo().getMentionedContent();
                        }
                    } else {
                        notificationContent = userInfo.getName() + " : " + content.toString();
                    }
                } else if (type.equals(Conversation.ConversationType.DISCUSSION)) {
                    Discussion discussion = RongUserInfoManager.getInstance().getDiscussionInfo(message.getTargetId());
                    if (discussion != null) {
                        targetName = discussion.getName();
                    }
                    if (isMentionedMessage(message)) {
                        if (TextUtils.isEmpty(message.getContent().getMentionedInfo().getMentionedContent())) {
                            notificationContent = mContext.getString(R.string.rc_message_content_mentioned) + userInfo.getName() + " : " + content.toString();
                        } else {
                            notificationContent = message.getContent().getMentionedInfo().getMentionedContent();
                        }
                    } else {
                        notificationContent = userInfo.getName() + " : " + content.toString();
                    }
                } else {
                    targetName = userInfo.getName();
                    notificationContent = content.toString();
                }
                if (TextUtils.isEmpty(targetName))
                    return;

                pushMsg = new PushNotificationMessage();
                pushMsg.setPushContent(notificationContent);
                pushMsg.setConversationType(RongPushClient.ConversationType.setValue(message.getConversationType().getValue()));
                pushMsg.setTargetId(message.getTargetId());
                pushMsg.setTargetUserName(targetName);
                pushMsg.setObjectName(message.getObjectName());
                pushMsg.setPushFlag("false");
                RongPushClient.sendNotification(mContext, pushMsg);
            }
        }
    }

    public void onEventMainThread(Group groupInfo) {
        Message message;
        PushNotificationMessage pushMsg;
        String key = ConversationKey.obtain(groupInfo.getId(), Conversation.ConversationType.GROUP).getKey();

        if (messageMap.containsKey(key)) {
            message = messageMap.get(key);
            String userName = "";
            Spannable content = RongContext.getInstance().getMessageTemplate(message.getContent().getClass())
                                .getContentSummary(message.getContent());

            messageMap.remove(key);

            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
            if (userInfo != null) {
                userName = userInfo.getName();
                if (TextUtils.isEmpty(userName))
                    return;
            }

            pushMsg = new PushNotificationMessage();
            pushMsg.setPushContent(userName + " : " + content.toString());
            pushMsg.setConversationType(RongPushClient.ConversationType.setValue(message.getConversationType().getValue()));
            pushMsg.setTargetId(message.getTargetId());
            pushMsg.setTargetUserName(groupInfo.getName());
            pushMsg.setObjectName(message.getObjectName());
            pushMsg.setPushFlag("false");
            RongPushClient.sendNotification(mContext, pushMsg);
        }

    }

    public void onEventMainThread(Discussion discussion) {
        Message message;
        PushNotificationMessage pushMsg;
        String key = ConversationKey.obtain(discussion.getId(), Conversation.ConversationType.DISCUSSION).getKey();
        if (messageMap.containsKey(key)) {
            String userName = "";
            message = messageMap.get(key);
            Spannable content = RongContext.getInstance().getMessageTemplate(message.getContent().getClass())
                                .getContentSummary(message.getContent());

            messageMap.remove(key);

            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
            if (userInfo != null) {
                userName = userInfo.getName();
                if (TextUtils.isEmpty(userName))
                    return;
            }

            pushMsg = new PushNotificationMessage();
            pushMsg.setPushContent(userName + " : " + content.toString());
            pushMsg.setConversationType(RongPushClient.ConversationType.setValue(message.getConversationType().getValue()));
            pushMsg.setTargetId(message.getTargetId());
            pushMsg.setTargetUserName(discussion.getName());
            pushMsg.setObjectName(message.getObjectName());
            pushMsg.setPushFlag("false");
            RongPushClient.sendNotification(mContext, pushMsg);
        }
    }

    public void onEventMainThread(PublicServiceProfile info) {
        Message message;
        PushNotificationMessage pushMsg;
        String key = ConversationKey.obtain(info.getTargetId(), info.getConversationType()).getKey();

        if (messageMap.containsKey(key)) {
            message = messageMap.get(key);
            Spannable content = RongContext.getInstance().getMessageTemplate(message.getContent().getClass())
                                .getContentSummary(message.getContent());

            pushMsg = new PushNotificationMessage();
            pushMsg.setPushContent(content.toString());
            pushMsg.setConversationType(RongPushClient.ConversationType.setValue(message.getConversationType().getValue()));
            pushMsg.setTargetId(message.getTargetId());
            pushMsg.setTargetUserName(info.getName());
            pushMsg.setObjectName(message.getObjectName());
            pushMsg.setPushFlag("false");
            RongPushClient.sendNotification(mContext, pushMsg);
            messageMap.remove(key);
        }
    }

    private boolean isMentionedMessage(Message message) {
        MentionedInfo mentionedInfo = message.getContent().getMentionedInfo();
        if (mentionedInfo != null && (mentionedInfo.getType().equals(MentionedInfo.MentionedType.ALL)
                                      || (mentionedInfo.getType().equals(MentionedInfo.MentionedType.PART)
                                          && mentionedInfo.getMentionedUserIdList() != null
                                          && mentionedInfo.getMentionedUserIdList().contains(RongIMClient.getInstance().getCurrentUserId())))) {
            return true;
        }
        return false;
    }
}