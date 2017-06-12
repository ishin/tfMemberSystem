package com.tianfangIMS.im.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tianfangIMS.im.R;

import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

/**
 * Created by LianMengYu on 2017/2/18.
 */

public class ConversationListDynamicActivtiy extends ConversationListFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conversation, container, false);
        GetConversation();
        Log.e("eeeeeeee","会话界面2是否执行");
        return view;
    }
//
//    @Override
//    public void getConversationList(Conversation.ConversationType[] conversationTypes, IHistoryDataResultCallback<List<Conversation>> callback) {
//        super.getConversationList(conversationTypes, callback);
//        RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>(){
//            @Override
//            public void onError(RongIMClient.ErrorCode errorCode) {
//
//            }
//
//            @Override
//            public void onSuccess(List<Conversation> conversations) {
//                if (callback != null) {
//                    callback.onResult(conversations);
//
//                }
//            }
//        });
//    }

    private void GetConversation(){
        ConversationListFragment fragment = new ConversationListFragment();
        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话，该会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")
                .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//系统
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "true")//设置群组会话，该会话非聚合显示
                .build();
        fragment.setUri(uri);  //设置 ConverssationListFragment 的显示属性
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.rong_content, fragment);
        transaction.commit();
    }

}
