package io.rong.imkit.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import io.rong.imkit.R;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import io.rong.imkit.widget.adapter.SubConversationListAdapter;
import io.rong.imlib.model.Conversation;

public class SubConversationListFragment extends ConversationListFragment {
    private final static String TAG = "SubConversationListFragment";
    private ListView mList;
    private SubConversationListAdapter mAdapter;

    public void setAdapter(SubConversationListAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public ConversationListAdapter onResolveAdapter(Context context) {
        if (mAdapter == null) {
            mAdapter = new SubConversationListAdapter(context);
        }
        return mAdapter;
    }

    @Override
    public boolean getGatherState(Conversation.ConversationType conversationType) {
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mList = findViewById(view, R.id.rc_list);
        return view;
    }
}
