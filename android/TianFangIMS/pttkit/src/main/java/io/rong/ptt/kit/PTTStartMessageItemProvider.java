package io.rong.ptt.kit;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.rong.imkit.RongContext;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.AutoLinkTextView;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.ptt.message.server.PTTStartMessage;

@ProviderTag(messageContent = PTTStartMessage.class, showSummaryWithName = true, showProgress = false, showWarning = false, showReadState = true)
public class PTTStartMessageItemProvider extends IContainerItemProvider.MessageProvider<PTTStartMessage> {
    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_text_message, null);

        ViewHolder holder = new ViewHolder();
        holder.message = (AutoLinkTextView) view.findViewById(android.R.id.text1);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View v, int position, PTTStartMessage content, UIMessage data) {
        ViewHolder holder = (ViewHolder) v.getTag();

        if (data == null || content == null) {
            return;
        }
        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.message.setBackgroundResource(R.drawable.rc_ic_bubble_right);
        } else {
            holder.message.setBackgroundResource(R.drawable.rc_ic_bubble_left);
        }

//        String direction = content.getDirection();
        Drawable drawable = null;
        UserInfo userInfo = RongContext.getInstance().getCurrentUserInfo();
        String msgContent = v.getContext().getString(R.string.rce_ptt_start, userInfo != null ? userInfo.getName() : "");

        holder.message.setText(msgContent);
        holder.message.setCompoundDrawablePadding(15);

    }

    @Override
    public Spannable getContentSummary(PTTStartMessage data) {
        return new SpannableString(RongContext.getInstance().getString(R.string.rce_ptt));
    }

    @Override
    public void onItemClick(View view, int position, PTTStartMessage content, UIMessage message) {
        // TODO
    }

    @Override
    public void onItemLongClick(View view, int position, PTTStartMessage content, UIMessage message) {

    }

    private static class ViewHolder {
        AutoLinkTextView message;
    }
}
