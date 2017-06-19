package com.pro.rc.mylibrary;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */

public class okhttpUtil {
     private static String key = "1AS5Caw05sd84SD8LOKI4pF4LtG0QASD0NyvYt0RE0Q==";

    public okhttpUtil() {
    }

    public static String getOkHttpUtils() {
        String str = EncryptUtil.encrypt(EncryptUtil.RemoveTrim(key));
        return str;
    }

    public static long nn() {
        String aa = null;

        try {
            aa = EncryptUtil.RemoveTrim(key);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        long cc = Long.parseLong(aa);
        return cc;
    }

    public static void gethttpUtils() {
        HttpHeaders headers = new HttpHeaders();
        headers.put("commonHeaderKey1", "commonHeaderValue1");
        headers.put("commonHeaderKey2", "commonHeaderValue2");
        HttpParams params = new HttpParams();
        params.put("commonParamsKey1", "commonParamsValue1", new boolean[0]);
        params.put("commonParamsKey2", "commonParamsValue3", new boolean[0]);
        OkGo.getInstance().debug("OkGo", Level.INFO, false).setConnectTimeout(60000L).setReadTimeOut(60000L).setWriteTimeOut(60000L).setCacheMode(CacheMode.NO_CACHE).setCacheTime(-1L).setRetryCount(3).setCookieStore(new PersistentCookieStore()).addCommonHeaders(headers).addCommonParams(params);
    }
}
