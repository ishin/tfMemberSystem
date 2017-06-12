package io.rong.imkit.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;

import java.lang.reflect.Method;

/**
 * Created by jiangecho on 2016/11/29.
 */

public class NotificationUtil {

    /**
     * @param context
     * @param title
     * @param content
     * @param intent
     * @param notificationId
     * @param defaults       控制通知属性， 对应public Builder setDefaults(int defaults)
     */
    public static void showNotification(Context context, String title, String content, PendingIntent intent, int notificationId, int defaults) {
        Notification notification = createNotification(context, title, content, intent, defaults);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notification != null) {
            nm.notify(notificationId, notification);
        }
    }

    public static void showNotification(Context context, String title, String content, PendingIntent intent, int notificationId) {
        showNotification(context, title, content, intent, notificationId, Notification.DEFAULT_ALL);
    }

    public static void clearNotification(Context context, int notificationId) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(notificationId);
    }

    private static Notification createNotification(Context context, String title, String content, PendingIntent pendingIntent, int defaults) {
        String tickerText = context.getResources().getString(context.getResources().getIdentifier("rc_notification_ticker_text", "string", context.getPackageName()));
        Notification notification;
        if (android.os.Build.VERSION.SDK_INT < 11) {
            try {
                Method method;
                notification = new Notification(context.getApplicationInfo().icon, tickerText, System.currentTimeMillis());

                Class<?> classType = Notification.class;
                method = classType.getMethod("setLatestEventInfo", new Class[]{Context.class, CharSequence.class, CharSequence.class, PendingIntent.class});
                method.invoke(notification, new Object[]{context, title, content, pendingIntent});

                notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_NO_CLEAR;
                notification.defaults = Notification.DEFAULT_ALL;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            boolean isLollipop = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
            int smallIcon = context.getResources().getIdentifier("notification_small_icon", "drawable", context.getPackageName());

            if (smallIcon <= 0 || !isLollipop) {
                smallIcon = context.getApplicationInfo().icon;
            }

            BitmapDrawable bitmapDrawable = (BitmapDrawable) context.getApplicationInfo().loadIcon(context.getPackageManager());
            Bitmap appIcon = bitmapDrawable.getBitmap();
            Notification.Builder builder = new Notification.Builder(context);
            builder.setLargeIcon(appIcon);
            builder.setSmallIcon(smallIcon);
            builder.setTicker(tickerText);
            builder.setContentTitle(title);
            builder.setContentText(content);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            builder.setOngoing(true);
            builder.setDefaults(defaults);
            notification = builder.getNotification();
        }
        return notification;
    }

    public static int getRingerMode(Context context) {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audio.getRingerMode();
    }
}
