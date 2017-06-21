package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import io.rong.imkit.widget.adapter.BaseAdapter;
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
    private TextView tv_search_cencal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchchattingdetail_layout);
        setTitle("搜索聊天记录");
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

        tv_search_cencal = (TextView) this.findViewById(R.id.tv_search_cencal);
        tv_search_cencal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_search.getText().toString())) {
                    finish();
                } else {
                    et_search.getText().clear();
                }
            }
        });

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
                                }
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {

                            }
                        });

                RongIMClient.getInstance().searchMessages(conversation.getConversationType(),
                        conversation.getTargetId(), mFilterString, 30, 0, new RongIMClient.ResultCallback<List<Message>>() {
                            @Override
                            public void onSuccess(List<Message> messages) {
                                mMessageShowCount = messages.size();
                                mMessages = messages;
                                if (mMessages.size() == 0) {
                                    lv_privatechat_search.setVisibility(View.GONE);
                                    mSearchNoResultsTextView.setVisibility(View.GONE);
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

        @Override
        protected View newView(Context context, int position, ViewGroup group) {
            return null;
        }

        @Override
        protected void bindView(View v, int position, Object data) {
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
            RongIM.getInstance().startGroupChat(mContext, mResult.getId(), mResult.getTitle(),mMessages.get(position).getMessageId(),0);

        } else if (type == 1) {
            RongIM.getInstance().startPrivateChat(mContext, mResult.getId(), mResult.getTitle(),mMessages.get(position).getMessageId(),0);
            Log.e("asdljasld","duibuxiaox:"+mMessages.get(position).getMessageId());
        }
    }
}
