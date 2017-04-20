package io.rong.imkit.fragment;

/**
 * Created by weiqinxiao on 16/11/30.
 */

public interface IHistoryDataResultCallback<T> {
    void onResult(T data);
    void onError();
}
