package io.rong.ptt;

import java.util.List;

/**
 * Created by jiangecho on 2016/12/26.
 */

public interface JoinSessionCallback {
    // include current participants?
    void onSuccess(List<String> users);

    void onError(String msg);
}
