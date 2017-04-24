package io.rong.ptt;

import java.util.List;

/**
 * Created by jiangecho on 2016/12/26.
 */

/**
 * 只有和当前pttSession相关的变化会通知到这
 * will be called on the ui thread
 */
public interface PTTSessionStateListener {
    void onParticipantChanged(PTTSession pttSession, List<String> userIds);

    void onMicHolderChanged(PTTSession pttSession, String holderUserId);

    void onNetworkError(String msg);
}
