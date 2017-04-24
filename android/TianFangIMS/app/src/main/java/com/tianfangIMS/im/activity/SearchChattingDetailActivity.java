package com.tianfangIMS.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.SealSearchConversationResult;
import com.tianfangIMS.im.pinyin.CharacterParser;
import com.tianfangIMS.im.view.FrameView;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.SearchConversationResult;

/**
 * Created by LianMengYu on 2017/3/11.
 */
public class SearchChattingDetailActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private SealSearchConversationResult mResult;
    private String mFilterString;
    private List<Message> mMessages;
    private Message mLastMessage;
    private EditText et_search;
    private ListView lv_privatechat_search;
    private int mMatchCount;
    private TextView mSearchNoResultsTextView;
    private int mMessageShowCount;
    private List<Message> mAdapterMessages;
    private ChattingRecordsAdapter mAdapter;
    private int type;//0为群组，1为单聊
    private LinearLayout no_result_chatting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchchattingdetail_layout);
        Intent intent = getIntent();
        mFilterString = intent.getStringExtra("filterString");
        mResult = intent.getParcelableExtra("searchConversationResult");
        type = intent.getIntExtra("flag", -1);
        init();
        initData();
    }

    private void init() {
        et_search = (EditText) this.findViewById(R.id.et_search);
        lv_privatechat_search = (ListView) this.findViewById(R.id.lv_privatechat_search);
        mSearchNoResultsTextView = (TextView) this.findViewById(R.id.ac_tv_search_no_results);
        no_result_chatting = (LinearLayout)this.findViewById(R.id.no_result_chatting);
        lv_privatechat_search.setOnItemClickListener(this);
    }

    private void initData() {
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFilterString = s.toString();
                final Conversation conversation = mResult.getConversation();
                RongIMClient.getInstance().searchConversations(mFilterString, new Conversation.ConversationType[]{conversation.getConversationType()},
                        new String[]{"RC:TxtMsg", "RC:ImgTextMsg", "RC:FileMsg"},
                        new RongIMClient.ResultCallback<List<SearchConversationResult>>() {
                            @Override
                            public void onSuccess(List<SearchConversationResult> SearchConversationResult) {
                                for (SearchConversationResult mResult : SearchConversationResult) {
                                    mMatchCount = mResult.getMatchCount();
                                    if (mResult.getMatchCount() == 0) {
                                        lv_privatechat_search.setVisibility(View.GONE);
                                        mSearchNoResultsTextView.setVisibility(View.VISIBLE);
//                                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
//                                        spannableStringBuilder.append("没有搜到");
//                                        SpannableStringBuilder colorFilterStr = new SpannableStringBuilder(mFilterString);
//                                        colorFilterStr.setSpan(new ForegroundColorSpan(Color.parseColor("#0099ff")), 0, mFilterString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//                                        spannableStringBuilder.append(colorFilterStr);
//                                        spannableStringBuilder.append("相关信息");
//                                        mSearchNoResultsTextView.setText(spannableStringBuilder);
                                        no_result_chatting.setVisibility(View.VISIBLE);
                                    } else {
//                                        mSearchNoResultsTextView.setVisibility(View.GONE);
//                                        lv_privatechat_search.setVisibility(View.VISIBLE);
//                                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
//                                        spannableStringBuilder.append(getString(R.string.ac_search_chat_detail, mResult.getMatchCount()));
//                                        SpannableStringBuilder colorFilterStr = new SpannableStringBuilder(mFilterString);
//                                        colorFilterStr.setSpan(new ForegroundColorSpan(Color.parseColor("#0099ff")), 0, mFilterString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//                                        spannableStringBuilder.append(colorFilterStr);
//                                        spannableStringBuilder.append("相关的聊天记录");
//                                        mSearchNoResultsTextView.setText(spannableStringBuilder);
                                        no_result_chatting.setVisibility(View.GONE);
                                    }
                                }
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                                Log.e("看看打印结果:", "打印失败------:" + errorCode);
                            }
                        });

                RongIMClient.getInstance().searchMessages(conversation.getConversationType(),
                        conversation.getTargetId(), mFilterString, 50, 0, new RongIMClient.ResultCallback<List<Message>>() {
                            @Override
                            public void onSuccess(List<Message> messages) {
                                mMessageShowCount = messages.size();
                                mMessages = messages;
                                if (mMessages.size() == 0) {
                                    lv_privatechat_search.setVisibility(View.GONE);
                                    mSearchNoResultsTextView.setVisibility(View.VISIBLE);
//                                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
//                                    spannableStringBuilder.append("没有搜到");
//                                    SpannableStringBuilder colorFilterStr = new SpannableStringBuilder(mFilterString);
//                                    colorFilterStr.setSpan(new ForegroundColorSpan(Color.parseColor("#0099ff")), 0, mFilterString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//                                    spannableStringBuilder.append(colorFilterStr);
//                                    spannableStringBuilder.append("相关信息");
//                                    mSearchNoResultsTextView.setText(spannableStringBuilder);
                                    no_result_chatting.setVisibility(View.VISIBLE);
                                } else {
                                    mSearchNoResultsTextView.setVisibility(View.GONE);
                                    lv_privatechat_search.setVisibility(View.VISIBLE);
                                    no_result_chatting.setVisibility(View.GONE);
                                    mAdapterMessages = messages;
                                    mAdapter = new ChattingRecordsAdapter();
                                    lv_privatechat_search.setAdapter(mAdapter);
                                    mLastMessage = messages.get(messages.size() - 1);
                                }
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode e) {
                                if (mFilterString.equals("")) {
                                    lv_privatechat_search.setVisibility(View.GONE);
                                    mSearchNoResultsTextView.setVisibility(View.GONE);
                                }
                            }
                        });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private class ChattingRecordsAdapter extends BaseAdapter {
        public ChattingRecordsAdapter() {
        }

        @Override
        public int getCount() {
            if (mAdapterMessages != null) {
                return mAdapterMessages.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mAdapterMessages == null)
                return null;

            if (position >= mAdapterMessages.size())
                return null;

            return mAdapterMessages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Viewholder viewholder = null;
            Message message = (Message) getItem(position);
            if (convertView == null) {
                viewholder = new Viewholder();
                convertView = View.inflate(getBaseContext(), R.layout.searchchatting_item, null);
                viewholder.img = (ImageView) convertView.findViewById(R.id.iv_seacchchatting_photo);
                viewholder.name = (TextView) convertView.findViewById(R.id.tv_seacchchatting_Name);
                viewholder.chat = (TextView) convertView.findViewById(R.id.tv_seacchchatting_Txt);
                viewholder.fv_searchchat_index = (FrameView) convertView.findViewById(R.id.fv_searchchat_index);
                if (type == 1) {
                    viewholder.fv_searchchat_index.setVisibility(View.GONE);
                }
                convertView.setTag(viewholder);
            } else {
                viewholder = (Viewholder) convertView.getTag();
            }
            if (mResult != null) {
                String title = mResult.getTitle();
                String portraitUri = mResult.getPortraitUri();
                Picasso.with(getBaseContext())
                        .load(portraitUri)
                        .resize(80, 80)
                        .into(viewholder.img);
                viewholder.name.setText(title);
                viewholder.chat.setText(CharacterParser.getInstance().getColoredChattingRecord(mFilterString, message.getContent()));
                if (type == 0) {
                    viewholder.fv_searchchat_index.setText(title.substring(1, 2));
                }
            }
            return convertView;
        }

        class Viewholder {
            ImageView img;
            TextView name;
            TextView chat;
            FrameView fv_searchchat_index;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (type == 0) {
            RongIM.getInstance().startGroupChat(mContext, mResult.getId(), mResult.getTitle());
        } else if (type == 1) {
            RongIM.getInstance().startPrivateChat(mContext, mResult.getId(), mResult.getTitle());
        }
    }
}
