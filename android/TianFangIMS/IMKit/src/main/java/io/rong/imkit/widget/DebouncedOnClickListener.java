package io.rong.imkit.widget;

import android.os.SystemClock;
import android.view.View;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by jiangecho on 2016/10/28.
 */

public abstract class DebouncedOnClickListener implements View.OnClickListener {
    private static final long DEFAULT_MIN_INTERNAL = 500;
    private final long mMinInterval;
    private Map<View, Long> mClickMap;

    public DebouncedOnClickListener(long minInterval) {
        this.mMinInterval = minInterval;
        mClickMap = new WeakHashMap<>();
    }

    public DebouncedOnClickListener() {
        this(DEFAULT_MIN_INTERNAL);
    }

    @Override
    public void onClick(View v) {
        Long lastClickTimestamp = mClickMap.get(v);
        long currentTimestamp = SystemClock.uptimeMillis();

        mClickMap.put(v, currentTimestamp);
        if (lastClickTimestamp == null || (lastClickTimestamp - currentTimestamp > mMinInterval)) {
            onDebouncedClick(v);
        }
    }

    public abstract void onDebouncedClick(View view);
}
