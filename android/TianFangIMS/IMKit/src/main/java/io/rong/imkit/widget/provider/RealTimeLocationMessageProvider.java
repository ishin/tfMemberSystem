package io.rong.imkit.widget.provider;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
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
import io.rong.imlib.location.message.RealTimeLocationStartMessage;
import io.rong.imlib.model.Message;

/**
 * Created by weiqinxiao on 15/8/14.
 */
@ProviderTag(messageContent = RealTimeLocationStartMessage.class, showReadState = true)
public class RealTimeLocationMessageProvider extends IContainerItemProvider.MessageProvider<RealTimeLocationStartMessage> {
    private static class ViewHolder {
        TextView message;
        boolean longClick;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_share_location_message, null);

        ViewHolder holder = new ViewHolder();
        holder.message = (TextView) view.findViewById(android.R.id.text1);
        view.setTag(holder);
        return view;
    }

    @Override
    public Spannable getContentSummary(RealTimeLocationStartMessage data) {
        if (data != null && data.getContent() != null)
            return new SpannableString(RongContext.getInstance()
                                       .getResources()
                                       .getString(R.string.rc_real_time_location_summary));
        return null;
    }

    @Override
    public void onItemClick(final View view, int position, RealTimeLocationStartMessage content, final UIMessage message) {

    }

    @Override
    public void onItemLongClick(final View view, int position, final RealTimeLocationStartMessage content, final UIMessage message) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.longClick = true;
        if (view instanceof TextView) {
            CharSequence text = ((TextView) view).getText();
            if (text != null && text instanceof Spannable)
                Selection.removeSelection((Spannable) text);
        }

        String[] items;

        Resources res = view.getContext().getResources();
        items = new String[] {res.getString(R.string.rc_dialog_item_message_delete)};

        OptionsPopupDialog.newInstance(view.getContext(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
            @Override
            public void onOptionsItemClicked(int which) {
                if (which == 0)
                    RongIM.getInstance().deleteMessages(new int[] {message.getMessageId()}, null);
            }
        }).show();
    }

    @Override
    public void bindView(View v, int position, final RealTimeLocationStartMessage content, final UIMessage data) {
        ViewHolder holder = (ViewHolder) v.getTag();

        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            Drawable drawable = holder.message.getResources().getDrawable(R.drawable.rc_icon_rt_message_right);
            drawable.setBounds(0, 0, 29, 41);
            holder.message.setBackgroundResource(R.drawable.rc_ic_bubble_right);
            holder.message.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            holder.message.setText(RongContext.getInstance()
                                   .getResources()
                                   .getString(R.string.rc_real_time_location_sharing));
        } else {
            Drawable drawable = holder.message.getResources().getDrawable(R.drawable.rc_icon_rt_message_left);
            drawable.setBounds(0, 0, 29, 41);
            holder.message.setBackgroundResource(R.drawable.rc_ic_bubble_left);
            holder.message.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            holder.message.setText(RongContext.getInstance()
                                   .getResources()
                                   .getString(R.string.rc_real_time_location_sharing));
        }
    }
}
