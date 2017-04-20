package com.tianfangIMS.im.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.tianfangIMS.im.bean.ChildPoint;
import com.tianfangIMS.im.bean.TreeInfo;
import com.tianfangIMS.im.utils.CropCircleBorderTransformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Titan on 2017/2/17.
 */

public class CustomView extends RelativeLayout {

    private static final String TAG = "CustomView";

    Context mContext;

    Paint mPaint;

    RectF mRectF;

    List<ChildPoint> mChildPoints;

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mChildPoints = new ArrayList<>();
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int half = getWidth() / 2;
        //外围边宽为半径的1/20
        int border = half / 20;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        //外圆与当前布局left的距离
        int margin = getChildAt(0).getMeasuredWidth() / 2;

        //填充
        mPaint.setStyle(Paint.Style.FILL);
        //内半圆底色
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(25);
        mPaint.setStrokeWidth(0);

        mRectF = new RectF(margin, margin, getWidth() - margin, getHeight() - margin);
        canvas.drawArc(mRectF, 90, 180, true, mPaint);

        //描边
        mPaint.setStyle(Paint.Style.STROKE);
        //外围边颜色
        mPaint.setColor(Color.GRAY);
        mPaint.setAlpha(25);
        //描边宽度 可随意设置
        mPaint.setStrokeWidth(border / 2);

        //黑色外边据布局左侧为75px(以联系人控件150的宽度为基)
        mRectF = new RectF(margin, margin, getWidth() - margin, getHeight() - margin);
        canvas.drawArc(mRectF, 90, 180, false, mPaint);

//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeWidth(2);
//        mPaint.setColor(Color.YELLOW);

        //绘制线的长度(斜边长)
        int radius = half - margin;

        for (int i = 60; i >= 0; i -= 30) {
            //余弦
            double angle = Math.sin(i * Math.PI / 180);
            //余弦公式 得出垂直边长
            int vertical = (int) (angle * radius);
            //勾股定理 得出底边长
            int bottom = (int) Math.sqrt(Math.pow(radius, 2) - Math.pow(vertical, 2));

//            canvas.drawLine(half, half, half - bottom, half - vertical, mPaint);
            mChildPoints.add(new ChildPoint(half - bottom, half - vertical));
        }

        for (int i = 30; i <= 60; i += 30) {
            //余弦
            double angle = Math.sin(i * Math.PI / 180);
            //余弦公式 得出垂直边长
            int vertical = (int) (angle * radius);
            //勾股定理 得出底边长
            int bottom = (int) Math.sqrt(Math.pow(radius, 2) - Math.pow(vertical, 2));

//            canvas.drawLine(half, half, half - bottom, half + vertical, mPaint);
            mChildPoints.add(new ChildPoint(half - bottom, half + vertical));
        }

        for (int i = 0; i < getChildCount(); i++) {
            ChildPoint mPoint = mChildPoints.get(i);
            ImageView mCircleImage = (ImageView) getChildAt(i);
            TreeInfo mInfo = (TreeInfo) mCircleImage.getTag(mCircleImage.getId());
            Glide.with(mContext).load(mInfo.getLogo()).bitmapTransform(new CropCircleBorderTransformation(mContext)).into(mCircleImage);
            mCircleImage.layout(mPoint.getX() - mCircleImage.getMeasuredWidth() / 2, mPoint.getY() - mCircleImage.getMeasuredHeight() / 2, mPoint.getX() + mCircleImage.getMeasuredWidth() / 2, mPoint.getY() + mCircleImage.getMeasuredHeight() / 2);
        }
    }
}
