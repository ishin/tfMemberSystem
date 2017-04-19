package io.rong.ptt;

/**
 * Created by jiangecho on 2016/12/26.
 */

/**
 * 所有ptt相关的变化，都会通知
 * will be called on the ui thread
 */
public interface PTTStateListener extends PTTSessionStateListener {
    void onSessionStart(PTTSession pttSession);

    void onSessionTerminated(PTTSession pttSession);
}
