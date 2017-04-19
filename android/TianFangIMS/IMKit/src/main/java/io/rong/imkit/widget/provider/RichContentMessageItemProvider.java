package io.rong.imkit.widget.provider;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.rong.common.RLog;
import io.rong.imkit.R;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongKitIntent;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.RichContentMessage;

@ProviderTag(messageContent = RichContentMessage.class, showReadState = true)
public class RichContentMessageItemProvider extends IContainerItemProvider.MessageProvider<RichContentMessage> {
    private static final String TAG = "RichContentMessageItemProvider";

    private static class ViewHolder {
        AsyncImageView img;
        TextView title;
        TextView content;
        RelativeLayout mLayout;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_rich_content_message, null);
        ViewHolder holder = new ViewHolder();
        holder.title = (TextView) view.findViewById(R.id.rc_title);
        holder.content = (TextView)view.findViewById(R.id.rc_content);
        holder.img = (AsyncImageView) view.findViewById(R.id.rc_img);
        holder.mLayout = (RelativeLayout) view.findViewById(R.id.rc_layout);
        view.setTag(holder);
        return view;
    }

    @Override
    public void onItemClick(View view, int position, RichContentMessage content, UIMessage message) {

        String action = RongKitIntent.RONG_INTENT_ACTION_WEBVIEW;
        Intent intent = new Intent(action);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("url", content.getUrl());
        intent.setPackage(view.getContext().getPackageName());
        view.getContext().startActivity(intent);
    }

    @Override
    public void onItemLongClick(final View view, int position, RichContentMessage content, final UIMessage message) {
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
        if (hasSent && enableMessageRecall
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
                    RongIM.getInstance().deleteMessages(new int[] {message.getMessageId()}, null);
                } else if (which == 1) {
                    RongIM.getInstance().recallMessage(message.getMessage(), getPushContent(view.getContext(), message));
                }
            }
        }).show();
    }

    @Override
    public void bindView(View v, int position, RichContentMessage content, UIMessage message) {
        ViewHolder holder = (ViewHolder) v.getTag();
        holder.title.setText(content.getTitle());
        holder.content.setText(content.getContent());
        if (content.getImgUrl() != null) {
            holder.img.setResource(content.getImgUrl(), 0);
        }

        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.mLayout.setBackgroundResource(R.drawable.rc_ic_bubble_right_file);
        } else {
            holder.mLayout.setBackgroundResource(R.drawable.rc_ic_bubble_left_file);
        }
    }

    @Override
    public Spannable getContentSummary(RichContentMessage data) {
        return new SpannableString(RongContext.getInstance().getResources().getString(R.string.rc_message_content_rich_text));
    }
}
