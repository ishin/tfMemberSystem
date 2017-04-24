package io.rong.imkit.widget.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.rong.imkit.R;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UnknownMessage;

@ProviderTag(messageContent = UnknownMessage.class, showPortrait = false, showWarning = false, centerInHorizontal = true, showSummaryWithName = false)
public class UnknownMessageItemProvider extends IContainerItemProvider.MessageProvider<MessageContent> {

    @Override
    public void bindView(View v, int position, MessageContent content, UIMessage message) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();
        viewHolder.contentTextView.setText(R.string.rc_message_unknown);
    }

    @Override
    public Spannable getContentSummary(MessageContent data) {
        return new SpannableString(RongContext.getInstance().getResources().getString(R.string.rc_message_unknown));
    }

    @Override
    public void onItemClick(View view, int position, MessageContent
                            content, UIMessage message) {
    }

    @Override
    public void onItemLongClick(View view, int position, MessageContent content, final UIMessage message) {
        String[] items;

        items = new String[] {view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete)};

        OptionsPopupDialog.newInstance(view.getContext(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
            @Override
            public void onOptionsItemClicked(int which) {
                if (which == 0)
                    RongIM.getInstance().deleteMessages(new int[] {message.getMessageId()}, null);
            }
        }).show();
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