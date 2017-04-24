package io.rong.ptt;

/**
 * Created by jiangecho on 2017/1/4.
 */

class Constants {
//    public static String URL_BASE = "http://35.164.107.27:8080/rce/restapi/ptt";
    public static String URL_BASE = "http://120.26.42.225:8080/rce/restapi/ptt";
//    public static String URL_BASE = "http://42.62.4.82:8080/rce/restapi/ptt";//测试多公司
    public static String URL_JOIN_SESSION = URL_BASE + "/joinsession";
    public static String URL_REQUIRE_CHANNEL = URL_BASE + "/requirechannel";
    public static String URL_RELEASE_CHANNEL = URL_BASE + "/releasechannel";
    public static String URL_PTT_STATUS = URL_BASE + "/status";
    public static String URL_QUIT_SESSION = URL_BASE + "/leavesession";
}
