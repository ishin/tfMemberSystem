package io.rong.ptt.kit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.rong.common.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import io.rong.ptt.PTTClient;
import io.rong.ptt.PTTSession;
import io.rong.ptt.PTTSessionStateListener;
import io.rong.ptt.RequestToSpeakCallback;

/**
 * do not start this activity manually, this activity should be started by PTTManager
 * 对讲功能
 */
public class PTTSessionActivity extends Activity implements PTTSessionStateListener, View.OnClickListener {
    private static final String TAG = PTTSessionActivity.class.getName();
    private AsyncImageView micHolderImageView;
    private ImageView holdToSpeakImageView;
    private TextView micHolderTextView;
    private GridView participantsGridView;

    private PTTClient pttClient;
    private List<String> participants;
    PttParticipantsAdapter pttParticipantsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rc_activity_ptt);
        setTitle(R.string.rce_ptt);
        //获取当前对讲
        pttClient = PTTClient.getInstance();
        PTTSession pttSession = pttClient.getCurrentPttSession();
        participants = pttSession.getParticipantIds();
        Log.e("你好","::"+participants);
        pttClient.setPttSessionStateListener(this);
        micHolderImageView = (AsyncImageView) findViewById(R.id.micHolderImageView);
        micHolderTextView = (TextView) findViewById(R.id.micHolderTextView);
        holdToSpeakImageView = (ImageView) findViewById(R.id.holdToSpeakImageView);
        participantsGridView = (GridView) findViewById(R.id.gridView);
        pttParticipantsAdapter = new PttParticipantsAdapter();
        participantsGridView.setAdapter(pttParticipantsAdapter);

        holdToSpeakImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return requestToSpeak(view, motionEvent);
            }
        });

        findViewById(R.id.exitImageView).setOnClickListener(this);
        findViewById(R.id.hideImageView).setOnClickListener(this);
    }

    //请求说话，抢麦
    boolean requestToSpeak(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            micHolderTextView.setText(getString(R.string.rce_ptt_prepare_to_speak));
            pttClient.requestToSpeak(new RequestToSpeakCallback() {

                //抢麦成功
                @Override
                public void onReadyToSpeak(long maxDurationMillis) {
                    updateMicHolder(RongIMClient.getInstance().getCurrentUserId());
                }

                //抢麦失败
                @Override
                public void onFail(String msg) {
                    RLog.e(TAG, "start speak error " + msg);
                }


                //说话超时，通过服务器设定时长，如果超过自动停止说话
                @Override
                public void onSpeakTimeOut() {
                    Toast.makeText(PTTSessionActivity.this, "speak time out", Toast.LENGTH_SHORT).show();
                    updateMicHolder("");
                }
            });
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            micHolderTextView.setText(getString(R.string.rce_ptt_hold_to_request_mic));
            micHolderImageView.setImageResource(R.drawable.rc_default_portrait);
            pttClient.stopSpeak();
        }
        return true;
    }

    @Override
    public void onParticipantChanged(PTTSession pttSession, final List<String> userIds) {
        participants = userIds;
        pttParticipantsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMicHolderChanged(PTTSession pttSession, String holderUserId) {
        updateMicHolder(holderUserId);
    }

    @Override
    public void onNetworkError(String msg) {

    }

    private void updateMicHolder(String holderUserId) {
        final UserInfo userInfo = RongContext.getInstance().getUserInfoFromCache(holderUserId);
        if (userInfo == null) {
            micHolderTextView.setText(getString(R.string.rce_ptt_hold_to_request_mic));
            micHolderImageView.setImageResource(R.drawable.rc_default_portrait);
        } else {
            micHolderTextView.setText(getString(R.string.rce_ptt_user_is_speaking, userInfo.getName()));
            micHolderImageView.setAvatar(userInfo.getPortraitUri());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.exitImageView) {
            pttClient.leaveSession();
            finish();
        } else if (id == R.id.hideImageView) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        // block back key
    }

    private class PttParticipantsAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return participants == null ? 0 : participants.size();
        }

        @Override
        public Object getItem(int position) {
            return participants.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PttParticipantHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(PTTSessionActivity.this).inflate(R.layout.rc_item_ptt_member, parent, false);
                holder = new PttParticipantHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (PttParticipantHolder) convertView.getTag();
            }
            holder.update(participants.get(position));
            return convertView;
        }
    }

    private class PttParticipantHolder {
        AsyncImageView imageView;

        PttParticipantHolder(View convertView) {
            imageView = (AsyncImageView) convertView.findViewById(R.id.participantImageView);
        }

        void update(String userId) {
            UserInfo userInfo = RongContext.getInstance().getUserInfoFromCache(userId);
            if (userInfo != null) {
                imageView.setAvatar(userInfo.getPortraitUri());
            }
        }

    }
}
