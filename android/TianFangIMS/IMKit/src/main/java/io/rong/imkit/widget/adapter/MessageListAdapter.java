package io.rong.imkit.widget.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.common.RLog;
import io.rong.imkit.R;
import io.rong.imkit.RongContext;
import io.rong.imkit.mention.RongMentionManager;
import io.rong.imkit.model.ConversationKey;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utilities.RongUtils;
import io.rong.imkit.utils.RongDateUtils;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.DebouncedOnClickListener;
import io.rong.imkit.widget.ProviderContainerView;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.ReadReceiptInfo;
import io.rong.imlib.model.UnknownMessage;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

public class MessageListAdapter extends BaseAdapter<UIMessage> {
    private static final String TAG = "MessageListAdapter";
    private static final long READ_RECEIPT_REQUEST_INTERVAL = 120;
    private LayoutInflater mInflater;
    private Context mContext;
    private OnItemHandlerListener mOnItemHandlerListener;
    boolean evaForRobot = false;
    boolean robotMode = true;
    private boolean timeGone = false;

    class ViewHolder {
        AsyncImageView leftIconView;
        AsyncImageView rightIconView;
        TextView nameView;
        ProviderContainerView contentView;
        ProgressBar progressBar;
        ImageView warning;
        ImageView readReceipt;
        ImageView readReceiptRequest;
        TextView readReceiptStatus;
        ViewGroup layout;
        TextView time;
        TextView sentStatus;
        RelativeLayout layoutItem;
    }

