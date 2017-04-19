package com.tianfangIMS.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.tianfangIMS.im.R;
import com.tianfangIMS.im.utils.NToast;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by LianMengYu on 2017/3/10.
 */

public class CleanAllChatDialog extends Dialog implements View.OnClickListener{
    private Context mContext;
    private RelativeLayout rl_dialog_cleanchatlog,rl_dialog_cancel;

    public CleanAllChatDialog(Context context) {
        super(context);
        this.mContext =context;
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
    private void clearConversation() {
        List<Conversation> list = RongIM.getInstance().getRongIMClient().getConversationList();
        for (int i = 0; i < list.size(); i++) {
            RongIM.getInstance().clearMessages(list.get(i).getConversationType(), list.get(i).getTargetId(), new RongIMClient.ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
            RongIM.getInstance().removeConversation(list.get(i).getConversationType(), list.get(i).getTargetId(), new RongIMClient.ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    NToast.shortToast(mContext, "聊天记录删除成功");
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_dialog_cleanchatlog:
                clearConversation();
                this.dismiss();
                break;
            case R.id.rl_dialog_cancel:
                this.dismiss();
                break;
        }
    }
}
