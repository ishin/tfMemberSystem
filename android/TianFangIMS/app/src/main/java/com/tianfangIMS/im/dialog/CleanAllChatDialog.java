package com.tianfangIMS.im.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.tianfangIMS.im.R;
import com.tianfangIMS.im.service.FloatService;
import com.tianfangIMS.im.utils.NToast;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by LianMengYu on 2017/3/10.
 */

public class CleanAllChatDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private RelativeLayout rl_dialog_cleanchatlog, rl_dialog_cancel;
    private SharedPreferences.Editor editor;
    private SharedPreferences sp;

    public CleanAllChatDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.cleanchatlog_dialog, null);
        sp = mContext.getSharedPreferences("config", Activity.MODE_PRIVATE);
        editor = sp.edit();
        setContentView(view);
        init(view);
    }

    private void init(View view) {
        rl_dialog_cleanchatlog = (RelativeLayout) view.findViewById(R.id.rl_dialog_cleanchatlog);
        rl_dialog_cancel = (RelativeLayout) view.findViewById(R.id.rl_dialog_cancel);

        rl_dialog_cleanchatlog.setOnClickListener(this);
        rl_dialog_cancel.setOnClickListener(this);
    }

    private void clearConversation() {

        RongIM.getInstance().clearConversations(new RongIMClient.ResultCallback() {
            @Override
            public void onSuccess(Object o) {
                NToast.shortToast(mContext, "聊天记录删除成功");
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                NToast.shortToast(mContext, "聊天记录删除失败");
            }
        }, Conversation.ConversationType.PRIVATE, Conversation.ConversationType.GROUP);

//        List<Conversation> conversationList = RongIM.getInstance().getConversationList();
//        if(conversationList != null && conversationList.size() > 0){
//            for (int i = 0; i < conversationList.size(); i++) {
//                RongIM.getInstance().removeConversation(conversationList.get(i).getConversationType(), conversationList.get(i).getTargetId(), new RongIMClient.ResultCallback<Boolean>() {
//                    @Override
//                    public void onSuccess(Boolean aBoolean) {
//                        NToast.shortToast(mContext, "聊天记录删除成功");
//                    }
//
//                    @Override
//                    public void onError(RongIMClient.ErrorCode errorCode) {
//                        NToast.shortToast(mContext, "聊天记录删除失败");
//                    }
//                });
//            }
//        }

    }

    private void setDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("提示");
        builder.setMessage("确定要清空聊天记录");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearConversation();
                Intent mIntent = new Intent(mContext, FloatService.class);
                mContext.stopService(mIntent);
                editor.putBoolean("isOpen", false);
                editor.apply();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog simpledialog = builder.create();
        simpledialog.setCanceledOnTouchOutside(false);
        simpledialog.setCancelable(false);
        simpledialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_dialog_cleanchatlog:
                setDialog();
                this.dismiss();
                break;
            case R.id.rl_dialog_cancel:
                this.dismiss();
                break;
        }
    }
}
