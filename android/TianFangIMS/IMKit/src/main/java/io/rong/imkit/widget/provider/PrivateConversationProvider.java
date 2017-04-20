package io.rong.imkit.widget.provider;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.rong.common.RLog;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.R;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.ConversationKey;
import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utils.RongDateUtils;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

import static io.rong.imlib.statistics.UserData.name;

@ConversationProviderTag(conversationType = "private", portraitPosition = 1)
public class PrivateConversationProvider implements IContainerItemProvider.ConversationProvider<UIConversation> {
    private static final String TAG = "PrivateConversationProvider";

    class ViewHolder {
        TextView title;
        TextView time;
        TextView content;
        ImageView notificationBlockImage;
        ImageView readStatus;
    }

    public View newView(Context context, ViewGroup viewGroup) {
        View result = LayoutInflater.from(context).inflate(R.layout.rc_item_base_conversation, null);

        ViewHolder holder = new ViewHolder();
        holder.title = (TextView) result.findViewById(R.id.rc_conversation_title);
        holder.time = (TextView) result.findViewById(R.id.rc_conversation_time);
        holder.content = (TextView) result.findViewById(R.id.rc_conversation_content);
        holder.notificationBlockImage = (ImageView) result.findViewById(R.id.rc_conversation_msg_block);
        holder.readStatus = (ImageView) result.findViewById(R.id.rc_conversation_status);
        result.setTag(holder);

        return result;
    }

    public void bindView(View view, int position, UIConversation data) {
        ViewHolder holder = (ViewHolder) view.getTag();
        ProviderTag tag = null;
        if (data == null) {
            holder.title.setText(null);
            holder.time.setText(null);
            holder.content.setText(null);
        } else {
            //设置会话标题
            holder.title.setText(data.getUIConversationTitle());
            //设置会话时间
            String time = RongDateUtils.getConversationListFormatDate(data.getUIConversationTime(), view.getContext());
            holder.time.setText(time);

            //设置内容
            if (!TextUtils.isEmpty(data.getDraft()) || data.getMentionedFlag()) {
                SpannableStringBuilder builder = new SpannableStringBuilder();
                SpannableString string;
                if (data.getMentionedFlag()) {
                    string = new SpannableString(view.getContext().getString(R.string.rc_message_content_mentioned));
                    string.setSpan(new ForegroundColorSpan(view.getContext().getResources().getColor(R.color.rc_mentioned_color)), 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.append(string).append(" ").append(data.getConversationContent());
                } else {
                    string = new SpannableString(view.getContext().getString(R.string.rc_message_content_draft));
                    string.setSpan(new ForegroundColorSpan(view.getContext().getResources().getColor(R.color.rc_draft_color)), 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.append(string).append(" ")
                            .append(data.getDraft());
                }
                AndroidEmoji.ensure(builder);

                holder.content.setText(builder);
                holder.readStatus.setVisibility(View.GONE);

            } else {
                //设置已读
                //readRec 是否显示已读回执
                boolean readRec = false;
                try {
                    readRec = view.getResources().getBoolean(R.bool.rc_read_receipt);
                } catch (Resources.NotFoundException e) {
                    RLog.e(TAG, "rc_read_receipt not configure in rc_config.xml");
                    e.printStackTrace();
                }

                if (readRec) {
                    if (data.getSentStatus() == Message.SentStatus.READ
                            && data.getConversationSenderId().equals(RongIM.getInstance().getCurrentUserId())) {
                        holder.readStatus.setVisibility(View.VISIBLE);
                    } else {
                        holder.readStatus.setVisibility(View.GONE);
                    }
                }
                holder.content.setText(data.getConversationContent());
            }

            if (RongContext.getInstance() != null && data.getMessageContent() != null)
                tag = RongContext.getInstance().getMessageProviderTag(data.getMessageContent().getClass());

            if (data.getSentStatus() != null && (data.getSentStatus() == Message.SentStatus.FAILED
                    || data.getSentStatus() == Message.SentStatus.SENDING) && tag != null && tag.showWarning() == true
                    && data.getConversationSenderId() != null && data.getConversationSenderId().equals(RongIM.getInstance().getCurrentUserId())) {
                Bitmap bitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.rc_conversation_list_msg_send_failure);
                int width = bitmap.getWidth();
                Drawable drawable = null;
                if (data.getSentStatus() == Message.SentStatus.FAILED && TextUtils.isEmpty(data.getDraft()))
                    drawable = view.getContext().getResources().getDrawable(R.drawable.rc_conversation_list_msg_send_failure);
                else if (data.getSentStatus() == Message.SentStatus.SENDING && TextUtils.isEmpty(data.getDraft()))
                    drawable = view.getContext().getResources().getDrawable(R.drawable.rc_conversation_list_msg_sending);
                if (drawable != null) {
                    drawable.setBounds(0, 0, width, width);
                    holder.content.setCompoundDrawablePadding(10);
                    holder.content.setCompoundDrawables(drawable, null, null, null);
                }
            } else {
                holder.content.setCompoundDrawables(null, null, null, null);
            }

            ConversationKey key = ConversationKey.obtain(data.getConversationTargetId(), data.getConversationType());
            Conversation.ConversationNotificationStatus status = RongContext.getInstance().getConversationNotifyStatusFromCache(key);
            if (status != null && status.equals(Conversation.ConversationNotificationStatus.DO_NOT_DISTURB))
                holder.notificationBlockImage.setVisibility(View.VISIBLE);
            else
                holder.notificationBlockImage.setVisibility(View.GONE);
        }
    }


    public Spannable getSummary(UIConversation data) {
        return null;
    }

    public String getTitle(String userId) {
        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(userId);
        return userInfo == null ? userId : userInfo.getName();
    }

    @Override
    public Uri getPortraitUri(String userId) {
        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(userId);
        return userInfo == null ? null : userInfo.getPortraitUri();
    }

}
