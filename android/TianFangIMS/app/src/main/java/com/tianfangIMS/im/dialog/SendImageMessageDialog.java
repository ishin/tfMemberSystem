package com.tianfangIMS.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.utils.NToast;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;

/**
 * Created by LianMengYu on 2017/3/13.
 */

public class SendImageMessageDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private String ids;
    private int position;
    private String name;
    private List<String> ImageMessageList;
    private Conversation.ConversationType conversationType;
    private String uri;
    private String Pos;
    private Button btn_quxiao_move, btn_submit_move;
    private ImageView iv_movegroupuser_photo;
    private TextView tv_movegroupuser_departmentName;
    private TextView tv_person_departmentTxt;
    private TextView tv_number;

    public SendImageMessageDialog(Context context, String ids, int position, String name, List<String> imageMessageList,
                                  Conversation.ConversationType conversationType, String uri, String Pos) {
        super(context);
        this.mContext = context;
        this.ids = ids;
        this.position = position;
        this.name = name;
        this.ImageMessageList = imageMessageList;
        this.conversationType = conversationType;
        this.uri = uri;
        this.Pos = Pos;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.movegroupuser_dialog, null);
        setContentView(view);
        init(view);
    }

    private void init(View view) {
        btn_quxiao_move = (Button) view.findViewById(R.id.btn_quxiao_move);
        btn_submit_move = (Button) view.findViewById(R.id.btn_submit_move);
        iv_movegroupuser_photo = (ImageView) view.findViewById(R.id.iv_movegroupuser_photo);
        tv_movegroupuser_departmentName = (TextView) view.findViewById(R.id.tv_movegroupuser_departmentName);
        tv_person_departmentTxt = (TextView) view.findViewById(R.id.tv_person_departmentTxt);
        tv_number = (TextView) view.findViewById(R.id.tv_number);
        tv_number.setVisibility(View.VISIBLE);

        tv_movegroupuser_departmentName.setText(name);
        tv_person_departmentTxt.setText("");
        tv_number.setText("共" + ImageMessageList.size() + "文件");
        Picasso.with(mContext)
                .load(uri)
                .resize(80, 80)
                .into(iv_movegroupuser_photo);
        btn_quxiao_move.setOnClickListener(this);
        btn_submit_move.setOnClickListener(this);
    }

    private void SendImageMessage() {
        for (int i = 0; i < ImageMessageList.size(); i++) {
            ImageMessage imageMessage = ImageMessage.obtain(null, Uri.parse(ImageMessageList.get(i)), true);
            Log.e("ImageMessage","----------:" + ImageMessageList.get(i));
            RongIM.getInstance().sendImageMessage(Conversation.ConversationType.PRIVATE, ids, imageMessage, null, null,
                    new RongIMClient.SendImageMessageCallback() {
                        @Override
                        public void onAttached(Message message) {

                        }

                        @Override
                        public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                            LoadDialog.dismiss(mContext);
                            NToast.shortToast(mContext, "发送失败" + errorCode.getValue());
                        }

                        @Override
                        public void onSuccess(Message message) {
                            LoadDialog.dismiss(mContext);
                            NToast.shortToast(mContext, "发送成功");
                            if (conversationType.equals(Conversation.ConversationType.PRIVATE)) {
                                RongIM.getInstance().startPrivateChat(mContext, ids, name);
                            } else if (conversationType.equals(Conversation.ConversationType.GROUP)) {
                                RongIM.getInstance().startGroupChat(mContext, ids, name);
                            }
                        }

                        @Override
                        public void onProgress(Message message, int i) {
                            Log.e("asdsadasdsa", "消息：" + message + "---进度:" + i);
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_quxiao_move:
                this.dismiss();
                break;
            case R.id.btn_submit_move:
                SendImageMessage();
                this.dismiss();
                break;
        }
    }
}
