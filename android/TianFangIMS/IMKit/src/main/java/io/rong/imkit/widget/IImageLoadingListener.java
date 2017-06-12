package io.rong.imkit.widget;

import android.net.Uri;

/**
 * Created by weiqinxiao on 16/7/5.
 */
public interface IImageLoadingListener {
    void onLoadingComplete(Uri uri);
    void onLoadingFail();
}
