package com.tianfangIMS.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tianfangIMS.im.R;
import com.tianfangIMS.im.utils.NToast;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by LianMengYu on 2017/2/27.
 */

public class ConversationListLongDialog extends Dialog implements View.OnClickListener{
    private Context mContext;
    private RelativeLayout rl_dialog_disturb,rl_dialog_delete;
    private TextView tv_dialog_disturb;
    private  Conversation.ConversationType conversationType;
    private String targetId;
    private SharedPreferences.Editor editor;
    private SharedPreferences sp;
    private int value;
    public ConversationListLongDialog(Context context, Conversation.ConversationType conversationType, String targetId,int value) {
        super(context);
        this.mContext = context;
        this.conversationType = conversationType;
        this.targetId = targetId;
        this.value = value;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.converationlistlong_dialog,null);
        setContentView(view);
        init(view);
        if(value == 1){
            tv_dialog_disturb.setText("开启免打扰");
        }else {
            tv_dialog_disturb.setText("关闭免打扰");
        }
    }
    private void init(View v){
        rl_dialog_disturb = (RelativeLayout)v.findViewById(R.id.rl_dialog_disturb);
        rl_dialog_delete = (RelativeLayout)v.findViewById(R.id.rl_dialog_delete);
        tv_dialog_disturb = (TextView)v.findViewById(R.id.tv_dialog_disturb);

        rl_dialog_delete.setOnClickListener(this);
        rl_dialog_disturb.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_dialog_disturb:
                if(value == 1){
                    RongIMClient.getInstance().setConversationNotificationStatus(conversationType, targetId,
                            Conversation.ConversationNotificationStatus.DO_NOT_DISTURB, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
                                @Override
                                public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                                    Log.e("免打扰","---:"+ conversationNotificationStatus);
                                }
                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    Log.e("免打扰","---:"+errorCode);
                                    NToast.shortToast(mContext,"开启免打扰失败");
                                }
                            });
                }else {
//                    tv_dialog_disturb.setText("关闭免打扰");
                    RongIMClient.getInstance().setConversationNotificationStatus(conversationType, targetId,
                            Conversation.ConversationNotificationStatus.NOTIFY, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
                                @Override
                                public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                                    Log.e("免打扰","---:"+ conversationNotificationStatus);
                                }
                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    Log.e("免打扰","---:"+errorCode);
                                    NToast.shortToast(mContext,"关闭免打扰失败");
                                }
                            });

                }
                this.dismiss();
                break;
            case R.id.rl_dialog_delete:
                RongIM.getInstance().removeConversation(conversationType,targetId);
                this.dismiss();
                break;
        }
    }
}
