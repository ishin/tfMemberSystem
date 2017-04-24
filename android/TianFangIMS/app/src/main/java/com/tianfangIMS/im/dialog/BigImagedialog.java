package com.tianfangIMS.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.R;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by LianMengYu on 2017/3/5.
 */

public class BigImagedialog extends Dialog {
    private Context mContext;
    private ImageView iv_bigimage;
    private String path;
    private PhotoViewAttacher photoViewAttacher;
    private int theme;
    public BigImagedialog(Context context, String path,int theme) {
        super(context);
        this.mContext = context;
        this.path = path;
        this.theme = theme;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.bigimage_dialog, null);
        setContentView(view);
        iv_bigimage = (ImageView)view.findViewById(R.id.iv_bigimage);
        Picasso.with(mContext)
                .load(path)
                .into(iv_bigimage);
        photoViewAttacher = new PhotoViewAttacher(iv_bigimage);
        photoViewAttacher.update();
    }
}
