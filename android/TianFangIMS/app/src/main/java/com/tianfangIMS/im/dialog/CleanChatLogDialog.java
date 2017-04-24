package com.tianfangIMS.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.tianfangIMS.im.R;
import com.tianfangIMS.im.utils.NToast;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by LianMengYu on 2017/3/5.
 */

public class CleanChatLogDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private Conversation.ConversationType mConversationType;
    private String fromConversationId;
    private RelativeLayout rl_dialog_cleanchatlog,rl_dialog_cancel;

    public CleanChatLogDialog(Context context, Conversation.ConversationType mConversationType, String fromConversationId) {
        super(context);
        this.mContext = context;
        this.mConversationType = mConversationType;
        this.fromConversationId = fromConversationId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.cleanchatlog_dialog,null);
        setContentView(view);
        init(view);
    }

    private void init(View view){
        rl_dialog_cleanchatlog = (RelativeLayout)view.findViewById(R.id.rl_dialog_cleanchatlog);
        rl_dialog_cancel = (RelativeLayout)view.findViewById(R.id.rl_dialog_cancel);

        rl_dialog_cleanchatlog.setOnClickListener(this);
        rl_dialog_cancel.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_dialog_cleanchatlog:
                RongIM.getInstance().deleteMessages(mConversationType, fromConversationId, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {

                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        NToast.shortToast(mContext,"聊天记录删除失败");
                    }
                });
                RongIM.getInstance().removeConversation(mConversationType, fromConversationId, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        NToast.shortToast(mContext,"会话列表删除失败");
                    }
                });
                this.dismiss();
                break;
            case R.id.rl_dialog_cancel:
                this.dismiss();
                break;
        }
    }
}
