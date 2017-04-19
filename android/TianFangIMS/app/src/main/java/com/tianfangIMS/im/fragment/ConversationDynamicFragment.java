package com.tianfangIMS.im.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tianfangIMS.im.R;

import java.util.Locale;

import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.model.Conversation;

/**
 * Created by LianMengYu on 2017/2/9.
 */

public class ConversationDynamicFragment extends BaseFragment{
    private String mTargetId; //目标 Id
    private Conversation.ConversationType mConversationType; //会话类型
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conversation, container, false);
        GetConversation();
        Log.e("eeeeeeee","会话界面2是否执行");
        return view;
    }
    private void GetConversation(){
        Intent intent = getActivity().getIntent();
        mTargetId = intent.getData().getQueryParameter("targetId");
//        mTargetIds = intent.getData().getQueryParameter("targetIds");
        mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));

        ConversationFragment fragment = new ConversationFragment();
        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

        fragment.setUri(uri);

        /* 加载 ConversationFragment */
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.rong_content, fragment);
        transaction.commit();
    }
}
