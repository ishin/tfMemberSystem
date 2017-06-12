package com.tianfangIMS.im.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.tianfangIMS.im.R;

/**
 * Created by LianMengYu on 2017/2/6.
 * 用于对讲界面的圆形头像
 */

public class CircleImage extends ImageView {

    Paint mPaint;

    public CircleImage(Context context) {
        super(context);
        init();
    }

    public CircleImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.default_image);
        setImageBitmap(drawCircle(mBitmap));
        mPaint.setColor(Color.parseColor("#6BDDC3"));
        //被注释的两行代码只渲染圆形的边 将其注释掉就会将整个圆进行指定颜色的渲染
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);
        //从当前控件的最中心开始画圆
        canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2 - 5, mPaint);
        super.onDraw(canvas);
    }

    private Bitmap drawCircle(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        //宽度 小于等于 高度
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            //宽度 大于 高度
            roundPx = height / 2;
            //需要裁剪的两侧长度
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        //创建新的画布
        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();

        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(20, 20, height - 20, height - 20);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;

    }
}
