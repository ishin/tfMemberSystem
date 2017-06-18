package io.rong.imkit.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.rong.imkit.R;
import io.rong.imkit.RongContext;

public class RongDateUtils {

    private static final int OTHER = 2014;
    private static final int TODAY = 6;
    private static final int YESTERDAY = 15;

    public static int judgeDate(Date date) {
        // 今天
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(Calendar.HOUR_OF_DAY, 0);
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.MILLISECOND, 0);
        // 昨天
        Calendar calendarYesterday = Calendar.getInstance();
        calendarYesterday.add(Calendar.DATE, -1);
        calendarYesterday.set(Calendar.HOUR_OF_DAY, 0);
        calendarYesterday.set(Calendar.MINUTE, 0);
        calendarYesterday.set(Calendar.SECOND, 0);
        calendarYesterday.set(Calendar.MILLISECOND, 0);

        Calendar calendarTomorrow = Calendar.getInstance();
        calendarTomorrow.add(Calendar.DATE, 1);
        calendarTomorrow.set(Calendar.HOUR_OF_DAY, 0);
        calendarTomorrow.set(Calendar.MINUTE, 0);
        calendarTomorrow.set(Calendar.SECOND, 0);
        calendarTomorrow.set(Calendar.MILLISECOND, 0);

        // 目标日期
        Calendar calendarTarget = Calendar.getInstance();
        calendarTarget.setTime(date);

