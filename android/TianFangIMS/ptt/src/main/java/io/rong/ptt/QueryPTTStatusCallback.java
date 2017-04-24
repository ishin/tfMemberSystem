package io.rong.ptt;

/**
 * Created by jiangecho on 2017/1/4.
 */

interface QueryPTTStatusCallback {
    void onSuccess(PTTSession pttSession);

    void onFail();
}
