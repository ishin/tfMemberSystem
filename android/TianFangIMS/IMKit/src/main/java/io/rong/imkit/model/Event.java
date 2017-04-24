package io.rong.imkit.model;

import android.app.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.RecallNotificationMessage;

/**
 * Created by DragonJ on 15/4/7.
 */
public class Event {

    public static class FileMessageEvent {
        Message message;
        int progress;
        int callBackType;
        RongIMClient.ErrorCode errorCode;

        public FileMessageEvent(Message message, int progress, int callBackType, RongIMClient.ErrorCode errorCode) {
            this.message = message;
            this.progress = progress;
            this.callBackType = callBackType;
            this.errorCode = errorCode;
        }
        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public int getCallBackType() {
            return callBackType;
        }

        public void setCallBackType(int callBackType) {
            this.callBackType = callBackType;
        }
        public RongIMClient.ErrorCode getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(RongIMClient.ErrorCode errorCode) {
            this.errorCode = errorCode;
        }
    }

    public static class OnReceiveMessageEvent {
        Message message;
        int left;

        public OnReceiveMessageEvent(Message message, int left) {
            this.message = message;
            this.left = left;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
        }
    }

    public static class MessageLeftEvent {
        public int left;

        public MessageLeftEvent(int left) {
            this.left = left;
        }
    }

    public static class OnReceiveMessageProgressEvent {
        Message message;
        int progress;

        public OnReceiveMessageProgressEvent() {
        }

        public int getProgress() {
            return progress;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }
    }

    public static class OnMessageSendErrorEvent {
        Message message;
        RongIMClient.ErrorCode errorCode;

        public OnMessageSendErrorEvent(Message message, RongIMClient.ErrorCode errorCode) {
            this.message = message;
            this.errorCode = errorCode;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public RongIMClient.ErrorCode getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(RongIMClient.ErrorCode errorCode) {
            this.errorCode = errorCode;
        }
    }

    public static class ConversationUnreadEvent {
        Conversation.ConversationType type;
        String targetId;

        public ConversationUnreadEvent(Conversation.ConversationType type, String targetId) {
            this.type = type;
            this.targetId = targetId;
        }

        public Conversation.ConversationType getType() {
            return type;
        }

        public void setType(Conversation.ConversationType type) {
            this.type = type;
        }

        public String getTargetId() {
            return targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }
    }


    public static class ConversationTopEvent extends BaseConversationEvent {
        boolean isTop;

        public ConversationTopEvent(Conversation.ConversationType type, String targetId, boolean isTop) {
            setConversationType(type);
            setTargetId(targetId);
            this.isTop = isTop;
        }

        public boolean isTop() {
            return isTop;
        }

        public void setTop(boolean isTop) {
            this.isTop = isTop;
        }
    }

    public static class ConversationRemoveEvent {
        Conversation.ConversationType type;
        String targetId;

        public ConversationRemoveEvent(Conversation.ConversationType type, String targetId) {
            this.type = type;
            this.targetId = targetId;
        }

        public Conversation.ConversationType getType() {
            return type;
        }

        public void setType(Conversation.ConversationType type) {
            this.type = type;
        }

        public String getTargetId() {
            return targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }
    }

    public static class MessageSentStatusEvent {
        int messageId;
        Message.SentStatus sentStatus;

        public MessageSentStatusEvent(int messageId, Message.SentStatus sentStatus) {
            this.messageId = messageId;
            this.sentStatus = sentStatus;
        }

        public int getMessageId() {
            return messageId;
        }

        public void setMessageId(int messageId) {
            this.messageId = messageId;
        }

        public Message.SentStatus getSentStatus() {
            return sentStatus;
        }

        public void setSentStatus(Message.SentStatus sentStatus) {
            this.sentStatus = sentStatus;
        }
    }


    public static class MessageDeleteEvent {
        List<Integer> messageIds;

        public MessageDeleteEvent(int... ids) {
            if (ids == null || ids.length == 0)
                return;
            messageIds = new ArrayList<>();
            for (int id : ids) {
                messageIds.add(id);
            }
        }

        public List<Integer> getMessageIds() {
            return messageIds;
        }

        public void setMessageIds(List<Integer> messageIds) {
            this.messageIds = messageIds;
        }
    }

    public static class MessagesClearEvent {
        Conversation.ConversationType type;
        String targetId;

        public MessagesClearEvent(Conversation.ConversationType type, String targetId) {
            this.type = type;
            this.targetId = targetId;
        }

        public Conversation.ConversationType getType() {
            return type;
        }

        public void setType(Conversation.ConversationType type) {
            this.type = type;
        }

        public String getTargetId() {
            return targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }
    }