    public MessageListAdapter(Context context) {
        super();
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setOnItemHandlerListener(OnItemHandlerListener onItemHandlerListener) {
        this.mOnItemHandlerListener = onItemHandlerListener;
    }

    public interface OnItemHandlerListener {
        public boolean onWarningViewClick(int position, Message data, View v);

        public void onReadReceiptStateClick(Message message);
    }

    @Override
    public long getItemId(int position) {
        UIMessage message = getItem(position);
        if (message == null)
            return -1;
        return message.getMessageId();
    }

    @Override
    protected View newView(final Context context, final int position, ViewGroup group) {
        View result = mInflater.inflate(R.layout.rc_item_message, null);

        final ViewHolder holder = new ViewHolder();
        holder.leftIconView = findViewById(result, R.id.rc_left);
        holder.rightIconView = findViewById(result, R.id.rc_right);
        holder.nameView = findViewById(result, R.id.rc_title);
        holder.contentView = findViewById(result, R.id.rc_content);
        holder.layout = findViewById(result, R.id.rc_layout);
        holder.progressBar = findViewById(result, R.id.rc_progress);
        holder.warning = findViewById(result, R.id.rc_warning);
        holder.readReceipt = findViewById(result, R.id.rc_read_receipt);
        holder.readReceiptRequest = findViewById(result, R.id.rc_read_receipt_request);
        holder.readReceiptStatus = findViewById(result, R.id.rc_read_receipt_status);

        holder.time = findViewById(result, R.id.rc_time);
        holder.sentStatus = findViewById(result, R.id.rc_sent_status);
        holder.layoutItem = findViewById(result, R.id.rc_layout_item_message);
        if (holder.time.getVisibility() == View.GONE) {
            timeGone = true;
        } else {
            timeGone = false;
        }
        result.setTag(holder);
        return result;
    }

    private boolean getNeedEvaluate(UIMessage data) {
        String extra = "";
        String robotEva = "";
        String sid = "";
        if (data != null && data.getConversationType() != null && data.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
            if (data.getContent() instanceof TextMessage) {
                extra = ((TextMessage) data.getContent()).getExtra();
                if (TextUtils.isEmpty(extra))
                    return false;
                try {
                    JSONObject jsonObj = new JSONObject(extra);
                    robotEva = jsonObj.optString("robotEva");
                    sid = jsonObj.optString("sid");
                } catch (JSONException e) {
                }
            }
            if (data.getMessageDirection() == Message.MessageDirection.RECEIVE
                    && data.getContent() instanceof TextMessage
                    && evaForRobot
                    && robotMode
                    && !TextUtils.isEmpty(robotEva)
                    && !TextUtils.isEmpty(sid)
                    && !data.getIsHistoryMessage()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void bindView(View v, final int position, final UIMessage data) {
        if (data == null) {
            return;
        }

        final ViewHolder holder = (ViewHolder) v.getTag();
        IContainerItemProvider provider;
        ProviderTag tag;

        if (holder == null) {
            RLog.e("MessageListAdapter", "view holder is null !");
            return;
        }
        if (getNeedEvaluate(data)) {
            provider = RongContext.getInstance().getEvaluateProvider();
            tag = RongContext.getInstance().getMessageProviderTag(data.getContent().getClass());
        } else if (RongContext.getInstance() != null && data != null && data.getContent() != null) {
            provider = RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
            if (provider == null) {
                provider = RongContext.getInstance().getMessageTemplate(UnknownMessage.class);
                tag = RongContext.getInstance().getMessageProviderTag(UnknownMessage.class);
            } else {
                tag = RongContext.getInstance().getMessageProviderTag(data.getContent().getClass());
            }
            if (provider == null) {
                RLog.e("MessageListAdapter", data.getObjectName() + " message provider not found !");
                return;
            }
        } else {
            RLog.e("MessageListAdapter", "Message is null !");
            return;
        }

        final View view = holder.contentView.inflate(provider);
        provider.bindView(view, position, data);

        if (tag == null) {
            RLog.e("MessageListAdapter", "Can not find ProviderTag for " + data.getObjectName());
            return;
        }

        if (tag.hide()) {
            holder.contentView.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
            holder.nameView.setVisibility(View.GONE);
            holder.leftIconView.setVisibility(View.GONE);
            holder.rightIconView.setVisibility(View.GONE);
            holder.layoutItem.setVisibility(View.GONE);
            holder.layoutItem.setPadding(0, 0, 0, 0);
        } else {
            holder.contentView.setVisibility(View.VISIBLE);
            holder.layoutItem.setVisibility(View.VISIBLE);
            holder.layoutItem.setPadding(RongUtils.dip2px(8),
                    RongUtils.dip2px(6),
                    RongUtils.dip2px(8),
                    RongUtils.dip2px(6));
        }

        if (data.getMessageDirection() == Message.MessageDirection.SEND) {

            if (tag.showPortrait()) {
                holder.rightIconView.setVisibility(View.VISIBLE);
                holder.leftIconView.setVisibility(View.GONE);
            } else {
                holder.leftIconView.setVisibility(View.GONE);
                holder.rightIconView.setVisibility(View.GONE);
            }

            if (!tag.centerInHorizontal()) {
                setGravity(holder.layout, Gravity.RIGHT);
                holder.contentView.containerViewRight();
                holder.nameView.setGravity(Gravity.RIGHT);
            } else {
                setGravity(holder.layout, Gravity.CENTER);
                holder.contentView.containerViewCenter();
                holder.nameView.setGravity(Gravity.CENTER_HORIZONTAL);
                holder.contentView.setBackgroundColor(Color.TRANSPARENT);
            }

            //readRec 是否显示已读回执
            boolean readRec = false;
            try {
                readRec = mContext.getResources().getBoolean(R.bool.rc_read_receipt);
            } catch (Resources.NotFoundException e) {
                RLog.e(TAG, "rc_read_receipt not configure in rc_config.xml");
                e.printStackTrace();
            }

            if (data.getSentStatus() == Message.SentStatus.SENDING) {
                if (tag.showProgress())
                    holder.progressBar.setVisibility(View.VISIBLE);
                else
                    holder.progressBar.setVisibility(View.GONE);

                holder.warning.setVisibility(View.GONE);
                holder.readReceipt.setVisibility(View.GONE);
            } else if (data.getSentStatus() == Message.SentStatus.FAILED) {
                holder.progressBar.setVisibility(View.GONE);
                holder.warning.setVisibility(View.VISIBLE);
                holder.readReceipt.setVisibility(View.GONE);
            } else if (data.getSentStatus() == Message.SentStatus.SENT) {
                holder.progressBar.setVisibility(View.GONE);
                holder.warning.setVisibility(View.GONE);
                holder.readReceipt.setVisibility(View.GONE);
            } else if (readRec && data.getSentStatus() == Message.SentStatus.READ) {
                holder.progressBar.setVisibility(View.GONE);
                holder.warning.setVisibility(View.GONE);
                if (data.getConversationType().equals(Conversation.ConversationType.PRIVATE) && tag.showReadState()) {
                    holder.readReceipt.setVisibility(View.VISIBLE);
                } else {
                    holder.readReceipt.setVisibility(View.GONE);
                }
            } else {
                holder.progressBar.setVisibility(View.GONE);
                holder.warning.setVisibility(View.GONE);
                holder.readReceipt.setVisibility(View.GONE);
            }

            holder.readReceiptRequest.setVisibility(View.GONE);
            holder.readReceiptStatus.setVisibility(View.GONE);
            if (readRec && RongContext.getInstance().isReadReceiptConversationType(data.getConversationType())
                    && (data.getConversationType().equals(Conversation.ConversationType.GROUP) || data.getConversationType().equals(Conversation.ConversationType.DISCUSSION))) {
                if (data.getContent() instanceof TextMessage
                        && !TextUtils.isEmpty(data.getUId())) {
                    boolean isLastSentMessage = true;
                    for (int i = position + 1; i < getCount(); i++) {
                        if (getItem(i).getMessageDirection() == Message.MessageDirection.SEND) {
                            isLastSentMessage = false;
                            break;
                        }
                    }

                    long serverTime = System.currentTimeMillis() - RongIMClient.getInstance().getDeltaTime();
                    if ((serverTime - data.getSentTime() < READ_RECEIPT_REQUEST_INTERVAL * 1000)
                            && isLastSentMessage
                            && (data.getReadReceiptInfo() == null || !data.getReadReceiptInfo().isReadReceiptMessage())) {
                        holder.readReceiptRequest.setVisibility(View.VISIBLE);
                    }
                }
                if (data.getContent() instanceof TextMessage
                        && data.getReadReceiptInfo() != null
                        && data.getReadReceiptInfo().isReadReceiptMessage()) {
                    if (data.getReadReceiptInfo().getRespondUserIdList() != null) {
                        holder.readReceiptStatus.setText(String.format(view.getResources().getString(R.string.rc_read_receipt_status), data.getReadReceiptInfo().getRespondUserIdList().size()));
                    } else {
                        holder.readReceiptStatus.setText(String.format(view.getResources().getString(R.string.rc_read_receipt_status), 0));
                    }
                    holder.readReceiptStatus.setVisibility(View.VISIBLE);
                }
            }

            holder.nameView.setVisibility(View.GONE);

            holder.readReceiptRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RongIMClient.getInstance().sendReadReceiptRequest(data.getMessage(), new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            ReadReceiptInfo readReceiptInfo = data.getReadReceiptInfo();
                            if (readReceiptInfo == null) {
                                readReceiptInfo = new ReadReceiptInfo();
                                data.setReadReceiptInfo(readReceiptInfo);
                            }
                            readReceiptInfo.setIsReadReceiptMessage(true);
                            holder.readReceiptStatus.setText(String.format(view.getResources().getString(R.string.rc_read_receipt_status), 0));
                            holder.readReceiptRequest.setVisibility(View.GONE);
                            holder.readReceiptStatus.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            RLog.e(TAG, "sendReadReceiptRequest failed, errorCode = " + errorCode);
                        }
                    });
                }
            });

            holder.readReceiptStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemHandlerListener != null) {
                        mOnItemHandlerListener.onReadReceiptStateClick(data.getMessage());
                    }
                }
            });

            holder.rightIconView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (RongContext.getInstance().getConversationBehaviorListener() != null) {
                        UserInfo userInfo = null;
                        if (!TextUtils.isEmpty(data.getSenderUserId())) {
                            userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                            userInfo = userInfo == null ? (new UserInfo(data.getSenderUserId(), null, null)) : userInfo;
                        }
                        RongContext.getInstance().getConversationBehaviorListener().onUserPortraitClick(mContext, data.getConversationType(), userInfo);
                    }
                }
            });

            holder.rightIconView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    if (RongContext.getInstance().getConversationBehaviorListener() != null) {
                        UserInfo userInfo = null;
                        if (!TextUtils.isEmpty(data.getSenderUserId())) {
                            userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                            userInfo = userInfo == null ? (new UserInfo(data.getSenderUserId(), null, null)) : userInfo;
                        }
                        return RongContext.getInstance().getConversationBehaviorListener().onUserPortraitLongClick(mContext, data.getConversationType(), userInfo);
                    }

                    return true;
                }
            });

            if (!tag.showWarning())
                holder.warning.setVisibility(View.GONE);

