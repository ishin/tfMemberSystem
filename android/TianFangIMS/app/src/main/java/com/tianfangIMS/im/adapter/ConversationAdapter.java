package com.tianfangIMS.im.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import io.rong.imlib.model.Conversation;

/**
 * Created by LianMengYu on 2017/1/16.
 */

public class ConversationAdapter extends ConversationListAdapter{

    public ConversationAdapter(Context context) {
        super(context);
    }

    @Override
    protected View newView(Context context, int position, ViewGroup group) {
        return super.newView(context, position, group);
    }

    @Override
    protected void bindView(View v, int position, UIConversation data) {
        if (data != null) {
            if (data.getConversationType().equals(Conversation.ConversationType.DISCUSSION))
                data.setUnreadType(UIConversation.UnreadRemindType.REMIND_ONLY);
        }
        super.bindView(v, position, data);
    }
}
