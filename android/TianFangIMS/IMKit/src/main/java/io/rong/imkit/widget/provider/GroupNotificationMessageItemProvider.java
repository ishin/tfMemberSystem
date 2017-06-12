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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import io.rong.imkit.R;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.GroupNotificationMessageData;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.model.UserInfo;
import io.rong.message.GroupNotificationMessage;

/**
 * Created by tiankui on 16/10/21.
 */
@ProviderTag(messageContent = GroupNotificationMessage.class, showPortrait = false, centerInHorizontal = true, showProgress = false, showSummaryWithName = false)
public class GroupNotificationMessageItemProvider extends IContainerItemProvider.MessageProvider<GroupNotificationMessage> {
    @Override
    public void bindView(View view, int i, GroupNotificationMessage groupNotificationMessage, UIMessage uiMessage) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        try {
            if (groupNotificationMessage != null && uiMessage != null) {
                if (groupNotificationMessage != null && groupNotificationMessage.getData() == null) {
                    return;
                }
                GroupNotificationMessageData data;
                try {
                    data = jsonToBean(groupNotificationMessage.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                String operation = groupNotificationMessage.getOperation();
                String operatorNickname = data.getOperatorNickname();
                String operatorUserId = groupNotificationMessage.getOperatorUserId();
                String currentUserId = RongIM.getInstance().getCurrentUserId();
                if (operatorNickname == null) {
                    UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(operatorUserId);
                    if (userInfo != null) {
                        operatorNickname = userInfo.getName();
                        if (operatorNickname == null) {
                            operatorNickname = groupNotificationMessage.getOperatorUserId();
                        }
                    }
                }
                List<String> memberList = data.getTargetUserDisplayNames();
                List<String> memberIdList = data.getTargetUserIds();
                String memberName = null;
                String memberUserId = null;
                Context context = RongContext.getInstance();
                if (memberIdList != null) {
                    if (memberIdList.size() == 1) {
                        memberUserId = memberIdList.get(0);
                    }
                }
                if (memberList != null) {
                    if (memberList.size() == 1) {
                        memberName = memberList.get(0);
                    } else if (memberIdList.size() > 1) {
                        StringBuilder sb = new StringBuilder();
                        for (String s : memberList) {
                            sb.append(s);
                            sb.append(context.getString(R.string.rc_item_divided_string));
                        }
                        String str = sb.toString();
                        memberName = str.substring(0, str.length() - 1);
                    }
                }

                if (!TextUtils.isEmpty(operation))
                    if (operation.equals("Add")) {
                        if (operatorUserId.equals(memberUserId)) {
                            viewHolder.contentTextView.setText(memberName + context.getString(R.string.rc_item_join_group));
                        } else {
                            String inviteName;
                            String invitedName;
                            if (!groupNotificationMessage.getOperatorUserId().equals(RongIM.getInstance().getCurrentUserId())) {
                                inviteName = operatorNickname;
                                invitedName = memberName;
                            } else {
                                inviteName = context.getString(R.string.rc_item_you);
                                invitedName = memberName;
                            }
                            viewHolder.contentTextView.setText(context.getString(R.string.rc_item_invitation, inviteName, invitedName));
                        }
                    } else if (operation.equals("Kicked")) {
                        String operator;
                        String kickedName;
                        if (memberIdList != null) {
                            for (String userId : memberIdList) {
                                if (currentUserId.equals(userId)) {
                                    operator = operatorNickname;
                                    kickedName = context.getString(R.string.rc_item_you);
                                    viewHolder.contentTextView.setText(context.getString(R.string.rc_item_remove_self, kickedName, operator));
                                } else {
                                    if (!operatorUserId.equals(currentUserId)) {
                                        operator = operatorNickname;
                                        kickedName = memberName;
                                    } else {
                                        operator = context.getString(R.string.rc_item_you);
                                        kickedName = memberName;
                                    }
                                    viewHolder.contentTextView.setText(context.getString(R.string.rc_item_remove_group_member, operator, kickedName));
                                }
                            }
                        }
                    } else if (operation.equals("Create")) {
                        GroupNotificationMessageData createGroupData = new GroupNotificationMessageData();
                        try {
                            createGroupData = jsonToBean(groupNotificationMessage.getData());
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        String name;
                        if (!operatorUserId.equals(currentUserId)) {
                            name = operatorNickname;
                        } else {
                            name = context.getString(R.string.rc_item_you);
                        }
                        viewHolder.contentTextView.setText(context.getString(R.string.rc_item_created_group, name));
                    } else if (operation.equals("Dismiss")) {
                        viewHolder.contentTextView.setText(operatorNickname + context.getString(R.string.rc_item_dismiss_groups));
                    } else if (operation.equals("Quit")) {
                        viewHolder.contentTextView.setText(operatorNickname + context.getString(R.string.rc_item_quit_groups));
                    } else if (operation.equals("Rename")) {
                        String operator;
                        String groupName;
                        if (!operatorUserId.equals(currentUserId)) {
                            operator = operatorNickname;
                            groupName = data.getTargetGroupName();
                        } else {
                            operator = context.getString(R.string.rc_item_you);
                            groupName = data.getTargetGroupName();
                        }
                        viewHolder.contentTextView.setText(context.getString(R.string.rc_item_change_group_name, operator, groupName));
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Spannable getContentSummary(GroupNotificationMessage groupNotificationMessage) {
        Context context = RongContext.getInstance();
        try {
            GroupNotificationMessageData data;
            if (groupNotificationMessage != null && groupNotificationMessage.getData() == null)
                return null;
            try {
                data = jsonToBean(groupNotificationMessage.getData());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            String operation = groupNotificationMessage.getOperation();
            String operatorNickname = data.getOperatorNickname();
            String operatorUserId = groupNotificationMessage.getOperatorUserId();
            String currentUserId = RongIM.getInstance().getCurrentUserId();

            if (operatorNickname == null) {
                UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(operatorUserId);
                if (userInfo != null) {
                    operatorNickname = userInfo.getName();
                }
                if (operatorNickname == null) {
                    operatorNickname = groupNotificationMessage.getOperatorUserId();
                }
            }
            List<String> memberList = data.getTargetUserDisplayNames();
            List<String> memberIdList = data.getTargetUserIds();
            String memberName = null;
            String memberUserId = null;
            if (memberIdList != null) {
                if (memberIdList.size() == 1) {
                    memberUserId = memberIdList.get(0);
                }
            }
            if (memberList != null) {
                if (memberList.size() == 1) {
                    memberName = memberList.get(0);
                } else if (memberIdList.size() > 1) {
                    StringBuilder sb = new StringBuilder();
                    for (String s : memberList) {
                        sb.append(s);
                        sb.append(context.getString(R.string.rc_item_divided_string));
                    }
                    String str = sb.toString();
                    memberName = str.substring(0, str.length() - 1);
                }
            }


            SpannableString spannableStringSummary = new SpannableString("");
            if (operation.equals("Add")) {
                try {
                    if (operatorUserId.equals(memberUserId)) {
                        spannableStringSummary = new SpannableString(operatorNickname + context.getString(R.string.rc_item_join_group));
                    } else {
                        String inviteName;
                        String invitedName;
                        if (!operatorUserId.equals(currentUserId)) {
                            inviteName = operatorNickname;
                            invitedName = memberName;
                        } else {
                            inviteName = context.getString(R.string.rc_item_you);
                            invitedName = memberName;
                        }
                        spannableStringSummary = new SpannableString(context.getString(R.string.rc_item_invitation, inviteName, invitedName));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (operation.equals("Kicked")) {
                String operator;
                String kickedName;
                if (memberIdList != null) {
                    for (String userId : memberIdList) {
                        if (currentUserId.equals(userId)) {
                            operator = operatorNickname;
                            kickedName = context.getString(R.string.rc_item_you);
                            spannableStringSummary = new SpannableString(context.getString(R.string.rc_item_remove_self, kickedName, operator));
                        } else {
                            if (!operatorUserId.equals(currentUserId)) {
                                operator = operatorNickname;
                                kickedName = memberName;
                            } else {
                                operator = context.getString(R.string.rc_item_you);
                                kickedName = memberName;
                            }
                            spannableStringSummary = new SpannableString(context.getString(R.string.rc_item_remove_group_member, operator, kickedName));
                        }
                    }
                }
            } else if (operation.equals("Create")) {
                String name;
                if (!operatorUserId.equals(currentUserId)) {
                    name = operatorNickname;
                } else {
                    name = context.getString(R.string.rc_item_you);
                }
                spannableStringSummary = new SpannableString(context.getString(R.string.rc_item_created_group, name));

            } else if (operation.equals("Dismiss")) {
                spannableStringSummary = new SpannableString(operatorNickname + context.getString(R.string.rc_item_dismiss_groups));
            } else if (operation.equals("Quit")) {
                spannableStringSummary = new SpannableString(operatorNickname + context.getString(R.string.rc_item_quit_groups));
            } else if (operation.equals("Rename")) {
                String operator;
                String groupName;
                if (!operatorUserId.equals(currentUserId)) {
                    operator = operatorNickname;
                    groupName = data.getTargetGroupName();
                } else {
                    operator = context.getString(R.string.rc_item_you);
                    groupName = data.getTargetGroupName();
                }
                spannableStringSummary = new SpannableString(context.getString(R.string.rc_item_change_group_name, operator, groupName));
            }

            return spannableStringSummary;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SpannableString(context.getString(R.string.rc_item_group_notification_summary));
    }

    @Override
    public void onItemClick(View view, int i, GroupNotificationMessage groupNotificationMessage, UIMessage uiMessage) {

    }

    @Override
    public void onItemLongClick(View view, int i, GroupNotificationMessage groupNotificationMessage, UIMessage uiMessage) {

    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_group_information_notification_message, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.contentTextView = (TextView) view.findViewById(R.id.rc_msg);
        viewHolder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
        view.setTag(viewHolder);
        return view;
    }

    private static class ViewHolder {
        TextView contentTextView;
    }


    private GroupNotificationMessageData jsonToBean(String data) {
        GroupNotificationMessageData dataEntity = new GroupNotificationMessageData();
        try {
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has("operatorNickname")) {
                dataEntity.setOperatorNickname(jsonObject.getString("operatorNickname"));
            }
            if (jsonObject.has("targetGroupName")) {
                dataEntity.setTargetGroupName(jsonObject.getString("targetGroupName"));
            }
            if (jsonObject.has("timestamp")) {
                dataEntity.setTimestamp(jsonObject.getLong("timestamp"));
            }
            if (jsonObject.has("targetUserIds")) {
                JSONArray jsonArray = jsonObject.getJSONArray("targetUserIds");
                for (int i = 0; i < jsonArray.length(); i++) {
                    dataEntity.getTargetUserIds().add(jsonArray.getString(i));
                }
            }
            if (jsonObject.has("targetUserDisplayNames")) {
                JSONArray jsonArray = jsonObject.getJSONArray("targetUserDisplayNames");
                for (int i = 0; i < jsonArray.length(); i++) {
                    dataEntity.getTargetUserDisplayNames().add(jsonArray.getString(i));
                }
            }
            if (jsonObject.has("oldCreatorId")) {
                dataEntity.setOldCreatorId(jsonObject.getString("oldCreatorId"));
            }
            if (jsonObject.has("oldCreatorName")) {
                dataEntity.setOldCreatorName(jsonObject.getString("oldCreatorName"));
            }
            if (jsonObject.has("newCreatorId")) {
                dataEntity.setNewCreatorId(jsonObject.getString("newCreatorId"));
            }
            if (jsonObject.has("newCreatorName")) {
                dataEntity.setNewCreatorName(jsonObject.getString("newCreatorName"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataEntity;
    }
}
