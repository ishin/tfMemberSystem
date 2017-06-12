package io.rong.ptt.net;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.rong.imlib.model.Conversation;

/**
 * Created by jiangecho on 2017/1/4.
 */

public class PttHttpManager {
    private HttpManager httpManager;

    public PttHttpManager() {
        httpManager = new HttpManager();
    }

    public void post(String url, String user, Conversation.ConversationType conversationType, String targetId, final PttResponseCallback callback) {
        post(url, user, conversationType, targetId, 0, callback);
    }

    /**
     *
     */
    public void post(String url, String user, Conversation.ConversationType conversationType, String targetId, int level, final PttResponseCallback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("user", user);
        params.put("conversationType", "" + conversationType.getValue());
        params.put("targetId", targetId);
        params.put("level", "" + level);
        httpManager.post(url, params, new ResponseCallback() {

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject;
                    jsonObject = new JSONObject(response);
                    if (callback != null) {
                        if (jsonObject.optInt("code") == 0) {
                            callback.onSuccess(jsonObject);
                        } else {
                            callback.onFail(jsonObject.optString("message"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFail(e.getMessage());
                    }
                }

            }

            @Override
            public void onFail(int code, String msg) {
                if (callback != null) {
                    callback.onFail(msg);
                }
            }
        });
    }
}
