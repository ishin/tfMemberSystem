package io.rong.ptt.net;

/**
 * Created by jiangecho on 2017/1/4.
 */

interface ResponseCallback {
    void onSuccess(String response);

    void onFail(int code, String msg);
}
