package io.rong.ptt;

/**
 * Created by jiangecho on 2016/12/26.
 */

/**
 * will be called on the ui thread
 */
public interface RequestToSpeakCallback {
    void onReadyToSpeak(long maxDurationMillis);

    void onFail(String msg);

    /**
     * 请求说话成功后，会有本次允许说话的最长时间，如果时间到之前，用户没有调用<code> PTTKitManager.stopSpeak</code> 结束说话，那本回调将本执行
     */
    void onSpeakTimeOut();
}
