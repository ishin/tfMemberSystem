package io.rong.imkit.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.text.TextUtils;

import java.util.Calendar;
import java.util.List;

import io.rong.common.RLog;
import io.rong.common.SystemUtils;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongNotificationManager;
import io.rong.imkit.model.ConversationInfo;
import io.rong.imkit.model.ConversationKey;
import io.rong.imkit.utils.NotificationUtil;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;


/**
 * 控制弹通知和消息音
 * <p/>
 * 1、应用是否在后台
 * 2、新消息提醒设置
 * 3、安静时间设置
 */
public class MessageNotificationManager {
    private final static String TAG = "MessageNotificationManager";

    //应用在前台，如果没有在会话界面，收消息时每间隔 3s 一次响铃、震动。
    //收离线消息时，left 为 0 才会做震动、响铃。
    private final static int SOUND_INTERVAL = 3000;
    private long lastSoundTime = 0;

    /**
     * 创建单实例。
     */
    private static class SingletonHolder {
        static final MessageNotificationManager instance = new MessageNotificationManager();
    }

    public static MessageNotificationManager getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * 是否设置了消息免打扰，新消息提醒是否关闭？
     *
     * @param context 上下文
     * @param message 要通知的消息
     * @param left    剩余的消息
     */
    public void notifyIfNeed(final Context context, final Message message, final int left) {
        // @消息按最高优先级处理
        if (message.getContent().getMentionedInfo() != null) {
            MentionedInfo mentionedInfo = message.getContent().getMentionedInfo();
            if (mentionedInfo.getType().equals(MentionedInfo.MentionedType.ALL)
                    || (mentionedInfo.getType().equals(MentionedInfo.MentionedType.PART)
                    && mentionedInfo.getMentionedUserIdList() != null
                    && mentionedInfo.getMentionedUserIdList().contains(RongIMClient.getInstance().getCurrentUserId()))) {
                notify(context, message, left);
                return;
            }
        }

        if (isInQuietTime(context)) {
            return;
        }

        ConversationKey key = ConversationKey.obtain(message.getTargetId(), message.getConversationType());
        Conversation.ConversationNotificationStatus notificationStatus = RongContext.getInstance().getConversationNotifyStatusFromCache(key);
        if (notificationStatus != null && notificationStatus == Conversation.ConversationNotificationStatus.NOTIFY) {
            notify(context, message, left);
        }
    }

    private void notify(Context context, Message message, int left) {
        boolean isInBackground = SystemUtils.isInBackground(context);

        if (message.getConversationType() == Conversation.ConversationType.CHATROOM) {
            return;
        }

        if (isInBackground) {
            RongNotificationManager.getInstance().onReceiveMessageFromApp(message);
        } else if (!isInConversationPager(message.getTargetId(), message.getConversationType())
                && left == 0
                && System.currentTimeMillis() - lastSoundTime > SOUND_INTERVAL) {
            lastSoundTime = System.currentTimeMillis();
            int ringerMode = NotificationUtil.getRingerMode(context);
            if (ringerMode != AudioManager.RINGER_MODE_SILENT) {
                if (ringerMode != AudioManager.RINGER_MODE_VIBRATE) {
                    sound();
                }
                vibrate();
            }
        }
    }

    private boolean isInQuietTime(Context context) {

        String startTimeStr = getNotificationQuietHoursForStartTime(context);

        int hour = -1;
        int minute = -1;
        int second = -1;

        if (!TextUtils.isEmpty(startTimeStr) && startTimeStr.contains(":")) {
            String[] time = startTimeStr.split(":");

            try {
                if (time.length >= 3) {
                    hour = Integer.parseInt(time[0]);
                    minute = Integer.parseInt(time[1]);
                    second = Integer.parseInt(time[2]);
                }
            } catch (NumberFormatException e) {
                RLog.e(TAG, "getConversationNotificationStatus NumberFormatException");
            }
        }

        if (hour == -1 || minute == -1 || second == -1) {
            return false;
        }

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.HOUR_OF_DAY, hour);
        startCalendar.set(Calendar.MINUTE, minute);
        startCalendar.set(Calendar.SECOND, second);


        long spanTime = getNotificationQuietHoursForSpanMinutes(context) * 60;
        long startTime = startCalendar.getTimeInMillis();

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis(startTime + spanTime * 1000);

        Calendar currentCalendar = Calendar.getInstance();
        if (currentCalendar.get(Calendar.DAY_OF_MONTH) == endCalendar.get(Calendar.DAY_OF_MONTH)) {

            return currentCalendar.after(startCalendar) && currentCalendar.before(endCalendar);
        } else {

            if (currentCalendar.before(startCalendar)) {

                endCalendar.set(Calendar.DAY_OF_MONTH, currentCalendar.get(Calendar.DAY_OF_MONTH));

                return currentCalendar.before(endCalendar);
            } else {
                return true;
            }
        }
    }

    private boolean isInConversationPager(String id, Conversation.ConversationType type) {
        List<ConversationInfo> list = RongContext.getInstance().getCurrentConversationList();
        //如果处于所在会话界面，不响铃。
        for (ConversationInfo conversationInfo : list) {
            return id.equals(conversationInfo.getTargetId()) && type == conversationInfo.getConversationType();
        }
        return false;
    }

    /**
     * 本地化通知免打扰时间。
     *
     * @param startTime   默认  “-1”
     * @param spanMinutes 默认 -1
     */
    public void saveNotificationQuietHours(Context mContext, String startTime, int spanMinutes) {
        SharedPreferences preferences = mContext.getSharedPreferences("RONG_SDK", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("QUIET_HOURS_START_TIME", startTime);
        editor.putInt("QUIET_HOURS_SPAN_MINUTES", spanMinutes);
        editor.apply();
    }

    /**
     * 获取通知免打扰开始时间
     *
     * @return
     */
    private String getNotificationQuietHoursForStartTime(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences("RONG_SDK", Context.MODE_PRIVATE);
        ;
        return preferences.getString("QUIET_HOURS_START_TIME", "");
    }

    /**
     * 获取通知免打扰时间间隔
     *
     * @return
     */
    private int getNotificationQuietHoursForSpanMinutes(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences("RONG_SDK", Context.MODE_PRIVATE);
        ;
        return preferences.getInt("QUIET_HOURS_SPAN_MINUTES", 0);
    }

    private void sound() {
        Uri res = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    mp.release();
                }
            });
            //设置 STREAM_RING 模式：当系统设置震动时，使用系统设置方式是否播放收消息铃声。
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mediaPlayer.setDataSource(RongContext.getInstance(), res);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) RongContext.getInstance().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[] {0, 200, 250, 200}, -1);
    }
}
