package io.rong.ptt;

import java.util.List;

import io.rong.imlib.model.Conversation;

/**
 * Created by jiangecho on 2016/12/29.
 */

public class PTTSession {
    String initiator;
    String targetId;
    String micHolder;
    private Conversation.ConversationType conversationType;
    private List<String> participantIds;

    public PTTSession(Conversation.ConversationType conversationType, String targetId) {
        this.targetId = targetId;
        this.conversationType = conversationType;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public Conversation.ConversationType getConversationType() {
        return conversationType;
    }

    public void setConversationType(Conversation.ConversationType conversationType) {
        this.conversationType = conversationType;
    }

    public List<String> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<String> participantIds) {
        this.participantIds = participantIds;
    }

    public String getMicHolder() {
        return micHolder;
    }

    public void setMicHolder(String micHolder) {
        this.micHolder = micHolder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PTTSession that = (PTTSession) o;

        if (!targetId.equals(that.targetId)) return false;
        return conversationType == that.conversationType;

    }

    @Override
    public int hashCode() {
        int result = targetId.hashCode();
        result = 31 * result + conversationType.hashCode();
        return result;
    }

    public String key() {
        return conversationType.getName() + ":" + targetId;
    }

}
