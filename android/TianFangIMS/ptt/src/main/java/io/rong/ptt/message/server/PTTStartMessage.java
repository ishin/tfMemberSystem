package io.rong.ptt.message.server;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * Created by jiangecho on 2016/12/28.
 */

// this message must be sent by the app server
@MessageTag(value = "RCE:PttBegin", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class PTTStartMessage extends MessageContent {
    private String initiator;

    @SuppressWarnings("unused")
    public PTTStartMessage(byte[] data) {
        try {
            String tmp = new String(data, "UTF-8");
            JSONObject jsonObject = new JSONObject(tmp);
            initiator = jsonObject.optString("initiator");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    PTTStartMessage() {
    }

    public String getInitiator() {
        return initiator;
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("initiator", initiator);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            return jsonObject.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.initiator);
    }

    protected PTTStartMessage(Parcel in) {
        this.initiator = in.readString();
    }

    public static final Creator<PTTStartMessage> CREATOR = new Creator<PTTStartMessage>() {
        @Override
        public PTTStartMessage createFromParcel(Parcel source) {
            return new PTTStartMessage(source);
        }

        @Override
        public PTTStartMessage[] newArray(int size) {
            return new PTTStartMessage[size];
        }
    };

    // this method is only for client test
    public static PTTStartMessage obtain(String initiator) {
        PTTStartMessage pttStartMessage = new PTTStartMessage();
        pttStartMessage.initiator = initiator;
        return pttStartMessage;
    }
}
