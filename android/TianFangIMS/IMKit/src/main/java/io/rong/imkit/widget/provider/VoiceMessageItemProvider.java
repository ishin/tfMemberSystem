package io.rong.imkit.widget.provider;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imkit.R;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.manager.AudioPlayManager;
import io.rong.imkit.manager.AudioRecordManager;
import io.rong.imkit.manager.IAudioPlayListener;
import io.rong.imkit.model.Event;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.VoiceMessage;

@ProviderTag(messageContent = VoiceMessage.class, showReadState = true)
public class VoiceMessageItemProvider extends IContainerItemProvider.MessageProvider<VoiceMessage> {
    private final static String TAG = "VoiceMessageItemProvider";

    private static class ViewHolder {
        ImageView img;
        TextView left;
        TextView right;
        ImageView unread;
    }

    public VoiceMessageItemProvider(Context context) {
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_voice_message, null);
        ViewHolder holder = new ViewHolder();
        holder.left = (TextView) view.findViewById(R.id.rc_left);
        holder.right = (TextView) view.findViewById(R.id.rc_right);
        holder.img = (ImageView) view.findViewById(R.id.rc_img);
        holder.unread = (ImageView) view.findViewById(R.id.rc_voice_unread);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(final View v, int position, final VoiceMessage content, final UIMessage message) {
        final ViewHolder holder = (ViewHolder) v.getTag();
        if (message.continuePlayAudio) {
            Uri playingUri = AudioPlayManager.getInstance().getPlayingUri();
            if (playingUri == null || !playingUri.equals(content.getUri())) {
                final boolean listened = message.getMessage().getReceivedStatus().isListened();
                AudioPlayManager.getInstance().startPlay(v.getContext(), content.getUri(), new VoiceMessagePlayListener(v.getContext(), message, holder, listened));
            }
        } else {
            Uri playingUri = AudioPlayManager.getInstance().getPlayingUri();
            if (playingUri != null && playingUri.equals(content.getUri())) {
                setLayout(v.getContext(), holder, message, true);
                final boolean listened = message.getMessage().getReceivedStatus().isListened();
                AudioPlayManager.getInstance().setPlayListener(new VoiceMessagePlayListener(v.getContext(), message, holder, listened));
            } else {
                setLayout(v.getContext(), holder, message, false);
            }
        }
    }


    @Override
    public void onItemClick(final View view, int position, final VoiceMessage content, final UIMessage message) {
        RLog.d(TAG, "Item index:" + position);
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.unread.setVisibility(View.GONE);
        Uri playingUri = AudioPlayManager.getInstance().getPlayingUri();
        if (playingUri != null && playingUri.equals(content.getUri())) {
            AudioPlayManager.getInstance().stopPlay();
        } else {
            final boolean listened = message.getMessage().getReceivedStatus().isListened();
            AudioPlayManager.getInstance().startPlay(view.getContext(), content.getUri(), new VoiceMessagePlayListener(view.getContext(), message, holder, listened));
        }
    }

