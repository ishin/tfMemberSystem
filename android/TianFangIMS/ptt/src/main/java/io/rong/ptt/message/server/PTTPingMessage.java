package io.rong.ptt.message.server;

import android.os.Parcel;

import io.rong.imlib.MessageTag;

/**
 * Created by jiangecho on 2017/1/6.
 */

@SuppressWarnings("unused")
@MessageTag(value = "RCE:PttPing", flag = MessageTag.STATUS)
public class PTTPingMessage extends PTTStatusMessage {

    public PTTPingMessage(byte[] data) {

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

    public PTTPingMessage() {
    }

    protected PTTPingMessage(Parcel in) {
    }

    public static final Creator<PTTPingMessage> CREATOR = new Creator<PTTPingMessage>() {
        @Override
        public PTTPingMessage createFromParcel(Parcel source) {
            return new PTTPingMessage(source);
        }

        @Override
        public PTTPingMessage[] newArray(int size) {
            return new PTTPingMessage[size];
        }
    };
}