    public static class CreateDiscussionEvent {
        String discussionId;
        String discussionName;
        List<String> userIdList;

        public CreateDiscussionEvent(String discussionId, String discussionName, List<String> userIdList) {
            this.discussionId = discussionId;
            this.discussionName = discussionName;
            this.userIdList = userIdList;
        }

        public String getDiscussionId() {
            return discussionId;
        }

        public void setDiscussionId(String discussionId) {
            this.discussionId = discussionId;
        }

        public String getDiscussionName() {
            return discussionName;
        }

        public void setDiscussionName(String discussionName) {
            this.discussionName = discussionName;
        }

        public List<String> getUserIdList() {
            return userIdList;
        }

        public void setUserIdList(List<String> userIdList) {
            this.userIdList = userIdList;
        }
    }

    public static class AddMemberToDiscussionEvent {
        String discussionId;
        List<String> userIdList;

        public AddMemberToDiscussionEvent(String discussionId, List<String> userIdList) {
            this.discussionId = discussionId;
            this.userIdList = userIdList;
        }

        public String getDiscussionId() {
            return discussionId;
        }

        public void setDiscussionId(String discussionId) {
            this.discussionId = discussionId;
        }

        public List<String> getUserIdList() {
            return userIdList;
        }

        public void setUserIdList(List<String> userIdList) {
            this.userIdList = userIdList;
        }
    }

    public static class RemoveMemberFromDiscussionEvent {
        String discussionId;
        String userId;

        public RemoveMemberFromDiscussionEvent(String discussionId, String userId) {
            this.discussionId = discussionId;
            this.userId = userId;
        }

        public String getDiscussionId() {
            return discussionId;
        }

