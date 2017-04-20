package io.rong.imkit;

import java.io.InputStream;
import java.util.List;

import io.rong.imlib.AnnotationNotFoundException;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.PublicServiceProfileList;
import io.rong.imlib.model.UserData;

/**
 * IM 客户端核心类。
 * <p/>
 * 所有 IM 相关方法、监听器都由此调用和设置。
 */
public class RongIMClientWrapper {

    public RongIMClientWrapper() {
    }

    /**
     * 连接服务器，在整个应用程序全局，只需要调用一次。
     *
     * @param token    从服务端获取的用户身份令牌（Token）。
     * @param callback 连接回调。
     * @return RongIMClientWrapper 实例。
     */
    @Deprecated
    public RongIMClientWrapper connect(String token, final RongIMClient.ConnectCallback callback) {
        RongIM.connect(token, callback);
        return this;
    }


    /**
     * 设置连接状态变化的监听器。
     *
     * @param listener 连接状态变化的监听器。
     */
    @Deprecated
    public static void setConnectionStatusListener(final RongIMClient.ConnectionStatusListener listener) {
        RongIM.setConnectionStatusListener(listener);
    }

    /**
     * 注册消息类型，如果对消息类型进行扩展，可以忽略此方法。
     *
     * @param type 消息类型，必须要继承自 {@link MessageContent}
     * @throws AnnotationNotFoundException 如果没有找到注解时抛出。
     */
    @Deprecated
    public static void registerMessageType(Class<? extends MessageContent> type) throws AnnotationNotFoundException {
        RongIM.registerMessageType(type);
    }

    /**
     * 获取连接状态。
     *
     * @return 连接状态枚举。
     */
    @Deprecated
    public RongIMClient.ConnectionStatusListener.ConnectionStatus getCurrentConnectionStatus() {
        return RongIM.getInstance().getCurrentConnectionStatus();
    }

    /**
     * 断开连接(默认断开后接收Push消息)。
     */
    @Deprecated
    public void disconnect() {
        RongIM.getInstance().disconnect();
    }

    /**
     * 断开连接(默认断开后接收Push消息)。
     *
     * @param isReceivePush 断开后是否接收push。
     */
    @Deprecated
    public void disconnect(boolean isReceivePush) {
        RongIM.getInstance().disconnect(isReceivePush);
    }

    /**
     * 注销登录(不再接收 Push 消息)。
     */
    @Deprecated
    public void logout() {
        RongIM.getInstance().logout();
    }

    /**
     * 设置接收消息的监听器。
     * <p/>
     * 所有接收到的消息、通知、状态都经由此处设置的监听器处理。包括私聊消息、讨论组消息、群组消息、聊天室消息以及各种状态。
     *
     * @param listener 接收消息的监听器。
     */
    @Deprecated
    public static void setOnReceiveMessageListener(RongIMClient.OnReceiveMessageListener listener) {
        RongIM.setOnReceiveMessageListener(listener);
    }

    @Deprecated
    public void getConversationList(RongIMClient.ResultCallback<List<Conversation>> callback) {
        RongIM.getInstance().getConversationList(callback);
    }

    /**
     * 获取对应对会话列表。
     *
     * @return 会话列表。
     * @see Conversation。
     */
    @Deprecated
    public List<Conversation> getConversationList() {
        return RongIM.getInstance().getConversationList();
    }

    /**
     * 根据会话类型，回调方式获取会话列表。
     *
     * @param callback 获取会话列表的回调。
     * @param types    会话类型。
     */
    @Deprecated
    public void getConversationList(RongIMClient.ResultCallback<List<Conversation>> callback, Conversation.ConversationType... types) {
        RongIM.getInstance().getConversationList(callback, types);
    }


    /**
     * 根据会话类型，获取会话列表。
     *
     * @param types 会话类型。
     * @return 返回会话列表。
     */
    @Deprecated
    public List<Conversation> getConversationList(Conversation.ConversationType... types) {
        return RongIM.getInstance().getConversationList(types);
    }

    /**
     * 根据不同会话类型的目标Id，回调方式获取某一会话信息。
     *
     * @param type     会话类型。
     * @param targetId 目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param callback 获取会话信息的回调。
     */
    @Deprecated
    public void getConversation(Conversation.ConversationType type, String targetId, RongIMClient.ResultCallback<Conversation> callback) {
        RongIM.getInstance().getConversation(type, targetId, callback);
    }

