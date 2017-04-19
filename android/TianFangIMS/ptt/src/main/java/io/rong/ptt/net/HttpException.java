package io.rong.ptt.net;

/**
 * Created by jiangecho on 2017/1/4.
 */

public class HttpException extends Exception {
    private int status; // http status
    private String errorMsg;

    public HttpException(int status, String errorMsg) {
        this.status = status;
        this.errorMsg = errorMsg;
    }

    public int getStatus() {
        return status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
