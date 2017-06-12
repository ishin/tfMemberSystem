package io.rong.imkit.widget.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
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
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.model.UserInfo;
import io.rong.message.RecallNotificationMessage;

@ProviderTag(messageContent = RecallNotificationMessage.class, showPortrait = false, showProgress = false, showWarning = false, centerInHorizontal = true,
             showSummaryWithName = false)
public class RecallMessageItemProvider extends IContainerItemProvider.MessageProvider<RecallNotificationMessage> {
    @Override
    public void onItemClick(View view, int position, RecallNotificationMessage content, UIMessage message) {

    }

    @Override
    public void bindView(View v, int position, RecallNotificationMessage content, UIMessage message) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();

        if (content != null && message != null) {
            String information;
            if (content.getOperatorId().equals(RongIM.getInstance().getCurrentUserId())) {
                information = RongContext.getInstance().getString(R.string.rc_you_recalled_a_message);
            } else {
                UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(content.getOperatorId());
                if (userInfo != null && userInfo.getName() != null) {
                    information = userInfo.getName() + RongContext.getInstance().getString(R.string.rc_recalled_a_message);
                } else {
                    information = content.getOperatorId() + RongContext.getInstance().getString(R.string.rc_recalled_a_message);
                }
            }
            viewHolder.contentTextView.setText(information);
        }
    }

    @Override
    public void onItemLongClick(View view, int position, RecallNotificationMessage content, UIMessage message) {

    }

    @Override
    public Spannable getContentSummary(RecallNotificationMessage data) {
        if (data != null && !TextUtils.isEmpty(data.getOperatorId())) {
            String information;

            if (data.getOperatorId().equals(RongIM.getInstance().getCurrentUserId())) {
                information = RongContext.getInstance().getString(R.string.rc_you_recalled_a_message);
            } else {
                UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getOperatorId());
                if (userInfo != null && userInfo.getName() != null) {
                    information = userInfo.getName() + RongContext.getInstance().getString(R.string.rc_recalled_a_message);
                } else {
                    information = data.getOperatorId() + RongContext.getInstance().getString(R.string.rc_recalled_a_message);
                }
            }
            return new SpannableString(information);
        }
        return null;
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
