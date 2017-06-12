package com.tianfangIMS.im.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * Lmy
 */
public class SildingFinishLayout extends RelativeLayout implements View.OnTouchListener {
    /**
     * SildingFinishLayout
     */
    private ViewGroup mParentView;
    private int mTouchSlop;
    private int downX;
    private int downY;
    /**
     */
    private int tempX;
    /**
     */
    private Scroller mScroller;
    /**
     */
    private int viewWidth;

    private boolean isSilding;

    private OnSildingFinishListener onSildingFinishListener;
    private boolean isFinish;


    public SildingFinishLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SildingFinishLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context);
    }


    /**
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = tempX = (int) ev.getRawX();
                downY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getRawX();
                if (Math.abs(moveX - downX) > mTouchSlop
                        && Math.abs((int) ev.getRawY() - downY) < mTouchSlop) {
                    return true;
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getRawX();
                int deltaX = tempX - moveX;
                tempX = moveX;
                if (Math.abs(moveX - downX) > mTouchSlop
                        && Math.abs((int) event.getRawY() - downY) < mTouchSlop) {
                    isSilding = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                int upX = (int) event.getRawX();
                isSilding = false;
                if (upX - downX >= 30) {
                    isFinish = true;
                    if (onSildingFinishListener != null) {
                        onSildingFinishListener.onSildingFinish();
                    }
                } else {
                    System.out.println("scrollOrigin");
                    if (onSildingFinishListener != null) {
                        onSildingFinishListener.onLeftScroll();
                    }
                    isFinish = false;
                }
                break;
        }

        return true;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            mParentView = (ViewGroup) this.getParent();
            viewWidth = this.getWidth();
        }
    }

    /**
     * @param onSildingFinishListener
     */
    public void setOnSildingFinishListener(
            OnSildingFinishListener onSildingFinishListener) {
        this.onSildingFinishListener = onSildingFinishListener;
    }

    private void scrollOrigin() {
        int delta = mParentView.getScrollX();
        mScroller.startScroll(mParentView.getScrollX(), 0, -delta, 0,
                Math.abs(delta));
        postInvalidate();
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mParentView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
            if (mScroller.isFinished() && isFinish) {
                if (onSildingFinishListener != null) {
                    onSildingFinishListener.onSildingFinish();
                } else {
                    scrollOrigin();
                    isFinish = false;
                }
            }
        }
    }

    public interface OnSildingFinishListener {
        public void onSildingFinish();

        public void onLeftScroll();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
