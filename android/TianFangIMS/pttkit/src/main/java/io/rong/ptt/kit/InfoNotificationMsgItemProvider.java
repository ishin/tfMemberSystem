package io.rong.ptt.kit;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.MessageContent;
import io.rong.ptt.message.InfoNotificationMsgInterface;

public abstract class InfoNotificationMsgItemProvider<K extends MessageContent & InfoNotificationMsgInterface> extends IContainerItemProvider.MessageProvider<K> {

    @Override
    public void bindView(View v, int position, K content, UIMessage message) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();

        if (content != null) {
            if (!TextUtils.isEmpty(content.getMessage()))
                viewHolder.contentTextView.setText(content.getMessage());
        }

    }

    @Override
    public Spannable getContentSummary(K data) {
        if (data != null && !TextUtils.isEmpty(data.getMessage()))
            return new SpannableString(data.getMessage());
        return null;
    }

    @Override
    public void onItemClick(View view, int position, K
            content, UIMessage message) {
    }

    @Override
    public void onItemLongClick(View view, int position, K content, final UIMessage message) {

    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_information_notification_message, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.contentTextView = (TextView) view.findViewById(R.id.rc_msg);
        viewHolder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
        view.setTag(viewHolder);

        return view;
    }

    private static class ViewHolder {
        TextView contentTextView;
    }


}

