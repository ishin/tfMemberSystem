package com.tianfangIMS.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.utils.NToast;

import java.util.Hashtable;

/**
 * Created by LianMengYu on 2017/4/27.
 */

public class QRDialog extends Dialog {
    private Context mContext;
    private ImageView QRcode_image, iv_qr_photo, qr_sex;
    private TextView qr_name, qr_position;
    //Name, Position, Sex, Logo
    private String Name, Position, Sex, Logo, Account;
    boolean flag = false;
    private static final int IMAGE_HALFWIDTH = 50;//宽度值，影响中间图片大小
    private String qrcodeContent;
    private String GUUID;

    public QRDialog(Context context, String logo, String sex, String position, String name, String account) {
        super(context);
        this.mContext = context;
        Logo = logo;
        Sex = sex;
        Position = position;
        Name = name;
        Account = account;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.qr_image_dialog, null);
        setContentView(view);
        GUUID = java.util.UUID.randomUUID().toString();
        QRcode_image = (ImageView) view.findViewById(R.id.QRcode_image);
        iv_qr_photo = (ImageView) view.findViewById(R.id.iv_qr_photo);
        qr_sex = (ImageView) view.findViewById(R.id.qr_sex);
        qr_name = (TextView) view.findViewById(R.id.qr_name);
        qr_position = (TextView) view.findViewById(R.id.qr_position);
        setInfo();
    }

    private void setInfo() {
        Picasso.with(mContext)
                .load(ConstantValue.ImageFile + Logo)
                .resize(80, 80)
                .centerCrop()
                .placeholder(R.mipmap.default_portrait)
                .config(Bitmap.Config.ARGB_8888)
                .error(R.mipmap.default_portrait)
                .into(iv_qr_photo);
        if (TextUtils.isEmpty(Logo)) {
            Logo = "defaultlogo.png";
        }
        qrcodeContent = Account + "&" + GUUID;
        qr_name.setText(Name);
        qr_position.setText(Position);
        if (Sex != null && !TextUtils.isEmpty(Sex)){
            if (Sex.equals("1")) {
                qr_sex.setImageResource(R.mipmap.me_sexicon_nan);
            } else {
                qr_sex.setImageResource(R.mipmap.me_sexicon_nv);
            }
        }else{
            return;
        }


        Picasso.with(mContext)
                .load(ConstantValue.ImageFile + Logo)
                .resize(50, 50)
                .centerCrop()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        try {
                            QRcode_image.setImageBitmap(createCode(qrcodeContent, bitmap, BarcodeFormat.QR_CODE));
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        if (placeHolderDrawable == null) {
                            NToast.shortToast(mContext, "二维码正在加载，请稍等");
                        }
                    }
                });
    }

    /**
     * 生成二维码
     *
     * @param string  二维码中包含的文本信息
     * @param mBitmap logo图片
     * @param format  编码格式
     * @return Bitmap 位图
     * @throws WriterException
     */
    public Bitmap createCode(String string, Bitmap mBitmap, BarcodeFormat format)
            throws WriterException {
        Matrix m = new Matrix();
        float sx = (float) 2 * IMAGE_HALFWIDTH / mBitmap.getWidth();
        float sy = (float) 2 * IMAGE_HALFWIDTH
                / mBitmap.getHeight();
        m.setScale(sx, sy);//设置缩放信息
        //将logo图片按martix设置的信息缩放
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
                mBitmap.getWidth(), mBitmap.getHeight(), m, false);
        MultiFormatWriter writer = new MultiFormatWriter();
        Hashtable<EncodeHintType, String> hst = new Hashtable<EncodeHintType, String>();
        hst.put(EncodeHintType.CHARACTER_SET, "UTF-8");//设置字符编码
        BitMatrix matrix = writer.encode(string, format, 800, 800, hst);//生成二维码矩阵信息
        int width = matrix.getWidth();//矩阵高度
        int height = matrix.getHeight();//矩阵宽度
        int halfW = width / 2;
        int halfH = height / 2;
        int[] pixels = new int[width * height];//定义数组长度为矩阵高度*矩阵宽度，用于记录矩阵中像素信息
        for (int y = 0; y < height; y++) {//从行开始迭代矩阵
            for (int x = 0; x < width; x++) {//迭代列
                if (x > halfW - IMAGE_HALFWIDTH && x < halfW + IMAGE_HALFWIDTH
                        && y > halfH - IMAGE_HALFWIDTH
                        && y < halfH + IMAGE_HALFWIDTH) {//该位置用于存放图片信息
                    //记录图片每个像素信息
                    pixels[y * width + x] = mBitmap.getPixel(x - halfW
                            + IMAGE_HALFWIDTH, y - halfH + IMAGE_HALFWIDTH);
                } else {
                    if (matrix.get(x, y)) {//如果有黑块点，记录信息
                        pixels[y * width + x] = 0xff000000;//记录黑块信息
                    }
                }

            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }


}
