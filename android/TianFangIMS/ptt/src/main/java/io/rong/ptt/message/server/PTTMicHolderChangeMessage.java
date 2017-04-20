package io.rong.ptt.message.server;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.imlib.MessageTag;

/**
 * Created by jiangecho on 2016/12/27.
 */

@MessageTag(value = "RCE:PttMC", flag = MessageTag.STATUS)
public class PTTMicHolderChangeMessage extends PTTStatusMessage {
    private String holder;

    @SuppressWarnings("unused")
    public PTTMicHolderChangeMessage(byte[] data) {
        try {
            String tmp = new String(data, "UTF-8");
            JSONObject jsonObject = new JSONObject(tmp);
            holder = jsonObject.optString("channelHolder");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] encode() {
        if (holder != null) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("channelHolder", holder);
                return jsonObject.toString().getBytes();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

    public String getHolder() {
        return holder;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.holder);
    }

    protected PTTMicHolderChangeMessage(Parcel in) {
        this.holder = in.readString();
    }

    public static final Creator<PTTMicHolderChangeMessage> CREATOR = new Creator<PTTMicHolderChangeMessage>() {
        @Override
        public PTTMicHolderChangeMessage createFromParcel(Parcel source) {
            return new PTTMicHolderChangeMessage(source);
        }

        @Override
        public PTTMicHolderChangeMessage[] newArray(int size) {
            return new PTTMicHolderChangeMessage[size];
        }
    };
}
