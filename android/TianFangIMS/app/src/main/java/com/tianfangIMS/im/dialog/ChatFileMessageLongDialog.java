package com.tianfangIMS.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.tianfangIMS.im.R;
import com.tianfangIMS.im.activity.SendMessageActivity;
import com.tianfangIMS.im.utils.NToast;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

/**
 * Created by LianMengYu on 2017/5/24.
 * 文件长按事件
 */

public class ChatFileMessageLongDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private RelativeLayout rl_dialog_ReSend, rl_dialog_delete;
    private Message message;

    public ChatFileMessageLongDialog(Context context, Message message) {
        super(context);
        this.mContext = context;
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.chatfilemessagelong_dialog, null);
        setContentView(view);
        init(view);
    }

    private void init(View v) {
        rl_dialog_ReSend = (RelativeLayout) v.findViewById(R.id.rl_dialog_ReSend);
        rl_dialog_delete = (RelativeLayout) v.findViewById(R.id.rl_dialog_delete);
        rl_dialog_ReSend.setOnClickListener(this);
        rl_dialog_delete.setOnClickListener(this);
    }

    private int[] getMessageId() throws NullPointerException {
        List<Integer> MessageIds = new ArrayList<>();
        MessageIds.add(message.getMessageId());
        int ids[] = new int[MessageIds.size()];
        try {
            for (int i = 0, j = MessageIds.size(); i < j; i++) {
                ids[i] = MessageIds.get(i);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return ids;
    }

    private void SendFlieMessage() {
        RongIM.getInstance().sendMessage(message.getConversationType(), message.getTargetId(), message.getContent(), "", "", new RongIMClient.SendImageMessageCallback() {
            @Override
            public void onAttached(Message message) {

            }

            @Override
            public void onSuccess(Message message) {
                NToast.shortToast(getOwnerActivity().getApplicationContext(), "发送成功");
                dismiss();
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                NToast.shortToast(mContext, "发送失败" + errorCode.getValue());
                dismiss();
            }

            @Override
            public void onProgress(Message message, int i) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_dialog_ReSend:
                ArrayList<Message> messagesList = new ArrayList<>();
                messagesList.add(message);
                if (messagesList != null && messagesList.size() > 0) {
                    Intent intentfile = new Intent(mContext, SendMessageActivity.class);
                    intentfile.putParcelableArrayListExtra("allFile", messagesList);
                    mContext.startActivity(intentfile);
                    dismiss();
                }
                break;
            case R.id.rl_dialog_delete:
                List<Integer> MessageIds = new ArrayList<>();
                MessageIds.add(message.getMessageId());
                int ids[] = new int[MessageIds.size()];
                for (int i = 0, j = MessageIds.size(); i < j; i++) {
                    ids[i] = MessageIds.get(i);
                }
                RongIM.getInstance().deleteMessages(getMessageId(), new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        NToast.shortToast(mContext, "删除成功");
                        dismiss();
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        NToast.shortToast(mContext, "删除失败" + errorCode);
                        dismiss();
                    }
                });
                break;
        }
    }
}
