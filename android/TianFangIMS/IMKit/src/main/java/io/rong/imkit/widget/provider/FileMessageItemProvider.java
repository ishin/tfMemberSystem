package io.rong.imkit.widget.provider;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.rong.common.RLog;
import io.rong.imkit.R;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.activity.FilePreviewActivity;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.utils.FileTypeUtils;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.FileMessage;

/**
 * Created by tiankui on 16/7/15.
 */
@ProviderTag(messageContent = FileMessage.class, showProgress = false, showReadState = true)
public class FileMessageItemProvider extends IContainerItemProvider.MessageProvider<FileMessage> {
    private static final String TAG = "FileMessageItemProvider";

    private static class ViewHolder {
        RelativeLayout cancelButton;
        LinearLayout message;
        TextView fileName;
        TextView fileSize;
        TextView canceledMessage;
        ImageView fileTypeImage;
        ProgressBar fileUploadProgress;
    }

    /**
     * 创建新View。
     *
     * @param context 当前上下文。
     * @param group   创建的新View所附属的父View。
     * @return 需要创建的新View。
     */
    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_file_message, null);
        ViewHolder holder = new ViewHolder();
        holder.message = (LinearLayout) view.findViewById(R.id.rc_message);
        holder.fileTypeImage = (ImageView) view.findViewById(R.id.rc_msg_iv_file_type_image);
        holder.fileName = (TextView) view.findViewById(R.id.rc_msg_tv_file_name);
        holder.fileSize = (TextView) view.findViewById(R.id.rc_msg_tv_file_size);
        holder.fileUploadProgress = (ProgressBar) view.findViewById(R.id.rc_msg_pb_file_upload_progress);
        holder.cancelButton = (RelativeLayout) view.findViewById(R.id.rc_btn_cancel);
        holder.canceledMessage = (TextView) view.findViewById(R.id.rc_msg_canceled);
        view.setTag(holder);
        return view;
    }


    /**
     * 为View绑定数据。
     *
     * @param v        需要绑定数据的View。
     * @param position 绑定的数据位置。
     * @param content  绑定的消息内容。
     * @param message  绑定的消息。
     */
    @Override
    public void bindView(View v, int position, FileMessage content, final UIMessage message) {

        final ViewHolder holder = (ViewHolder) v.getTag();

        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.message.setBackgroundResource(R.drawable.rc_ic_bubble_right_file);
        } else {
            holder.message.setBackgroundResource(R.drawable.rc_ic_bubble_left_file);
        }
        holder.fileName.setText(content.getName());
        long fileSizeBytes = content.getSize();
        holder.fileSize.setText(FileTypeUtils.formatFileSize(fileSizeBytes));
        holder.fileTypeImage.setImageResource(FileTypeUtils.fileTypeImageId(content.getName()));

        if (message.getSentStatus().equals(Message.SentStatus.SENDING) && message.getProgress() < 100) {
            holder.fileUploadProgress.setVisibility(View.VISIBLE);
            holder.cancelButton.setVisibility(View.VISIBLE);
            holder.canceledMessage.setVisibility(View.INVISIBLE);
            holder.fileUploadProgress.setProgress(message.getProgress());
        } else {
            if (message.getSentStatus().equals(Message.SentStatus.CANCELED)) {
                holder.canceledMessage.setVisibility(View.VISIBLE);
            } else {
                holder.canceledMessage.setVisibility(View.INVISIBLE);
            }
            holder.fileUploadProgress.setVisibility(View.INVISIBLE);
            holder.cancelButton.setVisibility(View.GONE);
        }

        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RongIM.getInstance().cancelSendMediaMessage(message.getMessage(), new RongIMClient.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        holder.canceledMessage.setVisibility(View.VISIBLE);
                        holder.fileUploadProgress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
            }
        });
    }


    /**
     * 当前数据的简单描述。
     *
     * @param data 当前需要绑定的数据
     * @return 数据的描述。
     */
    @Override
    public Spannable getContentSummary(FileMessage data) {
        StringBuilder summaryPhrase = new StringBuilder();
        String fileName = data.getName();
        summaryPhrase.append(RongContext.getInstance().getString(R.string.rc_message_content_file))
                .append(" ")
                .append(fileName);
        return new SpannableString(summaryPhrase);
    }

    /**
     * View的点击事件。
     *
     * @param view     所点击的View。
     * @param position 点击的位置。
     * @param content  点击的消息内容。
     * @param message  点击的消息。
     */
    @Override
    public void onItemClick(View view, int position, FileMessage content, UIMessage message) {
        Intent intent = new Intent(view.getContext(), FilePreviewActivity.class);
        intent.putExtra("FileMessage", content);
        intent.putExtra("Message", message.getMessage());
        intent.putExtra("Progress", message.getProgress());
        view.getContext().startActivity(intent);
    }

    /**
     * View的长按事件。
     *
     * @param view     所长按的View。
     * @param position 长按的位置。
     * @param content  长按的消息内容。
     * @param message  长按的消息。
     */
    @Override
    public void onItemLongClick(final View view, int position, FileMessage content, final UIMessage message) {
        String[] items;

        long deltaTime = RongIM.getInstance().getDeltaTime();
        long normalTime = System.currentTimeMillis() - deltaTime;
        boolean enableMessageRecall = false;
        int messageRecallInterval = -1;

        try {
            enableMessageRecall = RongContext.getInstance().getResources().getBoolean(R.bool.rc_enable_message_recall);
            messageRecallInterval = RongContext.getInstance().getResources().getInteger(R.integer.rc_message_recall_interval);
        } catch (Resources.NotFoundException e) {
            RLog.e(TAG, "rc_message_recall_interval not configure in rc_config.xml");
            e.printStackTrace();
        }
        if (message.getSentStatus().equals(Message.SentStatus.SENDING) && message.getProgress() < 100) {
            return;
        }
        if (message.getSentStatus().equals(Message.SentStatus.SENT)
                && enableMessageRecall
                && (normalTime - message.getSentTime()) <= messageRecallInterval * 1000
                && message.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId())
                && !message.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE)
                && !message.getConversationType().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE)
                && !message.getConversationType().equals(Conversation.ConversationType.PUBLIC_SERVICE)
                && !message.getConversationType().equals(Conversation.ConversationType.SYSTEM)
                && !message.getConversationType().equals(Conversation.ConversationType.CHATROOM)) {
            items = new String[]{view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete), view.getContext().getResources().getString(R.string.rc_dialog_item_message_recall)};
        } else {
            items = new String[]{view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete)};
        }

        OptionsPopupDialog.newInstance(view.getContext(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
            @Override
            public void onOptionsItemClicked(int which) {
                if (which == 0) {
                    RongIM.getInstance().cancelSendMediaMessage(message.getMessage(),null);
                    RongIM.getInstance().deleteMessages(new int[] {message.getMessageId()}, null);
                } else if (which == 1) {
                    RongIM.getInstance().recallMessage(message.getMessage(), getPushContent(view.getContext(), message));
                }
            }
        }).show();
    }
}
