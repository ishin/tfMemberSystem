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

import io.rong.common.RLog;
import io.rong.imkit.R;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.utils.MessageProviderUserInfoHelper;
import io.rong.imlib.model.UserInfo;
import io.rong.message.DiscussionNotificationMessage;

@ProviderTag(messageContent = DiscussionNotificationMessage.class, showPortrait = false, centerInHorizontal = true, showSummaryWithName = false)
public class DiscussionNotificationMessageItemProvider extends IContainerItemProvider.MessageProvider<DiscussionNotificationMessage> {
    private final static String TAG = "DiscussionNotificationMessageItemProvider";

    /**
     * 1、加入讨论组。
     */
    private final static int DISCUSSION_ADD_MEMBER = 1;
    /**
     * 2、退出讨论组。
     */
    private final static int DISCUSSION_EXIT = 2;
    /**
     * 3、讨论组改名。
     */
    private final static int DISCUSSION_RENAME = 3;
    /**
     * 4、讨论组群主T人。
     */
    private final static int DISCUSSION_REMOVE = 4;
    /**
     * 5、成员邀请开关。
     */
    private final static int DISCUSSION_MEMBER_INVITE = 5;

    public DiscussionNotificationMessageItemProvider() {
        RongContext.getInstance().getEventBus().register(this);
    }

    @Override
    public void bindView(View v, int position, DiscussionNotificationMessage content, UIMessage message) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();
        Spannable spannable = getContentSummary(content);

