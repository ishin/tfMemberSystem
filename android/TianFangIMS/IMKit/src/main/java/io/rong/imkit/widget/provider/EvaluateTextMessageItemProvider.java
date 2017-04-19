package io.rong.imkit.widget.provider;

import android.content.Context;
import android.content.Intent;
import android.text.ClipboardManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.R;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongKitIntent;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.widget.AutoLinkTextView;
import io.rong.imkit.widget.ILinkClickListener;
import io.rong.imkit.widget.LinkTextViewMovementMethod;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

public class EvaluateTextMessageItemProvider extends IContainerItemProvider.MessageProvider<TextMessage> {

    private static class ViewHolder {
        AutoLinkTextView message;
        TextView tv_prompt;
        ImageView iv_yes;
        ImageView iv_no;
        ImageView iv_complete;
        RelativeLayout layout_praise;
        boolean longClick;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_text_message_evaluate, null);

        ViewHolder holder = new ViewHolder();
        holder.message = (AutoLinkTextView) view.findViewById(R.id.evaluate_text);
        holder.tv_prompt = (TextView)view.findViewById(R.id.tv_prompt);
        holder.iv_yes = (ImageView)view.findViewById(R.id.iv_yes);
        holder.iv_no = (ImageView)view.findViewById(R.id.iv_no);
        holder.iv_complete = (ImageView)view.findViewById(R.id.iv_complete);
        holder.layout_praise = (RelativeLayout)view.findViewById(R.id.layout_praise);
        view.setTag(holder);
        return view;
    }

    @Override
    public Spannable getContentSummary(TextMessage data) {
        if (data == null)
            return null;

        String content = data.getContent();
        if (content != null) {
            if (content.length() > 100) {
                content = content.substring(0, 100);
            }
            return new SpannableString(AndroidEmoji.ensure(content));
        }
        return null;
    }

    @Override
    public void onItemClick(View view, int position, TextMessage content, UIMessage message) {

    }

    @Override
    public void onItemLongClick(final View view, int position, final TextMessage content, final UIMessage message) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.longClick = true;
        if (view instanceof TextView) {
            CharSequence text = ((TextView) view).getText();
            if (text != null && text instanceof Spannable)
                Selection.removeSelection((Spannable) text);
        }
        String[] items;

        items = new String[] {view.getContext().getResources().getString(R.string.rc_dialog_item_message_copy), view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete)};

        OptionsPopupDialog.newInstance(view.getContext(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
            @Override
            public void onOptionsItemClicked(int which) {
                if (which == 0) {
                    @SuppressWarnings("deprecation")
                    ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(((TextMessage) content).getContent());
                } else if (which == 1) {
                    RongIM.getInstance().getRongIMClient().deleteMessages(new int[] {message.getMessageId()}, null);
                }
            }
        }).show();
    }

    @Override
    public void bindView(final View v, final int position, final TextMessage content, final UIMessage data) {
        final ViewHolder holder = (ViewHolder) v.getTag();
        if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            v.setBackgroundResource(R.drawable.rc_ic_bubble_right);
        } else {
            v.setBackgroundResource(R.drawable.rc_ic_bubble_left);
        }
        if (data.getEvaluated())
        {
            holder.iv_yes.setVisibility(View.GONE);
            holder.iv_no.setVisibility(View.GONE);
            holder.iv_complete.setVisibility(View.VISIBLE);
            holder.tv_prompt.setText("感谢您的评价");
        }
        else
        {
            holder.iv_yes.setVisibility(View.VISIBLE);
            holder.iv_no.setVisibility(View.VISIBLE);
            holder.iv_complete.setVisibility(View.GONE);
            holder.tv_prompt.setText("您对我的回答");
        }
        holder.iv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String extra = ((TextMessage) data.getContent()).getExtra();
                String knowledgeId = "";
                if (!TextUtils.isEmpty(extra)) {
                    try {
                        JSONObject jsonObj = new JSONObject(extra);
                        knowledgeId = jsonObj.optString("sid");
                    } catch (JSONException e) {

                    }
                }
                RongIMClient.getInstance().evaluateCustomService(data.getSenderUserId(), true, knowledgeId);
                holder.iv_complete.setVisibility(View.VISIBLE);
                holder.iv_yes.setVisibility(View.GONE);
                holder.iv_no.setVisibility(View.GONE);
                holder.tv_prompt.setText("感谢您的评价");
                data.setEvaluated(true);
            }
        });

        holder.iv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String extra = ((TextMessage) data.getContent()).getExtra();
                String knowledgeId = "";
                if (!TextUtils.isEmpty(extra)) {
                    try {
                        JSONObject jsonObj = new JSONObject(extra);
                        knowledgeId = jsonObj.optString("sid");
                    } catch (JSONException e) {

                    }
                }
                RongIMClient.getInstance().evaluateCustomService(data.getSenderUserId(), false, knowledgeId);
                holder.iv_complete.setVisibility(View.VISIBLE);
                holder.iv_yes.setVisibility(View.GONE);
                holder.iv_no.setVisibility(View.GONE);
                holder.tv_prompt.setText("感谢您的评价");
                data.setEvaluated(true);
            }
        });
        final TextView textView = holder.message;
        if (data.getTextMessageContent() != null) {
            int len = data.getTextMessageContent().length();
            if (v.getHandler() != null && len > 500) {
                v.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(data.getTextMessageContent());
                    }
                }, 50);
            } else {
                textView.setText(data.getTextMessageContent());
            }
        }

        holder.message.setClickable(true);
        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        holder.message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v1) {
                onItemLongClick(v, position, content, data);
                return false;
            }
        });

        holder.message.setMovementMethod(new LinkTextViewMovementMethod(new ILinkClickListener() {
            @Override
            public boolean onLinkClick(String link) {
                RongIM.ConversationBehaviorListener listener = RongContext.getInstance().getConversationBehaviorListener();
                boolean result = false;
                if (listener != null) {
                    result = listener.onMessageLinkClick(v.getContext(), link);
                }
                if (listener == null || !result) {
                    String str = link.toLowerCase();
                    if (str.startsWith("http") || str.startsWith("https")) {
                        Intent intent = new Intent(RongKitIntent.RONG_INTENT_ACTION_WEBVIEW);
                        intent.setPackage(v.getContext().getPackageName());
                        intent.putExtra("url", link);
                        v.getContext().startActivity(intent);
                        result = true;
                    }
                }
                return result;
            }
        }));
    }
}
