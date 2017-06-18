package io.rong.imkit.widget.provider;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import io.rong.common.RLog;
import io.rong.imkit.R;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.plugin.location.AMapPreviewActivity;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.LocationMessage;

@ProviderTag(messageContent = LocationMessage.class, showReadState = true)
public class LocationMessageItemProvider extends IContainerItemProvider.MessageProvider<LocationMessage> {
    private final static String TAG = "LocationMessageItemProvider";

    private static class ViewHolder {
        AsyncImageView img;
        TextView title;
        FrameLayout mLayout;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_location_message, null);

        ViewHolder holder = new ViewHolder();

        holder.img = (AsyncImageView) view.findViewById(R.id.rc_img);
        holder.title = (TextView) view.findViewById(R.id.rc_content);
        holder.mLayout = (FrameLayout) view.findViewById(R.id.rc_layout);

        view.setTag(holder);
        return view;
    }

    @Override
    public void onItemClick(View view, int position, LocationMessage content, UIMessage message) {
        try {
            String clsName = "com.amap.api.netlocation.AMapNetworkLocationClient";
            Class<?> locationCls = Class.forName(clsName);
            if (locationCls != null) {
                Intent intent = new Intent(view.getContext(), AMapPreviewActivity.class);
                intent.putExtra("location", message.getContent());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            }
        } catch (Exception e) {
            RLog.i(TAG, "Not default AMap Location");
            e.printStackTrace();
        }
    }

    @Override
    public void onItemLongClick(final View view, int position, LocationMessage content, final UIMessage message) {
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
                    RongIM.getInstance().deleteMessages(new int[] {message.getMessageId()}, null);
                } else if (which == 1) {
                    RongIM.getInstance().recallMessage(message.getMessage(), getPushContent(view.getContext(), message));
                }
            }
        }).show();
    }

    @Override
    public void bindView(View v, int position, final LocationMessage content, final UIMessage uiMsg) {
        ViewHolder holder = (ViewHolder) v.getTag();
        final Uri uri = content.getImgUri();
        RLog.d(TAG, "uri = " + uri);
        if (uri == null || !uri.getScheme().equals("file")) {
            holder.img.setDefaultDrawable();
        } else {
            holder.img.setResource(uri);
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(holder.title.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;

        if (uiMsg.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.mLayout.setBackgroundResource(R.drawable.rc_ic_bubble_no_right);
            params.leftMargin = 0;
            params.rightMargin = (int) (12 * v.getResources().getDisplayMetrics().density);
            holder.title.setLayoutParams(params);
        } else {
            params.leftMargin = (int) (8.5 * v.getResources().getDisplayMetrics().density);
            params.rightMargin = 0;
            holder.title.setLayoutParams(params);
            holder.mLayout.setBackgroundResource(R.drawable.rc_ic_bubble_no_left);
        }
        holder.title.setText(content.getPoi());
    }

    @Override
    public Spannable getContentSummary(LocationMessage data) {
        return new SpannableString(RongContext.getInstance().getResources().getString(R.string.rc_message_content_location));
    }
}