    /**
     * 获取某一会话信息。
     *
     * @param type     会话类型。
     * @param targetId 目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @return 会话信息。
     */
    @Deprecated
    public Conversation getConversation(Conversation.ConversationType type, String targetId) {
        return RongIM.getInstance().getConversation(type, targetId);
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
    @Deprecated
    public void removeConversation(final Conversation.ConversationType type, final String targetId, final RongIMClient.ResultCallback<Boolean> callback) {
        RongIM.getInstance().removeConversation(type, targetId, callback);
    }

    /**
     * 从会话列表中移除某一会话，但是不删除会话内的消息。
     * <p/>
     * 如果此会话中有新的消息，该会话将重新在会话列表中显示，并显示最近的历史消息。
     *
     * @param type     会话类型。
     * @param targetId 目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @return 是否移除成功。
     */
    @Deprecated
    public boolean removeConversation(Conversation.ConversationType type, String targetId) {
        return RongIM.getInstance().removeConversation(type, targetId);
    }

    /**
     * 设置某一会话为置顶或者取消置顶，回调方式获取设置是否成功。
     *
     * @param type     会话类型。
     * @param id       目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param isTop    是否置顶。
     * @param callback 设置置顶或取消置顶是否成功的回调。
     */
    @Deprecated
    public void setConversationToTop(final Conversation.ConversationType type, final String id, final boolean isTop, final RongIMClient.ResultCallback<Boolean> callback) {
        RongIM.getInstance().setConversationToTop(type, id, isTop, callback);
    }

    /**
     * 设置某一会话为置顶或者取消置顶。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param isTop            是否置顶。
     * @return 是否设置成功。
     */
    @Deprecated
    public boolean setConversationToTop(Conversation.ConversationType conversationType, String targetId, boolean isTop) {
        return RongIM.getInstance().setConversationToTop(conversationType, targetId, isTop);
    }

    /**
     * 通过回调方式，获取所有未读消息数。
     *
     * @param callback 消息数的回调。
     */
    @Deprecated
    public void getTotalUnreadCount(final RongIMClient.ResultCallback<Integer> callback) {
        RongIM.getInstance().getTotalUnreadCount(callback);
    }

    /**
     * 获取所有未读消息数。
     *
     * @return 未读消息数。
     */
    @Deprecated
    public int getTotalUnreadCount() {
        return RongIM.getInstance().getTotalUnreadCount();
    }

    /**
     * 根据会话类型的目标 Id,回调方式获取来自某用户（某会话）的未读消息数。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @param callback         未读消息数的回调
     */
    @Deprecated
    public void getUnreadCount(Conversation.ConversationType conversationType, String targetId, RongIMClient.ResultCallback<Integer> callback) {
        RongIM.getInstance().getUnreadCount(conversationType, targetId, callback);
    }

    /**
     * 获取来自某用户（某会话）的未读消息数。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @return 未读消息数。
     */
    @Deprecated
    public int getUnreadCount(Conversation.ConversationType conversationType, String targetId) {
        return RongIM.getInstance().getUnreadCount(conversationType, targetId);
    }


    /**
     * 回调方式获取某会话类型的未读消息数。
     *
     * @param callback          未读消息数的回调。
     * @param conversationTypes 会话类型。
     */
    @Deprecated
    public void getUnreadCount(RongIMClient.ResultCallback<Integer> callback, Conversation.ConversationType... conversationTypes) {
        RongIM.getInstance().getUnreadCount(callback, conversationTypes);
    }

    /**
     * 根据会话类型数组，回调方式获取某会话类型的未读消息数。
     *
     * @param conversationTypes 会话类型。
     * @return 未读消息数的回调。
     */
    @Deprecated
    public int getUnreadCount(Conversation.ConversationType... conversationTypes) {
        return RongIM.getInstance().getUnreadCount(conversationTypes);
    }

    /**
     * 根据会话类型数组，回调方式获取某会话类型的未读消息数。
     *
     * @param conversationTypes 会话类型。
     * @param callback          未读消息数的回调。
     */
    @Deprecated
    public void getUnreadCount(Conversation.ConversationType[] conversationTypes, RongIMClient.ResultCallback<Integer> callback) {
        RongIM.getInstance().getUnreadCount(conversationTypes, callback);
    }

    /**
     * 获取最新消息记录。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。
     * @param count            要获取的消息数量。
     * @return 最新消息记录，按照时间顺序从新到旧排列。
     */
    @Deprecated
    public List<Message> getLatestMessages(Conversation.ConversationType conversationType, String targetId, int count) {
        return RongIM.getInstance().getLatestMessages(conversationType, targetId, count);
    }

    /**
     * 根据会话类型的目标 Id，回调方式获取最新的 N 条消息记录。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param count            要获取的消息数量。
     * @param callback         获取最新消息记录的回调，按照时间顺序从新到旧排列。
     */
    @Deprecated
    public void getLatestMessages(Conversation.ConversationType conversationType, String targetId, int count, RongIMClient.ResultCallback<List<Message>> callback) {
        RongIM.getInstance().getLatestMessages(conversationType, targetId, count, callback);
    }

    /**
     * 获取历史消息记录。
     *
     * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @param oldestMessageId  最后一条消息的 Id，获取此消息之前的 count 条消息，没有消息第一次调用应设置为:-1。
     * @param count            要获取的消息数量。
     * @return 历史消息记录，按照时间顺序从新到旧排列。
     */
    @Deprecated
    public List<Message> getHistoryMessages(Conversation.ConversationType conversationType, String targetId, int oldestMessageId, int count) {
        return RongIM.getInstance().getHistoryMessages(conversationType, targetId, oldestMessageId, count);
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
     */
    @Deprecated
    public List<Message> getHistoryMessages(Conversation.ConversationType conversationType, String targetId, String objectName, int oldestMessageId, int count) {
        return RongIM.getInstance().getHistoryMessages(conversationType, targetId, objectName, oldestMessageId, count);
    }

    /**
     * 根据会话类型的目标 Id，回调方式获取某消息类型标识的N条历史消息记录。
     *
     * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param objectName       消息类型标识。
     * @param oldestMessageId  最后一条消息的 Id，获取此消息之前的 count 条消息,没有消息第一次调用应设置为:-1。
     * @param count            要获取的消息数量。
     * @param callback         获取历史消息记录的回调，按照时间顺序从新到旧排列。
     */
    @Deprecated
    public void getHistoryMessages(Conversation.ConversationType conversationType, String targetId, String objectName, int oldestMessageId, int count, RongIMClient.ResultCallback<List<Message>> callback) {
        RongIM.getInstance().getHistoryMessages(conversationType, targetId, objectName, oldestMessageId, count, callback);
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
    @Deprecated
    public void getHistoryMessages(Conversation.ConversationType conversationType, String targetId, int oldestMessageId, int count, RongIMClient.ResultCallback<List<Message>> callback) {
        RongIM.getInstance().getHistoryMessages(conversationType, targetId, oldestMessageId, count, callback);
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
    @Deprecated
    public void getRemoteHistoryMessages(Conversation.ConversationType conversationType, String targetId, long dataTime, int count, RongIMClient.ResultCallback<List<Message>> callback) {
        RongIM.getInstance().getRemoteHistoryMessages(conversationType, targetId, dataTime, count, callback);
    }

    /**
     * 删除指定的一条或者一组消息。
     *
     * @param messageIds 要删除的消息 Id 数组。
     * @return 是否删除成功。
     */
    @Deprecated
    public boolean deleteMessages(final int[] messageIds) {
        return RongIM.getInstance().deleteMessages(messageIds);
    }

    /**
     * 删除指定的一条或者一组消息，回调方式获取是否删除成功。
     *
     * @param messageIds 要删除的消息 Id 数组。
     * @param callback   是否删除成功的回调。
     */
    @Deprecated
    public void deleteMessages(final int[] messageIds, final RongIMClient.ResultCallback<Boolean> callback) {
        RongIM.getInstance().deleteMessages(messageIds, callback);
    }

    /**
     * 清空某一会话的所有聊天消息记录。
     *
     * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @return 是否清空成功。
     */
    @Deprecated
    public boolean clearMessages(Conversation.ConversationType conversationType, String targetId) {
        return RongIM.getInstance().clearMessages(conversationType, targetId);
    }

    /**
     * 根据会话类型，清空某一会话的所有聊天消息记录,回调方式获取清空是否成功。
     *
     * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @param callback         清空是否成功的回调。
     */
    @Deprecated
    public void clearMessages(final Conversation.ConversationType conversationType, final String targetId, final RongIMClient.ResultCallback<Boolean> callback) {
        RongIM.getInstance().clearMessages(conversationType, targetId, callback);
    }

    /**
     * 清除消息未读状态。
     *
     * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @return 是否清空成功。
     */
    @Deprecated
    public boolean clearMessagesUnreadStatus(Conversation.ConversationType conversationType, String targetId) {
        return RongIM.getInstance().clearMessagesUnreadStatus(conversationType, targetId);
    }

    /**
     * 根据会话类型，清除目标 Id 的消息未读状态，回调方式获取清除是否成功。
     *
     * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @param callback         清除是否成功的回调。
     */
    @Deprecated
    public void clearMessagesUnreadStatus(final Conversation.ConversationType conversationType, final String targetId, final RongIMClient.ResultCallback<Boolean> callback) {
        RongIM.getInstance().clearMessagesUnreadStatus(conversationType, targetId, callback);
    }

    /**
     * 设置消息的附加信息，此信息只保存在本地。
     *
     * @param messageId 消息 Id。
     * @param value     消息附加信息，最大 1024 字节。
     * @return 是否设置成功。
     */
    @Deprecated
    public boolean setMessageExtra(int messageId, String value) {
        return RongIM.getInstance().setMessageExtra(messageId, value);
    }

    /**
     * 设置消息的附加信息，此信息只保存在本地，回调方式获取设置是否成功。
     *
     * @param messageId 消息 Id。
     * @param value     消息附加信息，最大 1024 字节。
     * @param callback  是否设置成功的回调。
     */
    @Deprecated
    public void setMessageExtra(int messageId, String value, RongIMClient.ResultCallback<Boolean> callback) {
        RongIM.getInstance().setMessageExtra(messageId, value, callback);
    }

    /**
     * 设置接收到的消息状态。
     *
     * @param messageId      消息 Id。
     * @param receivedStatus 接收到的消息状态。
     * @return 是否设置成功。
     */
    @Deprecated
    public boolean setMessageReceivedStatus(int messageId, Message.ReceivedStatus receivedStatus) {
        return RongIM.getInstance().setMessageReceivedStatus(messageId, receivedStatus);
    }

    /**
     * 根据消息 Id，设置接收到的消息状态，回调方式获取设置是否成功。
     *
     * @param messageId      消息 Id。
     * @param receivedStatus 接收到的消息状态。
     * @param callback       是否设置成功的回调。
     */
    @Deprecated
    public void setMessageReceivedStatus(int messageId, Message.ReceivedStatus receivedStatus, RongIMClient.ResultCallback<Boolean> callback) {
        RongIM.getInstance().setMessageReceivedStatus(messageId, receivedStatus, callback);
    }

    /**
     * 设置发送的消息状态。
     *
     * @param messageId  消息 Id。
     * @param sentStatus 发送的消息状态。
     * @return 是否设置成功。
     */
    @Deprecated
    public boolean setMessageSentStatus(int messageId, Message.SentStatus sentStatus) {
        return RongIM.getInstance().setMessageSentStatus(messageId, sentStatus);
    }

    /**
     * 根据消息 Id，设置发送的消息状态，回调方式获取设置是否成功。
     *
     * @param messageId  消息 Id。
     * @param sentStatus 发送的消息状态。
     * @param callback   是否设置成功的回调。
     */
    @Deprecated
    public void setMessageSentStatus(final int messageId, final Message.SentStatus sentStatus, final RongIMClient.ResultCallback<Boolean> callback) {
        RongIM.getInstance().setMessageSentStatus(messageId, sentStatus, callback);
    }

    /**
     * 获取某一会话的文字消息草稿。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @return 草稿的文字内容。
     */
    @Deprecated
    public String getTextMessageDraft(Conversation.ConversationType conversationType, String targetId) {
        return RongIM.getInstance().getTextMessageDraft(conversationType, targetId);
    }

    /**
     * 保存文字消息草稿。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param content          草稿的文字内容。
     * @return 是否保存成功。
     */
    @Deprecated
    public boolean saveTextMessageDraft(Conversation.ConversationType conversationType, String targetId, String content) {
        return RongIM.getInstance().saveTextMessageDraft(conversationType, targetId, content);
    }

    /**
     * 清除某一会话的文字消息草稿。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @return 是否清除成功。
     */
    @Deprecated
    public boolean clearTextMessageDraft(Conversation.ConversationType conversationType, String targetId) {
        return RongIM.getInstance().clearTextMessageDraft(conversationType, targetId);
    }

    /**
     * 根据会话类型，获取某一会话的文字消息草稿。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param callback         获取草稿文字内容的回调。
     */
    @Deprecated
    public void getTextMessageDraft(Conversation.ConversationType conversationType, String targetId, RongIMClient.ResultCallback<String> callback) {
        RongIM.getInstance().getTextMessageDraft(conversationType, targetId, callback);
    }

    /**
     * 保存文字消息草稿，回调方式获取保存是否成功。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param content          草稿的文字内容。
     * @param callback         是否保存成功的回调。
     */
    @Deprecated
    public void saveTextMessageDraft(Conversation.ConversationType conversationType, String targetId, String content, RongIMClient.ResultCallback<Boolean> callback) {
        RongIM.getInstance().saveTextMessageDraft(conversationType, targetId, content, callback);
    }

    /**
     * 清除某一会话的文字消息草稿，回调方式获取清除是否成功。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param callback         是否清除成功的回调。
     */
    @Deprecated
    public void clearTextMessageDraft(Conversation.ConversationType conversationType, String targetId, RongIMClient.ResultCallback<Boolean> callback) {
        RongIM.getInstance().clearTextMessageDraft(conversationType, targetId, callback);
    }

    /**
     * 获取讨论组信息和设置。
     *
     * @param discussionId 讨论组 Id。
     * @param callback     获取讨论组的回调。
     */
    @Deprecated
    public void getDiscussion(String discussionId, RongIMClient.ResultCallback<Discussion> callback) {
        RongIM.getInstance().getDiscussion(discussionId, callback);
    }

    /**
     * 设置讨论组名称。
     *
     * @param discussionId 讨论组 Id。
     * @param name         讨论组名称。
     * @param callback     设置讨论组的回调。
     */
    @Deprecated
    public void setDiscussionName(final String discussionId, final String name, final RongIMClient.OperationCallback callback) {
        RongIM.getInstance().setDiscussionName(discussionId, name, callback);
    }

    /**
     * 创建讨论组。
     *
     * @param name       讨论组名称，如：当前所有成员的名字的组合。
     * @param userIdList 讨论组成员 Id 列表。
     * @param callback   创建讨论组成功后的回调。
     */
    @Deprecated
    public void createDiscussion(final String name, final List<String> userIdList, final RongIMClient.CreateDiscussionCallback callback) {
        RongIM.getInstance().createDiscussion(name, userIdList, callback);
    }

    /**
     * 添加一名或者一组用户加入讨论组。
     *
     * @param discussionId 讨论组 Id。
     * @param userIdList   邀请的用户 Id 列表。
     * @param callback     执行操作的回调。
     */
    @Deprecated
    public void addMemberToDiscussion(final String discussionId, final List<String> userIdList, final RongIMClient.OperationCallback callback) {
        RongIM.getInstance().addMemberToDiscussion(discussionId, userIdList, callback);
    }

    /**
     * 供创建者将某用户移出讨论组。
     * <p/>
     * 移出自己或者调用者非讨论组创建者将产生
     * 错误。
     *
     * @param discussionId 讨论组 Id。
     * @param userId       用户 Id。
     * @param callback     执行操作的回调。
     */
    @Deprecated
    public void removeMemberFromDiscussion(final String discussionId, final String userId, final RongIMClient.OperationCallback callback) {
        RongIM.getInstance().removeMemberFromDiscussion(discussionId, userId, callback);
    }

    /**
     * 退出当前用户所在的某讨论组。
     *
     * @param discussionId 讨论组 Id。
     * @param callback     执行操作的回调。
     */
    @Deprecated
    public void quitDiscussion(final String discussionId, final RongIMClient.OperationCallback callback) {
        RongIM.getInstance().quitDiscussion(discussionId, callback);
    }

    /**
     * 模拟消息，向本地目标 Id 中插入一条消息
     *
     * @param type         会话类型。
     * @param targetId     目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param senderUserId 发送用户 Id。
     * @param content      消息内容。
     * @param callback     获得消息发送实体的回调。
     */
    @Deprecated
    public void insertMessage(Conversation.ConversationType type, String targetId, String senderUserId, MessageContent content, final RongIMClient.ResultCallback<Message> callback) {
        RongIM.getInstance().insertMessage(type, targetId, senderUserId, content, callback);
    }

    /**
     * 模拟消息。
     *
     * @param type         会话类型。
     * @param targetId     目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param senderUserId 发送用户 Id。
     * @param content      消息内容。
     * @return
     */
    @Deprecated
    public Message insertMessage(Conversation.ConversationType type, String targetId, String senderUserId, MessageContent content) {
        return RongIM.getInstance().insertMessage(type, targetId, senderUserId, content);
    }

    /**
     * 发送消息。
     *
     * @param type        会话类型。
     * @param targetId    目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param content     消息内容。
     * @param pushContent push 内容，为空时不 push 信息。
     * @param pushData    push 附加信息，开发者根据自已的需要设置 pushData。
     * @param callback    发送消息的回调。
     * @return
     */
    @Deprecated
    public Message sendMessage(Conversation.ConversationType type, String targetId, MessageContent content, String pushContent, String pushData, final RongIMClient.SendMessageCallback callback) {
        return RongIM.getInstance().sendMessage(type, targetId, content, pushContent, pushData, callback);
    }


    /**
     * 根据会话类型，发送消息。
     *
     * @param type           会话类型。
     * @param targetId       目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param content        消息内容。
     * @param pushContent    push 内容，为空时不 push 信息。
     * @param pushData       push 附加信息，开发者根据自已的需要设置 pushData。
     * @param callback       发送消息的回调。
     * @param resultCallback 获取发送消息实体的回调。
     */
    @Deprecated
    public void sendMessage(Conversation.ConversationType type, String targetId, MessageContent
                            content, String pushContent, final String pushData, final RongIMClient.SendMessageCallback callback,
                            final RongIMClient.ResultCallback<Message> resultCallback) {
        RongIM.getInstance().sendMessage(type, targetId, content, pushContent, pushData, callback, resultCallback);
    }

    /**
     * 发送消息。
     *
     * @param message        发送消息的实体。
     * @param pushContent    push 内容，为空时不 push 信息。
     * @param pushData       push 附加信息，开发者根据自已的需要设置 pushData。
     * @param callback       发送消息的回调。
     * @param resultCallback 获取发送消息实体的回调。
     */
    @Deprecated
    public void sendMessage(Message message, String pushContent, final String pushData,
                            final RongIMClient.SendMessageCallback callback, final RongIMClient.ResultCallback<Message> resultCallback) {
        RongIM.getInstance().sendMessage(message, pushContent, pushData, callback, resultCallback);
    }

    /**
     * 发送消息，返回发送的消息实体。
     *
     * @param message     发送消息的实体。
     * @param pushContent push 内容，为空时不 push 信息。
     * @param pushData    push 附加信息，开发者根据自已的需要设置 pushData。
     * @param callback    发送消息的回调。
     * @return 发送的消息实体。
     */
    @Deprecated
    public Message sendMessage(Message message, String pushContent, final String pushData,
                               final RongIMClient.SendMessageCallback callback) {
        return RongIM.getInstance().sendMessage(message, pushContent, pushData, callback);
    }

    /**
     * 发送图片消息。
     *
     * @param type        会话类型。
     * @param targetId    目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param content     消息内容。
     * @param pushContent push 内容，为空时不 push 信息。
     * @param pushData    push 附加信息，开发者根据自已的需要设置 pushData。
     * @param callback    发送消息的回调。
     */
    @Deprecated
    public void sendImageMessage(Conversation.ConversationType type, String
                                 targetId, MessageContent content, String pushContent, String pushData,
                                 final RongIMClient.SendImageMessageCallback callback) {
        RongIM.getInstance().sendImageMessage(type, targetId, content, pushContent, pushData, callback);
    }

    /**
     * 发送图片消息。
     *
     * @param message     发送消息的实体。
     * @param pushContent push 内容，为空时不 push 信息。
     * @param pushData    push 附加信息，开发者根据自已的需要设置 pushData。
     * @param callback    发送消息的回调。
     */
    @Deprecated
    public void sendImageMessage(Message message, String pushContent,
                                 final String pushData, final RongIMClient.SendImageMessageCallback callback) {
        RongIM.getInstance().sendImageMessage(message, pushContent, pushData, callback);
    }

    /**
     * 发送图片消息，可以使用该方法将图片上传到自己的服务器发送，同时更新图片状态。
     * 该方法适用于使用者自己上传图片，并通过 listener 将上传进度更新在UI上显示。
     *
     * @param message     发送消息的实体。
     * @param pushContent push 内容，为空时不 push 信息。
     * @param pushData    push 附加信息，开发者根据自已的需要设置 pushData。
     * @param callback    发送消息的回调，该回调携带 listener 对象，使用者可以调用其方法，更新图片上传进度。
     */
    @Deprecated
    public void sendImageMessage(Message message, String pushContent,
                                 final String pushData,
                                 final RongIMClient.SendImageMessageWithUploadListenerCallback callback) {
        RongIM.getInstance().sendImageMessage(message, pushContent, pushData, callback);
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
    @Deprecated
    public void downloadMedia(Conversation.ConversationType conversationType, String targetId, RongIMClient.MediaType mediaType, String imageUrl, final RongIMClient.DownloadMediaCallback callback) {
        RongIM.getInstance().downloadMedia(conversationType, targetId, mediaType, imageUrl, callback);
    }

    /**
     * 下载文件。
     *
     * @param imageUrl 文件的 URL 地址。
     * @param callback 下载文件的回调。
     */
    @Deprecated
    public void downloadMedia(String imageUrl, final RongIMClient.DownloadMediaCallback callback) {
        RongIM.getInstance().downloadMedia(imageUrl, callback);
    }

    /**
     * 获取会话消息提醒状态。
     *
     * @param conversationType 会话类型。
     * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @param callback         获取状态的回调。
     */
    @Deprecated
    public void getConversationNotificationStatus(final Conversation.ConversationType conversationType, final String targetId, final RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus> callback) {
        RongIM.getInstance().getConversationNotificationStatus(conversationType, targetId, callback);
    }

    /**
     * 设置会话消息提醒状态。
     *
     * @param conversationType   会话类型。
     * @param targetId           目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
     * @param notificationStatus 是否屏蔽。
     * @param callback           设置状态的回调。
     */
    @Deprecated
    public void setConversationNotificationStatus(final Conversation.ConversationType conversationType, final String targetId, final Conversation.ConversationNotificationStatus notificationStatus, final RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus> callback) {
        RongIM.getInstance().setConversationNotificationStatus(conversationType, targetId, notificationStatus, callback);
    }

    /**
     * 设置讨论组成员邀请权限。
     *
     * @param discussionId 讨论组 id。
     * @param status       邀请状态，默认为开放。
     * @param callback     设置权限的回调。
     */
    @Deprecated
    public void setDiscussionInviteStatus(final String discussionId, final RongIMClient.DiscussionInviteStatus status, final RongIMClient.OperationCallback callback) {
        RongIM.getInstance().setDiscussionInviteStatus(discussionId, status, callback);
    }

    /**
     * 同步当前用户的群组信息。
     * Warning: 已废弃，请勿使用。
     * 此方法已废弃，建议您通过您的App Server进行群组操作。 群组操作的流程，可以参考：http://support.rongcloud.cn/kb/MzY5
     *
     * @param groups   需要同步的群组实体。
     * @param callback 同步状态的回调。
     */
    @Deprecated
    public void syncGroup(final List<Group> groups, final RongIMClient.OperationCallback callback) {
        RongIM.getInstance().syncGroup(groups, callback);
    }

    /**
     * 加入群组。
     * Warning: 已废弃，请勿使用。
     * 此方法已废弃，建议您通过您的App Server进行群组操作。 群组操作的流程，可以参考：http://support.rongcloud.cn/kb/MzY5
     *
     * @param groupId   群组 Id。
     * @param groupName 群组名称。
     * @param callback  加入群组状态的回调。
     */
    @Deprecated
    public void joinGroup(final String groupId, final String groupName, final RongIMClient.OperationCallback callback) {
        RongIM.getInstance().joinGroup(groupId, groupName, callback);
    }

    /**
     * 退出群组。
     * Warning: 已废弃，请勿使用。
     * 此方法已废弃，建议您通过您的App Server进行群组操作。 群组操作的流程，可以参考：http://support.rongcloud.cn/kb/MzY5
     *
     * @param groupId  群组 Id。
     * @param callback 退出群组状态的回调。
     */
    @Deprecated
    public void quitGroup(final String groupId, final RongIMClient.OperationCallback callback) {
        RongIM.getInstance().quitGroup(groupId, callback);
    }

    /**
     * 获取当前连接用户的信息。
     *
     * @return 当前连接用户的信息。
     */
    @Deprecated
    public String getCurrentUserId() {
        return RongIM.getInstance().getCurrentUserId();
    }

    /**
     * 获取本地时间与服务器时间的差值。
     *
     * @return 本地时间与服务器时间的差值。
     */
    @Deprecated
    public long getDeltaTime() {
        return RongIM.getInstance().getDeltaTime();
    }

    /**
     * 加入聊天室。
     *
     * @param chatroomId      聊天室 Id。
     * @param defMessageCount 进入聊天室拉取消息数目，为 -1 时不拉取任何消息，默认拉取 10 条消息。
     * @param callback        状态回调。
     */
    @Deprecated
    public void joinChatRoom(final String chatroomId, final int defMessageCount, final RongIMClient.OperationCallback callback) {
        RongIM.getInstance().joinChatRoom(chatroomId, defMessageCount, callback);
    }

    /**
     * 加入已存在的聊天室。
     *
     * @param chatroomId      聊天室 Id。
     * @param defMessageCount 进入聊天室拉取消息数目，为 -1 时不拉取任何消息，默认拉取 10 条消息。
     * @param callback        状态回调。
     */
    @Deprecated
    public void joinExistChatRoom(final String chatroomId, final int defMessageCount, final RongIMClient.OperationCallback callback) {
        RongIM.getInstance().joinExistChatRoom(chatroomId, defMessageCount, callback);
    }

    /**
     * 退出聊天室。
     *
     * @param chatroomId 聊天室 Id。
     * @param callback   状态回调。
     */
    @Deprecated
    public void quitChatRoom(final String chatroomId, final RongIMClient.OperationCallback callback) {
        RongIM.getInstance().quitChatRoom(chatroomId, callback);
    }

    /**
     * 清空所有会话及会话消息，回调方式通知是否清空成功。
     *
     * @param callback          是否清空成功的回调。
     * @param conversationTypes 会话类型。
     */
    @Deprecated
    public void clearConversations(RongIMClient.ResultCallback callback, Conversation.ConversationType... conversationTypes) {
        RongIM.getInstance().clearConversations(callback, conversationTypes);
    }

    /**
     * 清空所有会话及会话消息。
     *
     * @param conversationTypes 会话类型。
     * @return 是否清空成功。
     */
    @Deprecated
    public boolean clearConversations(Conversation.ConversationType... conversationTypes) {
        return RongIM.getInstance().clearConversations(conversationTypes);
    }

    /**
     * 将某个用户加到黑名单中。
     *
     * @param userId   用户 Id。
     * @param callback 加到黑名单回调。
     */
    @Deprecated
    public void addToBlacklist(final String userId, final RongIMClient.OperationCallback callback) {
        RongIM.getInstance().addToBlacklist(userId, callback);
    }

    /**
     * 将个某用户从黑名单中移出。
     *
     * @param userId   用户 Id。
     * @param callback 移除黑名单回调。
     */
    @Deprecated
    public void removeFromBlacklist(final String userId, final RongIMClient.OperationCallback callback) {
        RongIM.getInstance().removeFromBlacklist(userId, callback);
    }

    /**
     * 获取某用户是否在黑名单中。
     *
     * @param userId   用户 Id。
     * @param callback 获取用户是否在黑名单回调。
     */
    @Deprecated
    public void getBlacklistStatus(String userId, RongIMClient.ResultCallback<RongIMClient.BlacklistStatus> callback) {
        RongIM.getInstance().getBlacklistStatus(userId, callback);
    }

    /**
     * 获取当前用户的黑名单列表。
     *
     * @param callback 获取黑名单回调。
     */
    @Deprecated
    public void getBlacklist(RongIMClient.GetBlacklistCallback callback) {
        RongIM.getInstance().getBlacklist(callback);
    }

    /**
     * 设置会话通知免打扰时间。
     *
     * @param startTime   起始时间 格式 HH:MM:SS。
     * @param spanMinutes 间隔分钟数 0 < spanMinutes < 1440。
     * @param callback    设置会话通知免打扰时间回调。
     */
    @Deprecated
    public void setNotificationQuietHours(final String startTime, final int spanMinutes, final RongIMClient.OperationCallback callback) {
        RongIM.getInstance().setNotificationQuietHours(startTime, spanMinutes, callback);
    }

    /**
     * 移除会话通知免打扰时间。
     *
     * @param callback 移除会话通知免打扰时间回调。
     */
    @Deprecated
    public void removeNotificationQuietHours(final RongIMClient.OperationCallback callback) {
        RongIM.getInstance().removeNotificationQuietHours(callback);
    }

    /**
     * 获取会话通知免打扰时间。
     *
     * @param callback 获取会话通知免打扰时间回调。
     */
    @Deprecated
    public void getNotificationQuietHours(final RongIMClient.GetNotificationQuietHoursCallback callback) {
        RongIM.getInstance().getNotificationQuietHours(callback);
    }

    /**
     * 获取公众服务信息。
     *
     * @param publicServiceType 会话类型，APP_PUBLIC_SERVICE 或者 PUBLIC_SERVICE。
     * @param publicServiceId   公众服务 Id。
     * @param callback          获取公众号信息回调。
     */
    @Deprecated
    public void getPublicServiceProfile(Conversation.PublicServiceType publicServiceType, String publicServiceId, RongIMClient.ResultCallback<PublicServiceProfile> callback) {
        RongIM.getInstance().getPublicServiceProfile(publicServiceType, publicServiceId, callback);
    }

    /**
     * 搜索公众服务。
     *
     * @param searchType 搜索类型枚举。
     * @param keywords   搜索关键字。
     * @param callback   搜索结果回调。
     */
    @Deprecated
    public void searchPublicService(RongIMClient.SearchType searchType, String keywords, RongIMClient.ResultCallback<PublicServiceProfileList> callback) {
        RongIM.getInstance().searchPublicService(searchType, keywords, callback);
    }

    /**
     * 按公众服务类型搜索公众服务。
     *
     * @param publicServiceType 公众服务类型。
     * @param searchType        搜索类型枚举。
     * @param keywords          搜索关键字。
     * @param callback          搜索结果回调。
     */
    @Deprecated
    public void searchPublicServiceByType(Conversation.PublicServiceType publicServiceType, RongIMClient.SearchType searchType, final String keywords, final RongIMClient.ResultCallback<PublicServiceProfileList> callback) {
        RongIM.getInstance().searchPublicServiceByType(publicServiceType, searchType, keywords, callback);
    }

    /**
     * 订阅公众号。
     *
     * @param publicServiceId   公共服务 Id。
     * @param publicServiceType 公众服务类型枚举。
     * @param callback          订阅公众号回调。
     */
    @Deprecated
    public void subscribePublicService(Conversation.PublicServiceType publicServiceType, String publicServiceId, RongIMClient.OperationCallback callback) {
        RongIM.getInstance().subscribePublicService(publicServiceType, publicServiceId, callback);
    }

    /**
     * 取消订阅公众号。
     *
     * @param publicServiceId   公共服务 Id。
     * @param publicServiceType 公众服务类型枚举。
     * @param callback          取消订阅公众号回调。
     */
    @Deprecated
    public void unsubscribePublicService(Conversation.PublicServiceType publicServiceType, String publicServiceId, RongIMClient.OperationCallback callback) {
        RongIM.getInstance().unsubscribePublicService(publicServiceType, publicServiceId, callback);
    }

    /**
     * 获取己关注公共账号列表。
     *
     * @param callback 获取己关注公共账号列表回调。
     */
    @Deprecated
    public void getPublicServiceList(RongIMClient.ResultCallback<PublicServiceProfileList> callback) {
        RongIM.getInstance().getPublicServiceList(callback);
    }


    /**
     * 设置用户信息。
     *
     * @param userData 用户信息。
     * @param callback 设置用户信息回调。
     */
    @Deprecated
    public void syncUserData(final UserData userData, final RongIMClient.OperationCallback callback) {
        RongIM.getInstance().syncUserData(userData, callback);
    }
}