    @Override
    public void onItemLongClick(final View view, int position, VoiceMessage content, final UIMessage message) {
        String[] items;

        long deltaTime = RongIM.getInstance().getDeltaTime();
        long normalTime = System.currentTimeMillis() - deltaTime;
        boolean enableMessageRecall = false;
        int messageRecallInterval = -1;
        boolean hasSent = (!message.getSentStatus().equals(Message.SentStatus.SENDING)) && (!message.getSentStatus().equals(Message.SentStatus.FAILED));

        try {
            enableMessageRecall = RongContext.getInstance().getResources().getBoolean(R.bool.rc_enable_message_recall);
            messageRecallInterval = RongContext.getInstance().getResources().getInteger(R.integer.rc_message_recall_interval);
        } catch (Resources.NotFoundException e) {
            RLog.e(TAG, "rc_message_recall_interval not configure in rc_config.xml");
            e.printStackTrace();
        }
        if (hasSent
                && enableMessageRecall
                && (normalTime - message.getSentTime()) <= messageRecallInterval * 1000
                && message.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId())
                && message.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId())
                && !message.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE)
                && !message.getConversationType().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE)
                && !message.getConversationType().equals(Conversation.ConversationType.PUBLIC_SERVICE)
                && !message.getConversationType().equals(Conversation.ConversationType.SYSTEM)
                && !message.getConversationType().equals(Conversation.ConversationType.CHATROOM)) {
            items = new String[] {view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete), view.getContext().getResources().getString(R.string.rc_dialog_item_message_recall)};
        } else {
            items = new String[] {view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete)};
        }

        OptionsPopupDialog.newInstance(view.getContext(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
            @Override
            public void onOptionsItemClicked(int which) {
                if (which == 0) {
                    AudioPlayManager.getInstance().stopPlay();
                    RongIM.getInstance().deleteMessages(new int[] {message.getMessageId()}, null);
                } else if (which == 1) {
                    if (AudioPlayManager.getInstance().getPlayingUri() != null) {
                        AudioPlayManager.getInstance().stopPlay();
                    }
                    RongIM.getInstance().recallMessage(message.getMessage(), getPushContent(view.getContext(), message));
                }
            }
        }).show();
    }

    private void setLayout(Context context, ViewHolder holder, UIMessage message, boolean playing) {
        VoiceMessage content = (VoiceMessage) message.getContent();
        int minLength = 57;
        int duration = AudioRecordManager.getInstance().getMaxVoiceDuration();
        holder.img.getLayoutParams().width = (int) ((content.getDuration() * (180 / duration) + minLength) * context.getResources().getDisplayMetrics().density);

        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.left.setText(String.format("%s\"", content.getDuration()));
            holder.left.setVisibility(View.VISIBLE);
            holder.right.setVisibility(View.GONE);
            holder.unread.setVisibility(View.GONE);
            holder.img.setScaleType(ImageView.ScaleType.FIT_END);
            holder.img.setBackgroundResource(R.drawable.rc_ic_bubble_right);
            AnimationDrawable animationDrawable = (AnimationDrawable) context.getResources().getDrawable(R.drawable.rc_an_voice_sent);
            if (playing) {
                holder.img.setImageDrawable(animationDrawable);
                if (animationDrawable != null)
                    animationDrawable.start();
            } else {
                holder.img.setImageDrawable(holder.img.getResources().getDrawable(R.drawable.rc_ic_voice_sent));
                if (animationDrawable != null)
                    animationDrawable.stop();
            }
        } else {
            holder.right.setText(String.format("%s\"", content.getDuration()));
            holder.right.setVisibility(View.VISIBLE);
            holder.left.setVisibility(View.GONE);
            if (!message.getReceivedStatus().isListened())
                holder.unread.setVisibility(View.VISIBLE);
            else
                holder.unread.setVisibility(View.GONE);
            holder.img.setBackgroundResource(R.drawable.rc_ic_bubble_left);
            AnimationDrawable animationDrawable = (AnimationDrawable) context.getResources().getDrawable(R.drawable.rc_an_voice_receive);
            if (playing) {
                holder.img.setImageDrawable(animationDrawable);
                if (animationDrawable != null)
                    animationDrawable.start();
            } else {
                holder.img.setImageDrawable(holder.img.getResources().getDrawable(R.drawable.rc_ic_voice_receive));
                if (animationDrawable != null)
                    animationDrawable.stop();
            }
            holder.img.setScaleType(ImageView.ScaleType.FIT_START);
        }
    }

    @Override
    public Spannable getContentSummary(VoiceMessage data) {
        return new SpannableString(RongContext.getInstance().getString(R.string.rc_message_content_voice));
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    private boolean muteAudioFocus(Context context, boolean bMute) {
        if (context == null) {
            RLog.d(TAG, "muteAudioFocus context is null.");
            return false;
        }
        if (Build.VERSION.SDK_INT < 8) {
            // 2.1以下的版本不支持下面的API：requestAudioFocus和abandonAudioFocus
            RLog.d(TAG, "muteAudioFocus Android 2.1 and below can not stop music");
            return false;
        }
        boolean bool = false;
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (bMute) {
            int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        } else {
            int result = am.abandonAudioFocus(null);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
        RLog.d(TAG, "muteAudioFocus pauseMusic bMute=" + bMute + " result=" + bool);
        return bool;
    }

    private class VoiceMessagePlayListener implements IAudioPlayListener {
        private Context context;
        private UIMessage message;
        private ViewHolder holder;
        private boolean listened;

        public VoiceMessagePlayListener(Context context, UIMessage message, ViewHolder holder, boolean listened) {
            this.context = context;
            this.message = message;
            this.holder = holder;
            this.listened = listened;
        }

        @Override
        public void onStart(Uri uri) {
            message.continuePlayAudio = false;
            message.setListening(true);
            message.getReceivedStatus().setListened();
            RongIMClient.getInstance().setMessageReceivedStatus(message.getMessageId(), message.getReceivedStatus(), null);
            setLayout(context, holder, message, true);
            EventBus.getDefault().post(new Event.AudioListenedEvent(message.getMessage()));
        }

        @Override
        public void onStop(Uri uri) {
            message.setListening(false);
            setLayout(context, holder, message, false);
        }

        @Override
        public void onComplete(Uri uri) {
            Event.PlayAudioEvent event = Event.PlayAudioEvent.obtain();
            event.messageId = message.getMessageId();
            //判断是否未听语音消息，在决定是否连续播放。
            if (message.isListening() && message.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
                try {
                    event.continuously = RongContext.getInstance().getResources().getBoolean(R.bool.rc_play_audio_continuous);
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (event.continuously) {
                EventBus.getDefault().post(event);
            }
            message.setListening(false);
            setLayout(context, holder, message, false);
        }

    }
}