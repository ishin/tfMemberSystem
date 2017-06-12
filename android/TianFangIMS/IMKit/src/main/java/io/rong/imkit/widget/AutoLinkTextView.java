package io.rong.imkit.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import io.rong.imkit.utils.RongLinkify;

/**
 * Created by jiangecho on 2016/10/31.
 */

public class AutoLinkTextView extends TextView {

    public AutoLinkTextView(Context context) {
        super(context);
    }

    public AutoLinkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoLinkTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AutoLinkTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setAutoLinkMask(0);
        super.setText(text, type);
        RongLinkify.addLinks(this, RongLinkify.ALL);
    }
}