        public void setDiscussionId(String discussionId) {
            this.discussionId = discussionId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    public static class QuitDiscussionEvent {
        String discussionId;

        public QuitDiscussionEvent(String discussionId) {
            this.discussionId = discussionId;
        }

        public String getDiscussionId() {
            return discussionId;
        }

        public void setDiscussionId(String discussionId) {
            this.discussionId = discussionId;
        }
    }

    public static class DiscussionInviteStatusEvent {
        String discussionId;
        RongIMClient.DiscussionInviteStatus status;

        public DiscussionInviteStatusEvent(String discussionId, RongIMClient.DiscussionInviteStatus status) {
            this.discussionId = discussionId;
            this.status = status;
        }

        public String getDiscussionId() {
            return discussionId;
        }

        public void setDiscussionId(String discussionId) {
            this.discussionId = discussionId;
        }


        public RongIMClient.DiscussionInviteStatus getStatus() {
            return status;
        }

        public void setStatus(RongIMClient.DiscussionInviteStatus status) {
            this.status = status;
        }
    }

    public static class SyncGroupEvent {
        List<Group> groups;

        public SyncGroupEvent(List<Group> groups) {
            this.groups = groups;
        }

        public List<Group> getGroups() {
            return groups;
        }

        public void setGroups(List<Group> groups) {
            this.groups = groups;
        }
    }

    public static class JoinGroupEvent {
        String groupId, groupName;

        public JoinGroupEvent(String groupId, String groupName) {
            this.groupId = groupId;
            this.groupName = groupName;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }
    }

    public static class QuitGroupEvent {
        String groupId;

        public QuitGroupEvent(String groupId) {
            this.groupId = groupId;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
    }

    public static class JoinChatRoomEvent {
        String chatRoomId;
        int defMessageCount;

        public JoinChatRoomEvent(String chatRoomId, int defMessageCount) {
            this.chatRoomId = chatRoomId;
            this.defMessageCount = defMessageCount;
        }

        public String getChatRoomId() {
            return chatRoomId;
        }

        public void setChatRoomId(String chatRoomId) {
            this.chatRoomId = chatRoomId;
        }

        public int getDefMessageCount() {
            return defMessageCount;
        }

        public void setDefMessageCount(int defMessageCount) {
            this.defMessageCount = defMessageCount;
        }
    }

    public static class QuitChatRoomEvent {
        String chatRoomId;

        public QuitChatRoomEvent(String chatRoomId) {
            this.chatRoomId = chatRoomId;
        }

        public String getChatRoomId() {
            return chatRoomId;
        }

        public void setChatRoomId(String chatRoomId) {
            this.chatRoomId = chatRoomId;
        }
    }

    public static class AddToBlacklistEvent {
        String userId;

        public AddToBlacklistEvent(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    public static class RemoveFromBlacklistEvent {
        String userId;

        public RemoveFromBlacklistEvent(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    protected static class BaseConversationEvent {
        protected Conversation.ConversationType mConversationType;
        protected String mTargetId;


        public Conversation.ConversationType getConversationType() {
            return mConversationType;
        }

        public void setConversationType(Conversation.ConversationType conversationType) {
            mConversationType = conversationType;
        }

        public String getTargetId() {
            return mTargetId;
        }

        public void setTargetId(String targetId) {
            mTargetId = targetId;
        }
    }

    public static class ConversationNotificationEvent extends BaseConversationEvent {

        private Conversation.ConversationNotificationStatus mStatus;

        public ConversationNotificationEvent(String targetId, Conversation.ConversationType conversationType, Conversation.ConversationNotificationStatus conversationNotificationStatus) {
            setTargetId(targetId);
            setConversationType(conversationType);
            setStatus(conversationNotificationStatus);
        }

        public static ConversationNotificationEvent obtain(String targetId, Conversation.ConversationType conversationType, Conversation.ConversationNotificationStatus conversationNotificationStatus) {
            return new ConversationNotificationEvent(targetId, conversationType, conversationNotificationStatus);
        }

        public Conversation.ConversationNotificationStatus getStatus() {
            return mStatus;
        }

        public void setStatus(Conversation.ConversationNotificationStatus status) {
            mStatus = status;
        }

    }

    public static class PublicServiceFollowableEvent extends BaseConversationEvent {
        private boolean isFollow = false;

        public PublicServiceFollowableEvent(String targetId, Conversation.ConversationType conversationType, boolean isFollow) {
            setTargetId(targetId);
            setConversationType(conversationType);
            setIsFollow(isFollow);
        }

        public static PublicServiceFollowableEvent obtain(String targetId, Conversation.ConversationType conversationType, boolean isFollow) {
            return new PublicServiceFollowableEvent(targetId, conversationType, isFollow);
        }

        public boolean isFollow() {
            return isFollow;
        }

        public void setIsFollow(boolean isFollow) {
            this.isFollow = isFollow;
        }
    }


    public static class VoiceInputOperationEvent {

        public static int STATUS_DEFAULT = -1;
        public static int STATUS_INPUTING = 0;
        public static int STATUS_INPUT_COMPLETE = 1;

        private int status;

        public VoiceInputOperationEvent(int status) {
            setStatus(status);
        }

        public static VoiceInputOperationEvent obtain(int status) {
            return new VoiceInputOperationEvent(status);
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    public static class AudioListenedEvent extends BaseConversationEvent {
        private Message message;

        public AudioListenedEvent(Message message) {
            this.message = message;
        }

        public Message getMessage() {
            return message;
        }
    }


    public static class NotificationUserInfoEvent {
        NotificationUserInfoEvent(String key) {
            setKey(key);
        }

        public static NotificationUserInfoEvent obtain(String key) {
            return new NotificationUserInfoEvent(key);
        }

        private String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class NotificationGroupInfoEvent {

        NotificationGroupInfoEvent(String key) {
            setKey(key);
        }

        public static NotificationGroupInfoEvent obtain(String key) {
            return new NotificationGroupInfoEvent(key);
        }

        private String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class NotificationDiscussionInfoEvent {
        NotificationDiscussionInfoEvent(String key) {
            setKey(key);
        }

        public static NotificationDiscussionInfoEvent obtain(String key) {
            return new NotificationDiscussionInfoEvent(key);
        }

        private String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

    }

    public static class NotificationPublicServiceInfoEvent {

        NotificationPublicServiceInfoEvent(String key) {
            setKey(key);
        }

        public static NotificationPublicServiceInfoEvent obtain(String key) {
            return new NotificationPublicServiceInfoEvent(key);
        }

        private String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class ConnectEvent {
        private boolean isConnectSuccess;

        public static ConnectEvent obtain(boolean flag) {
            ConnectEvent event = new ConnectEvent();
            event.setConnectStatus(flag);
            return event;
        }

        public void setConnectStatus(boolean flag) {
            isConnectSuccess = flag;
        }

        public boolean getConnectStatus() {
            return isConnectSuccess;
        }

    }

    public static class GroupUserInfoEvent {
        private GroupUserInfo userInfo;

        public static GroupUserInfoEvent obtain(GroupUserInfo info) {
            GroupUserInfoEvent event = new GroupUserInfoEvent();
            event.userInfo = info;
            return event;
        }

        public GroupUserInfo getUserInfo() {
            return userInfo;
        }
    }

    public static class PlayAudioEvent {
        public int messageId;
        public boolean continuously;

        public static PlayAudioEvent obtain() {
            return new PlayAudioEvent();
        }
    }

    public static class ReadReceiptEvent {
        public ReadReceiptEvent (Message message) {
            this.readReceiptMessage = message;
        }

        public Message getMessage() {
            return readReceiptMessage;
        }

        private Message readReceiptMessage;
    }

    public static class ClearConversationEvent {
        private List<Conversation.ConversationType> typeList = new ArrayList<Conversation.ConversationType>();

        public static ClearConversationEvent obtain(Conversation.ConversationType... conversationTypes) {
            ClearConversationEvent clearConversationEvent = new ClearConversationEvent();
            clearConversationEvent.setTypes(conversationTypes);
            return clearConversationEvent;
        }

        public void setTypes(Conversation.ConversationType[] types) {
            if (types == null || types.length == 0)
                return;
            typeList.clear();
            for (Conversation.ConversationType type : types) {
                typeList.add(type);
            }
        }

        public List<Conversation.ConversationType> getTypes() {
            return typeList;
        }
    }

    public static class MessageRecallEvent {
        private int mMessageId;
        private RecallNotificationMessage mRecallNotificationMessage;
        private boolean mRecallSuccess;

        public int getMessageId() {
            return mMessageId;
        }

        public RecallNotificationMessage getRecallNotificationMessage() {
            return mRecallNotificationMessage;
        }

        public boolean isRecallSuccess() {
            return mRecallSuccess;
        }

        public MessageRecallEvent (int messageId, RecallNotificationMessage recallNotificationMessage, boolean recallSuccess) {
            this.mMessageId = messageId;
            this.mRecallNotificationMessage = recallNotificationMessage;
            this.mRecallSuccess = recallSuccess;
        }
    }

    public static class RemoteMessageRecallEvent {
        private int mMessageId;
        private RecallNotificationMessage mRecallNotificationMessage;
        private boolean mRecallSuccess;

        public int getMessageId() {
            return mMessageId;
        }

        public RecallNotificationMessage getRecallNotificationMessage() {
            return mRecallNotificationMessage;
        }

        public boolean isRecallSuccess() {
            return mRecallSuccess;
        }

        public RemoteMessageRecallEvent (int messageId, RecallNotificationMessage recallNotificationMessage, boolean recallSuccess) {
            this.mMessageId = messageId;
            this.mRecallNotificationMessage = recallNotificationMessage;
            this.mRecallSuccess = recallSuccess;
        }
    }

    public static class ReadReceiptRequestEvent {
        public Conversation.ConversationType getConversationType() {
            return type;
        }

        public String getTargetId() {
            return targetId;
        }

        public String getMessageUId() {
            return messageUId;
        }

        public ReadReceiptRequestEvent (Conversation.ConversationType type, String targetId, String messageUId) {
            this.type = type;
            this.targetId = targetId;
            this.messageUId = messageUId;
        }

        private Conversation.ConversationType type;
        private String targetId;
        private String messageUId;
    }

    public static class ReadReceiptResponseEvent {
        public Conversation.ConversationType getConversationType() {
            return type;
        }

        public String getTargetId() {
            return targetId;
        }

        public String getMessageUId() {
            return messageUId;
        }

        public HashMap<String, Long> getResponseUserIdList() {
            return responseUserIdList;
        }

        public ReadReceiptResponseEvent (Conversation.ConversationType type, String targetId, String messageUId, HashMap<String, Long> responseUserIdList) {
            this.type = type;
            this.targetId = targetId;
            this.messageUId = messageUId;
            this.responseUserIdList = responseUserIdList;
        }

        private Conversation.ConversationType type;
        private String targetId;
        private String messageUId;
        private HashMap<String, Long> responseUserIdList;
    }

    public static class SyncReadStatusEvent {
        public String getTargetId() {
            return targetId;
        }

        public Conversation.ConversationType getConversationType() {
            return type;
        }

        public SyncReadStatusEvent (Conversation.ConversationType type, String targetId) {
            this.type = type;
            this.targetId = targetId;
        }

        private Conversation.ConversationType type;
        private String targetId;
    }

    public static class DraftEvent {
        private String content;
        private Conversation.ConversationType conversationType;
        private String targetId;

        public DraftEvent(Conversation.ConversationType conversationType, String targetId, String content) {
            this.conversationType = conversationType;
            this.targetId = targetId;
            this.content = content;
        }

        public Conversation.ConversationType getConversationType() {
            return conversationType;
        }

        public String getTargetId() {
            return targetId;
        }

        public String getContent() {
            return content;
        }
    }

    public static class CSTerminateEvent{
        private String text;
        private Activity activity;
        public CSTerminateEvent(Activity activity, String content){
            this.activity = activity;
            this.text = content;
        }
        public String getText() {
            return text;
        }
        public Activity getActivity() {
            return activity;
        }
    }
}
