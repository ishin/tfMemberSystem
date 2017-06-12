package com.tianfangIMS.im.adapter;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.JiLvMessage;
import com.tianfangIMS.im.dialog.BigImagedialog;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.FileMessageTypeUtils;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.activity.FilePreviewActivity;
import io.rong.imkit.utils.FileTypeUtils;
import io.rong.imlib.model.Message;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;

/**
 * Created by Rainking on 2017/4/25.
 */

public class ItemJiLvAdapter extends BaseMultiItemQuickAdapter<JiLvMessage, BaseViewHolder> {


    public interface OnItemsClickListener {
        void OnClick(int i);
    }

    Activity activity;
    List<JiLvMessage> mData;
    private ImageView iv_jilv_image;
    //选中的聊天记录
    private ArrayList<Message> opt_message;
    private ImageView iv_jilv_image_text;
    private CheckBox cb_jilv;
    private OnItemsClickListener onItemsClickListener;
    private boolean is_opt = false;


    public ItemJiLvAdapter(Activity activity, List<JiLvMessage> mData) {
        super(mData);

        addItemType(JiLvMessage.TYPE_1, R.layout.item_jilv_image);
        addItemType(JiLvMessage.TYPE_2, R.layout.item_jilv_image_text);
        addItemType(JiLvMessage.TYPE_3, R.layout.item_jilv_image_text);
        opt_message = new ArrayList<>();
        this.activity = activity;
        this.mData = mData;
    }


    private void fItemsClickListener(int i) {
        if (onItemsClickListener != null) {
            onItemsClickListener.OnClick(i);
        }
    }

    public void setOnItemsClickListener(OnItemsClickListener onItemsClickListener) {
        this.onItemsClickListener = onItemsClickListener;
    }

    public ArrayList<Message> getOpt_message() {
        return opt_message;
    }

    public boolean is_opt() {
        return is_opt;
    }

    public void setIs_opt(boolean is_opt) {
        this.is_opt = is_opt;
    }

    public void clearOpt() {
        opt_message.clear();

    }

    @Override
    protected void convert(final BaseViewHolder helper, final JiLvMessage item) {


        switch (item.getItemType()) {
            case JiLvMessage.TYPE_1:
                iv_jilv_image = helper.getView(R.id.iv_jilv_image);
                cb_jilv = helper.getView(R.id.cb_jilv);
                ImageMessage imageMessage = (ImageMessage) item.getMessage().getContent();
                Picasso.with(activity)
                        .load(imageMessage.getThumUri())
                        .error(R.mipmap.default_image)
                        .placeholder(R.mipmap.default_image)
                        .into(iv_jilv_image);

                iv_jilv_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageMessage imageMessage = (ImageMessage) item.getMessage().getContent();
                        if (!TextUtils.isEmpty(imageMessage.getRemoteUri().toString())) {
                            BigImagedialog bigImagedialog = new BigImagedialog(activity, imageMessage.getRemoteUri().toString(), R.style.Dialog_Fullscreen);
                            bigImagedialog.getWindow().setBackgroundDrawable(new ColorDrawable());
                            bigImagedialog.show();
                            CommonUtil.SetDialogStyle(bigImagedialog);
                        }
                    }
                });

                if (is_opt()) {
                    cb_jilv.setVisibility(View.VISIBLE);
                    cb_jilv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (buttonView.isChecked()) {
                                opt_message.add(item.getMessage());
                            } else {
                                opt_message.remove(item.getMessage());
                            }
                        }
                    });
                } else {
                    cb_jilv.setChecked(false);
                    cb_jilv.setVisibility(View.GONE);

                }
                break;
            case JiLvMessage.TYPE_2:
                final FileMessage fileMessage_video = (FileMessage) item.getMessage().getContent();
                iv_jilv_image_text = helper.getView(R.id.iv_jilv_image_text);
                cb_jilv = helper.getView(R.id.cb_jilv);
                //视频图片
                Picasso.with(activity)
                        .load(R.mipmap.unknow)
                        .error(R.mipmap.default_image)
                        .placeholder(R.mipmap.default_image)
                        .into(iv_jilv_image_text);

                helper.setText(R.id.tv_jilv_name, fileMessage_video.getName());
                helper.setText(R.id.tv_jilv_size, FileTypeUtils.formatFileSize(fileMessage_video.getSize()).trim());
                //参考融云  设置具体时间