        if (calendarTarget.before(calendarYesterday)) {// 是否在calendarT之前
            return OTHER;
        } else if (calendarTarget.before(calendarToday)) {
            return YESTERDAY;
        } else if (calendarTarget.before(calendarTomorrow)) {
            return TODAY;
        } else {
            return OTHER;
        }
    }

    private static String getWeekDay(int dayInWeek) {
        String weekDay = "";
        switch (dayInWeek) {
            case 1:
                weekDay = RongContext.getInstance().getResources().getString(R.string.rc_sunsay_format);
                break;
            case 2:
                weekDay = RongContext.getInstance().getResources().getString(R.string.rc_monday_format);
                break;
            case 3:
                weekDay = RongContext.getInstance().getResources().getString(R.string.rc_tuesday_format);
                break;
            case 4:
                weekDay = RongContext.getInstance().getResources().getString(R.string.rc_wednesday_format);
                break;
            case 5:
                weekDay = RongContext.getInstance().getResources().getString(R.string.rc_thuresday_format);
                break;
            case 6:
                weekDay = RongContext.getInstance().getResources().getString(R.string.rc_friday_format);
                break;
            case 7:
                weekDay = RongContext.getInstance().getResources().getString(R.string.rc_saturday_format);
                break;
            default:
                break;
        }
        return weekDay;
    }

    public static boolean isTime24Hour(Context context) {
        String timeFormat = android.provider.Settings.System.getString(context.getContentResolver(),
                            android.provider.Settings.System.TIME_12_24);

        if (timeFormat != null && timeFormat.equals("24")) {
            return true;
        }

        return false;
    }


    private static String getTimeString(long dateMillis, Context context) {
        if (dateMillis <= 0) {
            return "";
        }

        Date date = new Date(dateMillis);
        String formatTime = null;
        if (isTime24Hour(context)) {
            formatTime = formatDate(date, "HH:mm");
        } else {
            Calendar calendarTime = Calendar.getInstance();
            calendarTime.setTimeInMillis(dateMillis);
            int hour = calendarTime.get(Calendar.HOUR);
            if (calendarTime.get(Calendar.AM_PM) == 0) { //AM
                if (hour < 6) { //凌晨
                    if (hour == 0) {
                        hour = 12;
                    }
                    formatTime = RongContext.getInstance().getResources().getString(R.string.rc_daybreak_format);
                } else if (hour >= 6 && hour < 12) { //早上
                    formatTime = RongContext.getInstance().getResources().getString(R.string.rc_morning_format);
                }
            } else {//PM
                if (hour == 0) { //中午
                    formatTime = RongContext.getInstance().getResources().getString(R.string.rc_noon_format);
                    hour = 12;
                } else if (hour >= 1 && hour <= 5) { //下午
                    formatTime = RongContext.getInstance().getResources().getString(R.string.rc_afternoon_format);
                } else if (hour >= 6 && hour <= 11) {//晚上
                    formatTime = RongContext.getInstance().getResources().getString(R.string.rc_night_format);
                }
            }

            int minuteInt = calendarTime.get(Calendar.MINUTE);
            String minuteStr = Integer.toString(minuteInt);
            String timeStr = null;
            if (minuteInt < 10) {
                minuteStr = "0" + minuteStr;
            }
            timeStr = Integer.toString(hour) + ":" + minuteStr;

            if (context.getResources().getConfiguration().locale.getCountry().equals("CN")) {
                formatTime = formatTime + timeStr;
            } else {
                formatTime = timeStr + " " + formatTime;
            }
        }
        return formatTime;
    }

    private static String getDateTimeString(long dateMillis, boolean showTime, Context context) {
        if (dateMillis <= 0) {
            return "";
        }

        String formatDate = null;

        Date date = new Date(dateMillis);
        int type = judgeDate(date);
        long time = System.currentTimeMillis();
        Calendar calendarCur = Calendar.getInstance();
        Calendar calendardate = Calendar.getInstance();
        calendardate.setTimeInMillis(dateMillis);
        calendarCur.setTimeInMillis(time);
        int month = calendardate.get(Calendar.MONTH);
        int year = calendardate.get(Calendar.YEAR);
        int weekInMonth = calendardate.get(Calendar.DAY_OF_WEEK_IN_MONTH);
        int monthCur = calendarCur.get(Calendar.MONTH);
        int yearCur = calendarCur.get(Calendar.YEAR);
        int weekInMonthCur = calendarCur.get(Calendar.DAY_OF_WEEK_IN_MONTH);

        switch (type) {
            case TODAY:
                formatDate = getTimeString(dateMillis, context);
                break;

            case YESTERDAY:
                String formatString = RongContext.getInstance().getResources().getString(R.string.rc_yesterday_format);
                if (showTime) {
                    formatDate = formatString + " " + getTimeString(dateMillis, context);
                } else {
                    formatDate = formatString;
                }
                break;

            case OTHER:
                if (year == yearCur) {//同年
                    if (month == monthCur && weekInMonth == weekInMonthCur) {//同月同周
                        formatDate = getWeekDay(calendardate.get(Calendar.DAY_OF_WEEK));
                    } else { //不同月
                        if (context.getResources().getConfiguration().locale.getCountry().equals("CN")) {
                            formatDate = formatDate(date, "M" + RongContext.getInstance().getResources().getString(R.string.rc_month_format) +
                                                    "d" + RongContext.getInstance().getResources().getString(R.string.rc_day_format));
                        } else {
                            formatDate = formatDate(date, "M/d");
                        }
                    }
                } else {
                    if (context.getResources().getConfiguration().locale.getCountry().equals("CN")) {
                        formatDate = formatDate(date, "yyyy" + RongContext.getInstance().getResources().getString(R.string.rc_year_format) +
                                                "M" + RongContext.getInstance().getResources().getString(R.string.rc_month_format) +
                                                "d" + RongContext.getInstance().getResources().getString(R.string.rc_day_format));
                    } else {
                        formatDate = formatDate(date, "M/d/yy");
                    }
                }

                if (showTime) {
                    formatDate = formatDate + " " + getTimeString(dateMillis, context);
                }
                break;
            default:
                break;
        }

        return formatDate;
    }


    public static String getConversationListFormatDate(long dateMillis, Context context) {
        String formatDate = getDateTimeString(dateMillis, false, context);
        return formatDate;
    }

    public static String getConversationFormatDate(long dateMillis, Context context) {
        String formatDate = getDateTimeString(dateMillis, true, context);
        return formatDate;
    }

    /**
     * @param currentTime 当前时间
     * @param preTime     之前的某个时间
     * @param interval    时间间隔
     * @return true 间隔大于interval秒  false 小于等于
     */
    public static boolean isShowChatTime(long currentTime, long preTime, int interval) {

        int typeCurrent = judgeDate(new Date(currentTime));
        int typePre = judgeDate(new Date(preTime));

        if (typeCurrent == typePre) {

            if ((currentTime - preTime) > interval * 1000) {
                return true;
            } else {
                return false;
            }

        } else {
            return true;
        }

        // return typeCurrent == typePre ? (((currentTime - preTime) > 60 *
        // 1000) ? true : false) : true;

    }

    public static String formatDate(Date date, String fromat) {
        SimpleDateFormat sdf = new SimpleDateFormat(fromat);
        return sdf.format(date);
    }

}
