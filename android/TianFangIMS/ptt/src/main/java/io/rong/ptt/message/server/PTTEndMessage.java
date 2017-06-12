package io.rong.ptt.message.server;

import android.os.Parcel;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.ptt.message.InfoNotificationMsgInterface;

/**
 * Created by jiangecho on 2016/12/29.
 */

// this message must be sent by the app server
@SuppressWarnings("unused")
@MessageTag(value = "RCE:PttEnd", flag = MessageTag.ISPERSISTED)
public class PTTEndMessage extends MessageContent implements InfoNotificationMsgInterface{

    public PTTEndMessage(byte[] data) {
    }

    public PTTEndMessage() {
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    protected PTTEndMessage(Parcel in) {
    }

    public static final Creator<PTTEndMessage> CREATOR = new Creator<PTTEndMessage>() {
        @Override
        public PTTEndMessage createFromParcel(Parcel source) {
            return new PTTEndMessage(source);
        }

        @Override
        public PTTEndMessage[] newArray(int size) {
            return new PTTEndMessage[size];
        }
    };

    @Override
    public String getMessage() {
        return "语音对讲结束";
    }
}