//            holder.sentStatus.setVisibility(View.VISIBLE);

        } else {
            if (tag.showPortrait()) {
                holder.rightIconView.setVisibility(View.GONE);
                holder.leftIconView.setVisibility(View.VISIBLE);
            } else {
                holder.leftIconView.setVisibility(View.GONE);
                holder.rightIconView.setVisibility(View.GONE);
            }

            if (!tag.centerInHorizontal()) {
                setGravity(holder.layout, Gravity.LEFT);
                holder.contentView.containerViewLeft();
                holder.nameView.setGravity(Gravity.LEFT);

            } else {
                setGravity(holder.layout, Gravity.CENTER);
                holder.contentView.containerViewCenter();
                holder.nameView.setGravity(Gravity.CENTER_HORIZONTAL);
                holder.contentView.setBackgroundColor(Color.TRANSPARENT);
            }

            holder.progressBar.setVisibility(View.GONE);
            holder.warning.setVisibility(View.GONE);
            holder.readReceipt.setVisibility(View.GONE);
            holder.readReceiptRequest.setVisibility(View.GONE);
            holder.readReceiptStatus.setVisibility(View.GONE);

            holder.nameView.setVisibility(View.VISIBLE);

            if (data.getConversationType() == Conversation.ConversationType.PRIVATE
                    || !tag.showSummaryWithName()
                    || data.getConversationType() == Conversation.ConversationType.PUBLIC_SERVICE
                    || data.getConversationType() == Conversation.ConversationType.APP_PUBLIC_SERVICE) {

                holder.nameView.setVisibility(View.GONE);
            } else {
                UserInfo userInfo = null;
                if (data.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE)
                        && data.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {

                    if (data.getUserInfo() != null) {
                        userInfo = data.getUserInfo();
                    } else {
                        if (data.getMessage() != null && data.getMessage().getContent() != null) {
                            userInfo = data.getMessage().getContent().getUserInfo();
                        }
                    }
                    if (userInfo != null) {
                        holder.nameView.setText(userInfo.getName());
                    } else {
                        holder.nameView.setText(data.getSenderUserId());
                    }
                } else if (data.getConversationType() == Conversation.ConversationType.GROUP) {
                    GroupUserInfo groupUserInfo = RongUserInfoManager.getInstance().getGroupUserInfo(data.getTargetId(), data.getSenderUserId());
                    if (groupUserInfo != null) {
                        holder.nameView.setText(groupUserInfo.getNickname());
                    } else {
                        userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                        if (userInfo == null)
                            holder.nameView.setText(data.getSenderUserId());
                        else
                            holder.nameView.setText(userInfo.getName());
                    }
                } else {
                    userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                    if (userInfo == null)
                        holder.nameView.setText(data.getSenderUserId());
                    else
                        holder.nameView.setText(userInfo.getName());
                }
            }

            holder.leftIconView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (RongContext.getInstance().getConversationBehaviorListener() != null) {
                        UserInfo userInfo = null;
                        if (!TextUtils.isEmpty(data.getSenderUserId())) {
                            userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                            userInfo = userInfo == null ? (new UserInfo(data.getSenderUserId(), null, null)) : userInfo;
                        }
                        RongContext.getInstance().getConversationBehaviorListener().onUserPortraitClick(mContext, data.getConversationType(), userInfo);
                    }
                }
            });
        }

        holder.leftIconView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                UserInfo userInfo = null;
                if (!TextUtils.isEmpty(data.getSenderUserId())) {
                    userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                    userInfo = userInfo == null ? (new UserInfo(data.getSenderUserId(), null, null)) : userInfo;
                }
                if (RongContext.getInstance().getConversationBehaviorListener() == null ||
                        !RongContext.getInstance().getConversationBehaviorListener().onUserPortraitLongClick(mContext, data.getConversationType(), userInfo)) {
                    if (RongContext.getInstance().getResources().getBoolean(R.bool.rc_enable_mentioned_message)
                            && (data.getConversationType().equals(Conversation.ConversationType.GROUP)
                            || data.getConversationType().equals(Conversation.ConversationType.DISCUSSION))) {
                        RongMentionManager.getInstance().mentionMember(data.getConversationType(), data.getTargetId(), data.getSenderUserId());
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return RongContext.getInstance().getConversationBehaviorListener().onUserPortraitLongClick(mContext, data.getConversationType(), userInfo);
                }
            }
        });


        if (holder.rightIconView.getVisibility() == View.VISIBLE) {
            UserInfo userInfo;
            Uri portrait;
            if (data.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE)
                    && data.getUserInfo() != null && data.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
                userInfo = data.getUserInfo();
                portrait = userInfo.getPortraitUri();
                if (portrait != null) {
                    holder.rightIconView.setAvatar(portrait.toString(), 0);
                }
            } else if ((data.getConversationType().equals(Conversation.ConversationType.PUBLIC_SERVICE)
                    || data.getConversationType().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE))
                    && data.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {

                userInfo = data.getUserInfo();
                if (userInfo != null) {
                    portrait = userInfo.getPortraitUri();
                    if (portrait != null) {
                        holder.leftIconView.setAvatar(portrait.toString(), 0);
                    }
                } else {
                    PublicServiceProfile publicServiceProfile;

                    ConversationKey mKey = ConversationKey.obtain(data.getTargetId(), data.getConversationType());
                    publicServiceProfile = RongContext.getInstance().getPublicServiceInfoFromCache(mKey.getKey());
                    portrait = publicServiceProfile.getPortraitUri();

                    if (portrait != null) {
                        holder.rightIconView.setAvatar(portrait.toString(), 0);
                    }
                }
            } else if (!TextUtils.isEmpty(data.getSenderUserId())) {
                userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());

                if (userInfo != null && userInfo.getPortraitUri() != null) {
                    holder.rightIconView.setAvatar(userInfo.getPortraitUri().toString(), 0);
                }
            }
        } else if (holder.leftIconView.getVisibility() == View.VISIBLE) {
            UserInfo userInfo = null;
            Uri portrait = null;
            if (data.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE)
                    && data.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
                if (data.getUserInfo() != null) {
                    userInfo = data.getUserInfo();
                } else {
                    if (data.getMessage() != null && data.getMessage().getContent() != null) {
                        userInfo = data.getMessage().getContent().getUserInfo();
                    }
                }
                if (userInfo != null) {
                    portrait = userInfo.getPortraitUri();
                    if (portrait != null) {
                        holder.leftIconView.setAvatar(portrait.toString(), 0);
                    }
                }
            } else if ((data.getConversationType().equals(Conversation.ConversationType.PUBLIC_SERVICE)
                    || data.getConversationType().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE))
                    && data.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {

                userInfo = data.getUserInfo();
                if (userInfo != null) {
                    portrait = userInfo.getPortraitUri();
                    if (portrait != null) {
                        holder.leftIconView.setAvatar(portrait.toString(), 0);
                    }
                } else {
                    PublicServiceProfile publicServiceProfile;
                    ConversationKey mKey = ConversationKey.obtain(data.getTargetId(), data.getConversationType());
                    publicServiceProfile = RongContext.getInstance().getPublicServiceInfoFromCache(mKey.getKey());
                    if (publicServiceProfile != null && publicServiceProfile.getPortraitUri() != null) {
                        holder.leftIconView.setAvatar(publicServiceProfile.getPortraitUri().toString(), 0);
                    }
                }
            } else if (!TextUtils.isEmpty(data.getSenderUserId())) {
                userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());

                if (userInfo != null && userInfo.getPortraitUri() != null) {
                    holder.leftIconView.setAvatar(userInfo.getPortraitUri().toString(), 0);
                }
            }
        }

        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (RongContext.getInstance().getConversationBehaviorListener() != null) {
                        if (RongContext.getInstance().getConversationBehaviorListener().onMessageClick(mContext, v, data.getMessage())) {
                            return;
                        }
                    }

                    IContainerItemProvider.MessageProvider provider;//= RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
                    if (getNeedEvaluate(data))
                        provider = RongContext.getInstance().getEvaluateProvider();
                    else
                        provider = RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
                    if (provider != null)
                        provider.onItemClick(v, position, data.getContent(), data);
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (RongContext.getInstance().getConversationBehaviorListener() != null)
                        if (RongContext.getInstance().getConversationBehaviorListener().onMessageLongClick(mContext, v, data.getMessage()))
                            return true;

                    IContainerItemProvider.MessageProvider provider;//= RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
                    if (getNeedEvaluate(data))
                        provider = RongContext.getInstance().getEvaluateProvider();
                    else
                        provider = RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
                    if (provider != null)
                        provider.onItemLongClick(v, position, data.getContent(), data);
                    return true;
                }
            });
        }

        holder.warning.setOnClickListener(new DebouncedOnClickListener() {
            @Override
            public void onDebouncedClick(View view) {
                if (mOnItemHandlerListener != null) {
                    mOnItemHandlerListener.onWarningViewClick(position, data.getMessage(), view);
                }
            }
        });

        if (tag.hide()) {
            holder.time.setVisibility(View.GONE);
            return;
        }

        if (!timeGone) {
            String time = RongDateUtils.getConversationFormatDate(data.getSentTime(), view.getContext());
            holder.time.setText(time);
            if (position == 0) {
                holder.time.setVisibility(View.VISIBLE);
            } else {
                UIMessage pre = getItem(position - 1);
                if (RongDateUtils.isShowChatTime(data.getSentTime(), pre.getSentTime(), 180)) {
                    holder.time.setVisibility(View.VISIBLE);
                } else {
                    holder.time.setVisibility(View.GONE);
                }
            }
        }
    }

    private void setGravity(View view, int gravity) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = gravity;
    }

    public void setEvaluateForRobot(boolean needEvaluate) {
        evaForRobot = needEvaluate;
    }

    public void setRobotMode(boolean robotMode) {
        this.robotMode = robotMode;
    }
}