//                helper.setText(R.id.tv_jilv_date,item.getMessage().getReceivedTime())

                helper.setOnClickListener(R.id.ll_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        fItemsClickListener(helper.getPosition());
                        Intent intent = new Intent(activity, FilePreviewActivity.class);
                        intent.putExtra("FileMessage", fileMessage_video);
                        intent.putExtra("Message", item.getMessage());
                        intent.putExtra("Progress", 100);
                        activity.startActivity(intent);
                    }
                });
                if (is_opt()) {
                    cb_jilv.setVisibility(View.VISIBLE);
                    cb_jilv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (buttonView.isChecked()) {
                                opt_message.add(item.getMessage());
                            } else {
                                opt_message.remove(item.getMessage());
                            }
                        }
                    });
                } else {
                    cb_jilv.setChecked(false);
                    cb_jilv.setVisibility(View.GONE);
                }

                break;
            case JiLvMessage.TYPE_3:
                final FileMessage fileMessage_qita = (FileMessage) item.getMessage().getContent();

                iv_jilv_image_text = helper.getView(R.id.iv_jilv_image_text);
                cb_jilv = helper.getView(R.id.cb_jilv);

                String type = FileMessageTypeUtils.fileMessageType(fileMessage_qita.getName());
                //这儿呢
                //根据不同类型显示不同的图片
                switch (type) {
                    case "IMAGE":
                        break;
                    case "TXT":
                        //TXT  图片
                        Picasso.with(activity)
                                .load(R.mipmap.txt_icon)
                                .error(R.mipmap.ic_launcher)
                                .placeholder(R.mipmap.ic_launcher)
                                .into(iv_jilv_image_text);
                        break;
                    case "AUDIO":
                        break;
                    case "WORD":
                        break;
                    case "EXCEL":
                        break;
                    case "UN_KNOW":
                        Picasso.with(activity)
                                .load(R.mipmap.unknow)
                                .error(R.mipmap.ic_launcher)
                                .placeholder(R.mipmap.ic_launcher)
                                .into(iv_jilv_image_text);
                        break;
                }


                helper.setText(R.id.tv_jilv_name, fileMessage_qita.getName());
                helper.setText(R.id.tv_jilv_size, FileTypeUtils.formatFileSize(fileMessage_qita.getSize()).trim());
                //参考融云
//                helper.setText(R.id.tv_jilv_date,item.getMessage().getReceivedTime())


                helper.setOnClickListener(R.id.ll_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        fItemsClickListener(helper.getPosition());
//                        Intent intent = new Intent(activity, FilePreviewActivity.class);
                        Log.e("asdassdasdsad", "点击了");
//                        new OpenFile().openFiles(fileMessage_qita.getFileUrl());
                        Log.e("asdassdasdsad", "url:" + fileMessage_qita.getFileUrl());
//                        RongIM.getInstance().downloadMediaMessage(item.getMessage(), new IRongCallback.IDownloadMediaMessageCallback() {
//                            @Override
//                            public void onSuccess(Message message) {
//
//                            }
//
//                            @Override
//                            public void onProgress(Message message, int i) {
//                                Log.e("asdassdasdsad","onProgress--:"+i);
//                            }
//
//                            @Override
//                            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//                                Log.e("asdassdasdsad","onError--:"+errorCode);
//                            }
//                            @Override
//                            public void onCanceled(Message message) {
//                            }
//                        });
//                        fileMessage_qita.getMediaUrl()
                        //
                        Intent intent = new Intent(activity, FilePreviewActivity.class);
                        intent.putExtra("FileMessage", fileMessage_qita);
                        intent.putExtra("Message", item.getMessage());
                        intent.putExtra("Progress", 100);
                        activity.startActivity(intent);
                    }
                });

                if (is_opt()) {
                    cb_jilv.setVisibility(View.VISIBLE);
                    cb_jilv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (buttonView.isChecked()) {
                                opt_message.add(item.getMessage());
                            } else {
                                opt_message.remove(item.getMessage());
                            }
                        }
                    });
                } else {
                    cb_jilv.setChecked(false);
                    cb_jilv.setVisibility(View.GONE);
                }
                break;
        }
    }
}
