package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.tianfangIMS.im.R;
import com.tianfangIMS.im.fragment.IntercomFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.model.Conversation;

/**
 * Created by LianMengYu on 2017/2/9.
 */

public class ConversationViewpagerActivity extends BaseActivity implements ViewPager.OnPageChangeListener, IUnReadMessageObserver {
    private static final String TAG = "ConversationViewpagerActivity";
    public static ViewPager mViewpager;
    private List<Fragment> mFragment = new ArrayList<>();
    private ConversationFragment conversationDynamicFragment = null;
    private IntercomFragment intercomFragment;
    /**
     * 会话类型
     */
    private Conversation.ConversationType mConversationType;
    /**
     * 对方id
     */
    private String mTargetId;
    private String mTargetIds;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_viewpager_layout);
        mContext = this;
        ConversationFragment fragment = new ConversationFragment();
        Intent intent = getIntent();

        mTargetId = intent.getData().getQueryParameter("targetId");
        mTargetIds = intent.getData().getQueryParameter("targetIds");
        mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();
        fragment.setUri(uri);
             /* 加载 ConversationFragment */
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.rong_content, fragment);
        transaction.commit();
        initConversationViewPager();
    }

    private void initConversationViewPager() {
        Fragment ConversationD = initConversation();
        mViewpager = (ViewPager) this.findViewById(R.id.viewpager_conversation);
        mFragment.add(ConversationD);
        mFragment.add(new IntercomFragment());
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }
        };
        mViewpager.setAdapter(fragmentPagerAdapter);
        mViewpager.setOffscreenPageLimit(4);
        mViewpager.setOnPageChangeListener(this);
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("systemconversation", false)) {
            mViewpager.setCurrentItem(0, false);
        }
    }

    protected void initData() {

        final Conversation.ConversationType[] conversationTypes = {
                Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM,
                Conversation.ConversationType.PUBLIC_SERVICE, Conversation.ConversationType.APP_PUBLIC_SERVICE
        };
        RongIM.getInstance().addUnReadMessageCountChangedObserver(this, conversationTypes);
//        getConversationPush();// 获取 push 的 id 和 target
//        getPushMessage();
    }

    private Fragment initConversation() {
        if (conversationDynamicFragment == null) {
            ConversationFragment fragment = new ConversationFragment();
            Intent intent = getIntent();
            mTargetId = intent.getData().getQueryParameter("targetId");
            mTargetIds = intent.getData().getQueryParameter("targetIds");
            mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));

            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                    .appendQueryParameter("targetId", mTargetId).build();
            fragment.setUri(uri);
             /* 加载 ConversationFragment */
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.rong_content, fragment);
            transaction.commit();
            conversationDynamicFragment = fragment;
            return fragment;
        } else {
            return conversationDynamicFragment;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCountChanged(int count) {

    }
}
