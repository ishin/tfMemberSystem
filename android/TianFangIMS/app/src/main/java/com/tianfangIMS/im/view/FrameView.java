package com.tianfangIMS.im.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by LianMengYu on 2017/2/9.
 */

public class FrameView extends TextView {

    Paint mPaint;
    String[] colorArr = new String[]{"#c8b6ee", "#9fb7e3", "#7be3d4", "#bde7d4", "#f4e09d", "#f7ba8a", "#865573", "#515e8a", "#87cbe2", "#d8d1b8", "#c5958a", "#f28186"};

    public FrameView(Context context) {
        this(context, null);
    }

    public FrameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor(colorArr[(int) (Math.random() * 12)]));
        mPaint.setAlpha(230);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mPaint);
        super.onDraw(canvas);
    }
}