        if (spannable != null && spannable.length() > 0) {
            viewHolder.contentTextView.setVisibility(View.VISIBLE);
            viewHolder.contentTextView.setText(spannable);
        } else {
            viewHolder.contentTextView.setVisibility(View.GONE);
        }
    }


    @Override
    public Spannable getContentSummary(DiscussionNotificationMessage data) {

        if (data == null) {
            RLog.e(TAG, "getContentSummary DiscussionNotificationMessage is null;");
            return new SpannableString("");
        } else {
            RLog.i(TAG, "getContentSummary call getContentSummary()  method ");
        }

        return new SpannableString(getWrapContent(RongContext.getInstance(), data));
    }

    @Override
    public void onItemClick(View view, int position, DiscussionNotificationMessage content, UIMessage message) {

    }


    @Override
    public View newView(Context context, ViewGroup group) {

        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_discussion_notification_message, null);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.contentTextView = (TextView) view.findViewById(R.id.rc_msg);
        viewHolder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
        view.setTag(viewHolder);

        return view;
    }


    private static class ViewHolder {
        TextView contentTextView;
    }

    /**
     * 根据操作类型来拼讨论通知信息。
     *
     * @param discussionNotificationMessage 讨论通知消息。
     * @return 拼完后可直接显示的消息。
     */
    private final String getWrapContent(Context context, DiscussionNotificationMessage discussionNotificationMessage) {
        if (discussionNotificationMessage == null)
            return "";

        String[] operatedUserIds = null;
        String extension = discussionNotificationMessage.getExtension();
        String operatorId = discussionNotificationMessage.getOperator();
        String currentUserId = "";
        String content = "";
        int operatedUserIdsLength = 0;

        if (!TextUtils.isEmpty(extension)) {

            if (extension.indexOf(",") != -1) {
                operatedUserIds = extension.split(",");
            } else {
                operatedUserIds = new String[] {extension};
            }
            operatedUserIdsLength = operatedUserIds.length;
        }

        currentUserId = RongIM.getInstance().getCurrentUserId();

        if (TextUtils.isEmpty(currentUserId))
            return "";

        int operatorType = discussionNotificationMessage.getType();

        /**
         * 1、加入讨论组。
         * 2、退出讨论组。
         * 3、讨论组改名。
         * 4、讨论组群主T人。
         * 5、成员邀请开关。
         */
        switch (operatorType) {

            /**
             * 加入讨论组。
             * 1、[xxx]邀请了[n]位成员加入了讨论组。
             * 2、[xxx]邀请[xxx]加入了讨论组。
             */
            case DISCUSSION_ADD_MEMBER:

                if (operatedUserIds != null) {

                    if (currentUserId.equals(operatorId)) {//操作人是自己
                        String you = context.getResources().getString(R.string.rc_discussion_nt_msg_for_you);

                        if (operatedUserIdsLength == 1) {//你邀请了[n]位成员加入了讨论组

                            String userId = operatedUserIds[0];
                            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(userId);

                            if (userInfo != null) {
                                String formatString = context.getResources().getString(R.string.rc_discussion_nt_msg_for_added);
                                content = String.format(formatString, you, userInfo.getName());
                            } else {
                                MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, userId);
                            }

                        } else {//你邀请了xxx加入了讨论组
                            String formatString = context.getResources().getString(R.string.rc_discussion_nt_msg_for_add);
                            content = String.format(formatString, you, operatedUserIdsLength);
                        }

                    } else {//操作人是别人

                        if (operatedUserIdsLength == 1) {//xxx邀请了[n]位成员加入了讨论组
                            String userId = operatedUserIds[0];
                            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(userId);
                            UserInfo operator = RongUserInfoManager.getInstance().getUserInfo(operatorId);

                            if (userInfo != null && operator != null) {
                                String formatString = context.getResources().getString(R.string.rc_discussion_nt_msg_for_added);
                                content = String.format(formatString, operator.getName(), userInfo.getName());
                            } else {

                                if (userInfo == null)
                                    MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, userId);

                                if (operator == null)
                                    MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
                            }

                        } else {//xxx邀请了xxx加入了讨论组
                            UserInfo operator = RongUserInfoManager.getInstance().getUserInfo(operatorId);

                            if (operator != null) {
                                String formatString = context.getResources().getString(R.string.rc_discussion_nt_msg_for_add);
                                content = String.format(formatString, operator.getName(), operatedUserIdsLength);
                            } else {
                                MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
                            }
                        }
                    }
                }

                break;

            /**
             * 退出讨论组。
             * 1、xxx退出了讨论组。
             */
            case DISCUSSION_EXIT:

                UserInfo operator = RongUserInfoManager.getInstance().getUserInfo(operatorId);

                if (operator != null) {
                    String formatString = context.getResources().getString(R.string.rc_discussion_nt_msg_for_exit);
                    content = String.format(formatString, operator.getName());
                } else {
                    MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
                }

                break;

            /**
             * 讨论组改名。
             * 1、%1$s修改讨论组名称为%2$s
             */
            case DISCUSSION_RENAME:

                if (currentUserId.equals(operatorId)) {
                    String you = context.getResources().getString(R.string.rc_discussion_nt_msg_for_you);
                    String formatString = context.getResources().getString(R.string.rc_discussion_nt_msg_for_rename);
                    content = String.format(formatString, you, extension);
                } else {
                    UserInfo operatorUserInfo = RongUserInfoManager.getInstance().getUserInfo(operatorId);

                    if (operatorUserInfo != null) {
                        String formatString = context.getResources().getString(R.string.rc_discussion_nt_msg_for_rename);
                        content = String.format(formatString, operatorUserInfo.getName(), extension);
                    } else {
                        MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
                    }
                }

                break;

            /**
             * 讨论组群主T人。
             * 1、你被 xxx 移出了讨论组。
             * 2、xxx 被 xxx 移出了讨论组"。
             */
            case DISCUSSION_REMOVE:
                String operatedUserId = operatedUserIds[0];

                if (currentUserId.equals(operatorId)) {//xxx被你移出了讨论组
                    UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(operatedUserId);

                    if (userInfo != null) {
                        String you = context.getResources().getString(R.string.rc_discussion_nt_msg_for_you);
                        String formatString = context.getResources().getString(R.string.rc_discussion_nt_msg_for_who_removed);
                        content = String.format(formatString, userInfo.getName(), you);
                    } else {
                        MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
                    }
                } else {//1,你被xxx称出了讨论组  2,xxx 被xxx 移出了讨论组

                    if (currentUserId.equals(operatedUserId)) {//被操作者是自己
                        UserInfo operatorUserInfo = RongUserInfoManager.getInstance().getUserInfo(operatorId);

                        if (operatorUserInfo != null) {
                            String formatString = context.getResources().getString(R.string.rc_discussion_nt_msg_for_removed);
                            content = String.format(formatString, operatorUserInfo.getName());
                        } else {
                            MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
                        }

                    } else {

                        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(operatedUserId);
                        UserInfo operatorUserInfo = RongUserInfoManager.getInstance().getUserInfo(operatorId);

                        if (userInfo != null && operatorUserInfo != null) {
                            String formatString = context.getResources().getString(R.string.rc_discussion_nt_msg_for_who_removed);
                            content = String.format(formatString, userInfo.getName(), operatorUserInfo.getName());
                        } else {

                            if (operatorUserInfo == null)
                                MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);

                            if (userInfo == null)
                                MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatedUserId);
                        }
                    }

                }

                break;

            /**
             * 成员邀请开关。
             */
            case DISCUSSION_MEMBER_INVITE:

                if (currentUserId.equals(operatorId)) {
                    String you = context.getResources().getString(R.string.rc_discussion_nt_msg_for_you);

                    if ("1".equals(extension)) {//关闭
                        String closeFormat = context.getResources().getString(R.string.rc_discussion_nt_msg_for_is_open_invite_close);
                        content = String.format(closeFormat, you);
                    } else if ("0".equals(extension)) {//打开
                        String openFormat = context.getResources().getString(R.string.rc_discussion_nt_msg_for_is_open_invite_open);
                        content = String.format(openFormat, you);
                    }
                } else {//开放
                    UserInfo operatorUserInfo = RongUserInfoManager.getInstance().getUserInfo(operatorId);

                    if (operatorUserInfo != null) {
                        if ("1".equals(extension)) {//关闭
                            String closeFormat = context.getResources().getString(R.string.rc_discussion_nt_msg_for_is_open_invite_close);
                            content = String.format(closeFormat, operatorUserInfo.getName());
                        } else if ("0".equals(extension)) {//打开
                            String openFormat = context.getResources().getString(R.string.rc_discussion_nt_msg_for_is_open_invite_open);
                            content = String.format(openFormat, operatorUserInfo.getName());
                        }
                    } else {
                        MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
                    }
                }

                break;

            default:
                content = "";
                break;
        }

        RLog.i(TAG, "content return " + content);

        return content;
    }

    @Override
    public void onItemLongClick(View view, int position, DiscussionNotificationMessage content, final UIMessage message) {
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

    public void onEventMainThread(final UserInfo userInfo) {
        if (userInfo.getName() == null)
            return;

        if (MessageProviderUserInfoHelper.getInstance().isCacheUserId(userInfo.getUserId())) {
            MessageProviderUserInfoHelper.getInstance().notifyMessageUpdate(userInfo.getUserId());
        }
    }


}
