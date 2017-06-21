package com.tianfangIMS.im.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianfangIMS.im.R;
import com.tianfangIMS.im.adapter.ItemJiLvAdapter;
import com.tianfangIMS.im.bean.JiLvMessage;
import com.tianfangIMS.im.utils.FileMessageTypeUtils;
import com.tianfangIMS.im.utils.JsonUtil;
import com.tianfangIMS.im.utils.NToast;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;

/**
 * Created by LianMengYu on 2017/2/14.
 */
public class SelectPhoteActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "SelectPhoteActivity";
    private List<JiLvMessage> jiLvMessageList;
    TextView tv_select;
    Boolean flag = true;
    private RecyclerView rv_list;
    private LinearLayout ll_jilv_image;
    private LinearLayout ll_jilv_video;
    private LinearLayout ll_jilv_qita;
    private TextView tv_jilv_image;
    private TextView tv_jilv_video;
    private TextView tv_jilv_qita;
    private View v_jilv_image;
    private View v_jilv_video;
    private View v_jilv_qita;
    private ItemJiLvAdapter itemJiLvAdapter;
    private List<Message> messageList = new ArrayList<>();
    private ImageView iv_retransmission;
    private ImageView iv_del;
    private Conversation.ConversationType mConversationType;
    private String fromConversationId;
    private int jilv_type = 1;


    private Dialog simpledialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectphont_layout);
        setTitle("聊天文件");
        setTv_completeVisibiliy(View.VISIBLE);
        mContext = this;
        tv_select = getTv_title();
        tv_select.setText("选择");
        tv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemJiLvAdapter.is_opt()) {
                    tv_select.setText("选择");
                    itemJiLvAdapter.setIs_opt(false);
                    itemJiLvAdapter.notifyDataSetChanged();
                } else {
                    tv_select.setText("取消");
                    itemJiLvAdapter.setIs_opt(true);
                    itemJiLvAdapter.notifyDataSetChanged();
                }
            }
        });
        messageList = (List<Message>) getIntent().getSerializableExtra("messageList");
        mConversationType = (Conversation.ConversationType) getIntent().getSerializableExtra("mConversationType");
        fromConversationId = getIntent().getStringExtra("fromConversationId");
        jiLvMessageList = new ArrayList<>();
        for (Message message : messageList) {
            if (message.getContent() instanceof ImageMessage) {
                jiLvMessageList.add(new JiLvMessage(JiLvMessage.TYPE_1, message));
            }
        }

        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        iv_retransmission = (ImageView) findViewById(R.id.iv_retransmission);
        iv_del = (ImageView) findViewById(R.id.iv_del);
        iv_retransmission.setOnClickListener(this);
        iv_del.setOnClickListener(this);

        ll_jilv_image = (LinearLayout) findViewById(R.id.ll_jilv_image);
        ll_jilv_video = (LinearLayout) findViewById(R.id.ll_jilv_video);
        ll_jilv_qita = (LinearLayout) findViewById(R.id.ll_jilv_qita);

        ll_jilv_image.setOnClickListener(this);
        ll_jilv_video.setOnClickListener(this);
        ll_jilv_qita.setOnClickListener(this);


        tv_jilv_image = (TextView) findViewById(R.id.tv_jilv_image);
        tv_jilv_video = (TextView) findViewById(R.id.tv_jilv_video);
        tv_jilv_qita = (TextView) findViewById(R.id.tv_jilv_qita);

        v_jilv_image = findViewById(R.id.v_jilv_image);
        v_jilv_video = findViewById(R.id.v_jilv_video);
        v_jilv_qita = findViewById(R.id.v_jilv_qita);

        rv_list.setLayoutManager(new GridLayoutManager(this, 3));

        itemJiLvAdapter = new ItemJiLvAdapter(this, jiLvMessageList);
        rv_list.setAdapter(itemJiLvAdapter);
    }
    private void finishDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("提示");
        builder.setMessage("是否确认删除");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delFile();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        simpledialog = builder.create();
        simpledialog.setCanceledOnTouchOutside(false);
        simpledialog.setCancelable(false);
        simpledialog.show();
    }

    private void delFile() {
        if (itemJiLvAdapter.getOpt_message().size() > 0) {
            List<Integer> list_id = new ArrayList<>();
            for (Message message : itemJiLvAdapter.getOpt_message()) {
                list_id.add(message.getMessageId());
            }
            Integer[] l = list_id.toArray(new Integer[list_id.size()]);

            int[] array = new int[l.length];
            for (int i = 0; i < l.length; i++) {
                Integer integer = Integer.valueOf(l[i]);
                array[i] = integer;
            }
            RongIM.getInstance().deleteMessages(array, new RongIMClient.ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    if (aBoolean) {
                        final List<Message> all_list = new ArrayList<Message>(RongIMClient.getInstance().getHistoryMessages(mConversationType, fromConversationId, -1, Integer.MAX_VALUE));
                        jiLvMessageList = new ArrayList<JiLvMessage>();
                        //刷新聊天记录列表
                        switch (jilv_type) {
                            case 1:
                                jiLvMessageList.clear();

                                for (Message message : all_list) {
                                    if (message.getContent() instanceof ImageMessage) {
                                        jiLvMessageList.add(new JiLvMessage(JiLvMessage.TYPE_1, message));
                                    }
                                }
                                itemJiLvAdapter.setNewData(jiLvMessageList);

                                itemJiLvAdapter.setIs_opt(false);
                                if (itemJiLvAdapter.is_opt()) {
                                    tv_select.setText("取消");
                                    itemJiLvAdapter.setIs_opt(true);
                                    itemJiLvAdapter.notifyDataSetChanged();
                                } else {
                                    tv_select.setText("选择");
                                    itemJiLvAdapter.setIs_opt(false);
                                    itemJiLvAdapter.notifyDataSetChanged();
                                }

                                itemJiLvAdapter.notifyDataSetChanged();
                                NToast.shortToast(SelectPhoteActivity.this, "删除图片成功");

                                break;
                            case 2:
                                jiLvMessageList.clear();
                                for (Message message : all_list) {
                                    if (message.getContent() instanceof FileMessage) {
                                        FileMessage fileMessage = (FileMessage) message.getContent();
                                        String type = FileMessageTypeUtils.fileMessageType(fileMessage.getName());
                                        switch (type) {
                                            case "VIDEO":
                                                jiLvMessageList.add(new JiLvMessage(JiLvMessage.TYPE_2, message));
                                                break;
                                        }
                                    }
                                }
                                itemJiLvAdapter.setNewData(jiLvMessageList);
                                itemJiLvAdapter.setIs_opt(false);

                                if (itemJiLvAdapter.is_opt()) {
                                    tv_select.setText("取消");
                                    itemJiLvAdapter.setIs_opt(true);
                                    itemJiLvAdapter.notifyDataSetChanged();
                                } else {
                                    tv_select.setText("选择");
                                    itemJiLvAdapter.setIs_opt(false);
                                    itemJiLvAdapter.notifyDataSetChanged();
                                }
                                itemJiLvAdapter.notifyDataSetChanged();
                                NToast.shortToast(SelectPhoteActivity.this, "删除音频成功");
                                break;
                            case 3:
                                jiLvMessageList.clear();
                                for (Message message : all_list) {
                                    if (message.getContent() instanceof FileMessage) {
                                        FileMessage fileMessage = (FileMessage) message.getContent();
                                        String type = FileMessageTypeUtils.fileMessageType(fileMessage.getName());
                                        switch (type) {
                                            case "IMAGE":
                                            case "TXT":
                                            case "AUDIO":
                                            case "WORD":
                                            case "EXCEL":
                                            case "UN_KNOW":
                                                jiLvMessageList.add(new JiLvMessage(JiLvMessage.TYPE_3, message));
                                                break;
                                        }
                                    }
                                }
                                itemJiLvAdapter.setNewData(jiLvMessageList);
                                itemJiLvAdapter.setIs_opt(false);
                                if (itemJiLvAdapter.is_opt()) {
                                    tv_select.setText("取消");
                                    itemJiLvAdapter.setIs_opt(true);
                                    itemJiLvAdapter.notifyDataSetChanged();
                                } else {
                                    tv_select.setText("选择");
                                    itemJiLvAdapter.setIs_opt(false);
                                    itemJiLvAdapter.notifyDataSetChanged();
                                }
                                itemJiLvAdapter.notifyDataSetChanged();
                                NToast.shortToast(SelectPhoteActivity.this, "删除文件成功");
                                break;
                        }
                    } else {
                        NToast.shortToast(SelectPhoteActivity.this, "删除失败");
                    }

                }
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    NToast.shortToast(SelectPhoteActivity.this, "删除失败");
                }
            });
        } else {
            NToast.shortToast(SelectPhoteActivity.this, "请先选择文件");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_jilv_image:
                tv_select.setText("选择");
                itemJiLvAdapter.setIs_opt(false);
                jilv_type = 1;
                v_jilv_image.setBackgroundResource(R.drawable.item_tab_box2);
                v_jilv_video.setBackgroundResource(R.drawable.item_tab_box);
                v_jilv_qita.setBackgroundResource(R.drawable.item_tab_box);
                jiLvMessageList.clear();
                for (Message message : messageList) {
                    if (message.getContent() instanceof ImageMessage) {
                        jiLvMessageList.add(new JiLvMessage(JiLvMessage.TYPE_1, message));
                    }

                }

                rv_list.setLayoutManager(new GridLayoutManager(this, 3));
                itemJiLvAdapter = new ItemJiLvAdapter(this, jiLvMessageList);
                rv_list.setAdapter(itemJiLvAdapter);
                break;
            case R.id.ll_jilv_video:
                tv_select.setText("选择");
                itemJiLvAdapter.setIs_opt(false);
                jilv_type = 2;
                v_jilv_image.setBackgroundResource(R.drawable.item_tab_box);
                v_jilv_video.setBackgroundResource(R.drawable.item_tab_box2);
                v_jilv_qita.setBackgroundResource(R.drawable.item_tab_box);
                jiLvMessageList.clear();
                for (Message message : messageList) {
                    if (message.getContent() instanceof FileMessage) {
                        FileMessage fileMessage = (FileMessage) message.getContent();
                        String type = FileMessageTypeUtils.fileMessageType(fileMessage.getName());
                        switch (type) {
                            case "VIDEO":
                                jiLvMessageList.add(new JiLvMessage(JiLvMessage.TYPE_2, message));
                                break;
                        }
                    }
                }

                rv_list.setLayoutManager(new LinearLayoutManager(this));
                itemJiLvAdapter = new ItemJiLvAdapter(this, jiLvMessageList);
                rv_list.setAdapter(itemJiLvAdapter);
                break;
            case R.id.ll_jilv_qita:
                tv_select.setText("选择");
                itemJiLvAdapter.setIs_opt(false);
                jilv_type = 3;
                v_jilv_image.setBackgroundResource(R.drawable.item_tab_box);
                v_jilv_video.setBackgroundResource(R.drawable.item_tab_box);
                v_jilv_qita.setBackgroundResource(R.drawable.item_tab_box2);
                jiLvMessageList.clear();
                for (Message message : messageList) {
                    if (message.getContent() instanceof FileMessage) {
                        FileMessage fileMessage = (FileMessage) message.getContent();
                        String type = FileMessageTypeUtils.fileMessageType(fileMessage.getName());
                        switch (type) {
                            case "IMAGE":
                            case "TXT":
                            case "AUDIO":
                            case "WORD":
                            case "EXCEL":
                            case "UN_KNOW":
                                jiLvMessageList.add(new JiLvMessage(JiLvMessage.TYPE_3, message));
                                break;
                        }
                    }
                }

                rv_list.setLayoutManager(new LinearLayoutManager(this));
                itemJiLvAdapter = new ItemJiLvAdapter(this, jiLvMessageList);
                rv_list.setAdapter(itemJiLvAdapter);
                break;
            case R.id.iv_del:
              //TODO
                finishDialog();
                break;
            case R.id.iv_retransmission:
                Log.d("iv_retransmission", itemJiLvAdapter.getOpt_message().size() + "onClick: " + JsonUtil.toJson(itemJiLvAdapter.getOpt_message()));
                if (itemJiLvAdapter.getOpt_message().size() > 0) {
                    switch (jilv_type) {
                        case 1:
                            Intent intent = new Intent(SelectPhoteActivity.this, SendMessageActivity.class);
                            intent.putParcelableArrayListExtra("allMessage", itemJiLvAdapter.getOpt_message());
                            startActivity(intent);
                            finish();
                            break;
                        case 2:
                        case 3:
                            Intent intentfile = new Intent(SelectPhoteActivity.this, SendMessageActivity.class);
                            intentfile.putParcelableArrayListExtra("allFile", itemJiLvAdapter.getOpt_message());
                            startActivity(intentfile);
                            finish();
                            break;
                    }
                } else {
                    NToast.shortToast(SelectPhoteActivity.this, "请先选择文件");
                }
                break;
        }
    }
}
