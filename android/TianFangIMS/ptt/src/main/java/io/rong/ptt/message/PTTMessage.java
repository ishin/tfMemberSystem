package io.rong.ptt.message;

import android.os.Parcel;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import io.rong.imlib.MessageTag;
import io.rong.message.StatusMessage;

/**
 * Created by jiangecho on 2016/12/26.
 */

@MessageTag(value = "RCE:PttMsg", flag = MessageTag.STATUS)
public class PTTMessage extends StatusMessage {

    // in base64 format
    private byte[] content;

    /**
     * called by reflection
     *
     * @param data data in base64 format
     */
    @SuppressWarnings("unused")
    public PTTMessage(byte[] data) {
        //this.content = data;
        try {
            String str = new String(data, "UTF-8");
            JSONObject jsonObject = new JSONObject(str);
            String base64Content = jsonObject.getString("content");
            content = base64Content.getBytes();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param data raw data
     * @param len
     */
    public PTTMessage(byte[] data, int len) {
        this.content = Base64.encode(data, 0, len, Base64.NO_WRAP);
    }

    public byte[] getRawData() {
        return Base64.decode(content, Base64.NO_WRAP);
    }

    @Override
    public byte[] encode() {
        if (content != null && content.length > 0) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("content", new String(content, "UTF-8"));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return jsonObject.toString().getBytes();
        }
        return new byte[0];
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.content);
    }

    protected PTTMessage(Parcel in) {
        this.content = in.createByteArray();
    }

    public static final Creator<PTTMessage> CREATOR = new Creator<PTTMessage>() {
        @Override
        public PTTMessage createFromParcel(Parcel source) {
            return new PTTMessage(source);
        }

        @Override
        public PTTMessage[] newArray(int size) {
            return new PTTMessage[size];
        }
    };

    // // TODO: 2016/12/26 object pool?
    public static PTTMessage obtain(byte[] data, int len) {
        return new PTTMessage(data, len);
    }

    public static PTTMessage obtain(short[] data, int len) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(len * 2);
        for (short s : data) {
            byteBuffer.putShort(s);
        }
        return new PTTMessage(byteBuffer.array(), len);
    }
}
