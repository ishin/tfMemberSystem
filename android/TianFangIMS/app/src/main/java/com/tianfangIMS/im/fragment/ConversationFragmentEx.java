package com.tianfangIMS.im.fragment;

import io.rong.imkit.fragment.ConversationFragment;

/**
 * Created by LianMengYu on 2017/5/30.
 */

public class ConversationFragmentEx extends ConversationFragment {

    @Override
    public boolean onResendItemClick(io.rong.imlib.model.Message message) {
        return false;
    }

    public void onWarningDialog(String msg) {
        String typeStr = getUri().getLastPathSegment();
        if (!typeStr.equals("chatroom")) {
            super.onWarningDialog(msg);
        }
    }

}
