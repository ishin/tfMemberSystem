package io.rong.imkit.widget.provider;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.rong.common.RLog;
import io.rong.imkit.R;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongKitIntent;
import io.rong.imkit.manager.SendImageManager;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;

/**
 * Created by DragonJ on 14-8-2.
 */
@ProviderTag(messageContent = ImageMessage.class, showProgress = false, showReadState = true)
public class ImageMessageItemProvider extends IContainerItemProvider.MessageProvider<ImageMessage> {

    private static final String TAG = "ImageMessageItemProvider";

    private static class ViewHolder {
        AsyncImageView img;
        TextView message;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_image_message, null);

        ViewHolder holder = new ViewHolder();

        holder.message = (TextView) view.findViewById(R.id.rc_msg);
        holder.img = (AsyncImageView) view.findViewById(R.id.rc_img);

        view.setTag(holder);
        return view;
    }

    @Override
    public void onItemClick(View view, int position, ImageMessage content, UIMessage message) {
        if (content != null) {
            Intent intent = new Intent(RongKitIntent.RONG_INTENT_ACTION_PICTUREPAGERVIEW);
            intent.setPackage(view.getContext().getPackageName());
            intent.putExtra("message", message.getMessage());
            view.getContext().startActivity(intent);
        }
    }

    @Override
    public void onItemLongClick(final View view, int position, ImageMessage content, final UIMessage message) {
        String[] items;

        long deltaTime = RongIM.getInstance().getDeltaTime();
        long normalTime = System.currentTimeMillis() - deltaTime;
        boolean enableMessageRecall = false;
        int messageRecallInterval = -1;
        boolean hasSent = (!message.getSentStatus().equals(Message.SentStatus.SENDING)) && (!message.getSentStatus().equals(Message.SentStatus.FAILED));

        try {
            enableMessageRecall = RongContext.getInstance().getResources().getBoolean(R.bool.rc_enable_message_recall);
            messageRecallInterval = RongContext.getInstance().getResources().getInteger(R.integer.rc_message_recall_interval);
        } catch (Resources.NotFoundException e) {
            RLog.e(TAG, "rc_message_recall_interval not configure in rc_config.xml");
            e.printStackTrace();
        }

        if (hasSent
                && enableMessageRecall
                && (normalTime - message.getSentTime()) <= messageRecallInterval * 1000
                && message.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId())
                && !message.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE)
                && !message.getConversationType().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE)
                && !message.getConversationType().equals(Conversation.ConversationType.PUBLIC_SERVICE)
                && !message.getConversationType().equals(Conversation.ConversationType.SYSTEM)
                && !message.getConversationType().equals(Conversation.ConversationType.CHATROOM)) {
            items = new String[] {view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete), view.getContext().getResources().getString(R.string.rc_dialog_item_message_recall)};
        } else {
            items = new String[] {view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete)};
        }

        OptionsPopupDialog.newInstance(view.getContext(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
            @Override
            public void onOptionsItemClicked(int which) {
                if (which == 0) {
                    SendImageManager.getInstance().cancelSendingImage(message.getConversationType(), message.getTargetId(), message.getMessageId());
                    RongIM.getInstance().deleteMessages(new int[] {message.getMessageId()}, null);
                } else if (which == 1) {
                    RongIM.getInstance().recallMessage(message.getMessage(), getPushContent(view.getContext(), message));
                }
            }
        }).show();
    }

    @Override
    public void bindView(View v, int position, ImageMessage content, UIMessage message) {
        final ViewHolder holder = (ViewHolder) v.getTag();

        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
            v.setBackgroundResource(R.drawable.rc_ic_bubble_no_right);
        } else {
            v.setBackgroundResource(R.drawable.rc_ic_bubble_no_left);
        }

        holder.img.setResource(content.getThumUri());

        int progress = message.getProgress();

        Message.SentStatus status = message.getSentStatus();

        if (status.equals(Message.SentStatus.SENDING) && progress < 100) {
            holder.message.setText(progress + "%");
            holder.message.setVisibility(View.VISIBLE);
        } else {
            holder.message.setVisibility(View.GONE);
        }
    }

    @Override
    public Spannable getContentSummary(ImageMessage data) {
        return new SpannableString(RongContext.getInstance()
                                   .getResources()
                                   .getString(R.string.rc_message_content_image));
    }
}
