package io.rong.imkit.model;

import android.text.SpannableStringBuilder;

import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imlib.CustomServiceConfig;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.ReadReceiptInfo;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

public class UIMessage {
    private SpannableStringBuilder textMessageContent;
    private UserInfo mUserInfo;
    private int mProgress;
    private boolean evaluated = false;
    private boolean isHistoryMessage = true;
    private Message mMessage;
    private boolean mNickName;
    private boolean isListening;
    public boolean continuePlayAudio;
    private CustomServiceConfig csConfig;

    public boolean isListening() {
        return isListening;
    }

    public void setListening(boolean listening) {
        isListening = listening;
    }

    public boolean isNickName() {
        return mNickName;
    }

    public void setNickName(boolean nickName) {
        this.mNickName = nickName;
    }

    public Message getMessage() {
        return mMessage;
    }

    public void setMessage(Message message) {
        mMessage = message;
    }

    public void setReceivedStatus(Message.ReceivedStatus receivedStatus) {
        mMessage.setReceivedStatus(receivedStatus);
    }

    public void setSentStatus(Message.SentStatus sentStatus) {
        mMessage.setSentStatus(sentStatus);
    }

    public void setReceivedTime(long receivedTime) {
        mMessage.setReceivedTime(receivedTime);
    }

    public void setSentTime(long sentTime) {
        mMessage.setSentTime(sentTime);
    }

    public void setContent(MessageContent content) {
        mMessage.setContent(content);
    }

    public void setExtra(String extra) {
        mMessage.setExtra(extra);
    }

    public void setSenderUserId(String senderUserId) {
        mMessage.setSenderUserId(senderUserId);
    }

    public void setCsConfig(CustomServiceConfig csConfig) {
        this.csConfig = csConfig;
    }


    public String getUId() {
        return mMessage.getUId();
    }

    public Conversation.ConversationType getConversationType() {
        return mMessage.getConversationType();
    }

    public String getTargetId() {
        return mMessage.getTargetId();
    }

    public int getMessageId() {
        return mMessage.getMessageId();
    }

    public Message.MessageDirection getMessageDirection() {
        return mMessage.getMessageDirection();
    }

    public String getSenderUserId() {
        return mMessage.getSenderUserId();
    }

    public Message.ReceivedStatus getReceivedStatus() {
        return mMessage.getReceivedStatus();
    }

    public Message.SentStatus getSentStatus() {
        return mMessage.getSentStatus();
    }

    public long getReceivedTime() {
        return mMessage.getReceivedTime();
    }

    public long getSentTime() {
        return mMessage.getSentTime();
    }

    public String getObjectName() {
        return mMessage.getObjectName();
    }

    public MessageContent getContent() {
        return mMessage.getContent();
    }

    public String getExtra() {
        return mMessage.getExtra();
    }

    public CustomServiceConfig getCsConfig() {
        return csConfig;
    }

    public static UIMessage obtain(Message message) {
        UIMessage uiMessage = new UIMessage();
        uiMessage.mMessage = message;
        uiMessage.continuePlayAudio = false;
        return uiMessage;
    }

    public SpannableStringBuilder getTextMessageContent() {
        if (textMessageContent == null) {
            MessageContent content = mMessage.getContent();
            if (content instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) content;
                if (textMessage.getContent() != null) {
                    SpannableStringBuilder spannable = new SpannableStringBuilder(textMessage.getContent());
                    AndroidEmoji.ensure(spannable);
                    setTextMessageContent(spannable);
                }
            }
        }

        return textMessageContent;
    }

    public ReadReceiptInfo getReadReceiptInfo() {
        return mMessage.getReadReceiptInfo();
    }

    public void setReadReceiptInfo(ReadReceiptInfo info) {
        mMessage.setReadReceiptInfo(info);
    }

    public void setTextMessageContent(SpannableStringBuilder textMessageContent) {
        this.textMessageContent = textMessageContent;
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
    }

    public void setProgress(int progress) {
        mProgress = progress;
    }

    public int getProgress() {
        return mProgress;
    }

    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }

    public boolean getEvaluated() {
        return evaluated;
    }

    public void setIsHistoryMessage(boolean isHistoryMessage) {
        this.isHistoryMessage = isHistoryMessage;
    }

    public boolean getIsHistoryMessage() {
        return isHistoryMessage;
    }
}
