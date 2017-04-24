package io.rong.ptt.message.server;

import android.os.Parcel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.MessageTag;

/**
 * Created by jiangecho on 2016/12/27.
 */

@MessageTag(value = "RCE:PttPC", flag = MessageTag.STATUS)
public class PTTParticipantChangeMessage extends PTTStatusMessage {
    private List<String> participants;

    @SuppressWarnings("unused")
    public PTTParticipantChangeMessage(byte[] data) {
        try {
            String str = new String(data, "UTF-8");
            JSONObject jsonObject = new JSONObject(str);
            JSONArray jsonArray = jsonObject.optJSONArray("participants");
            if (jsonArray != null) {
                participants = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    participants.add(jsonArray.getString(i));
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] encode() {
        if (participants != null && participants.size() > 0) {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray(participants);
            try {
                jsonObject.put("participants", jsonArray);
                return jsonObject.toString().getBytes("UTF-8");
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

    public List<String> getParticipants() {
        return participants;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.participants);
    }

    protected PTTParticipantChangeMessage(Parcel in) {
        this.participants = in.createStringArrayList();
    }

    public static final Creator<PTTParticipantChangeMessage> CREATOR = new Creator<PTTParticipantChangeMessage>() {
        @Override
        public PTTParticipantChangeMessage createFromParcel(Parcel source) {
            return new PTTParticipantChangeMessage(source);
        }

        @Override
        public PTTParticipantChangeMessage[] newArray(int size) {
            return new PTTParticipantChangeMessage[size];
        }
    };
}
