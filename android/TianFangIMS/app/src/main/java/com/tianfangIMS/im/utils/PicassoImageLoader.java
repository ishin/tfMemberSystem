package com.tianfangIMS.im.utils;

import android.app.Activity;
import android.widget.ImageView;

import com.lzy.imagepicker.loader.ImageLoader;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.R;

import java.io.File;

/**
 * Created by LianMengYu on 2017/2/15.
 */

public class PicassoImageLoader implements ImageLoader {
    @Override
    public void clearMemoryCache() {

    }

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        Picasso.with(activity)//
                .load(new File(path))//
                .placeholder(R.mipmap.default_image)//
                .error(R.mipmap.default_image)//
                .resize(width, height)//
                .centerInside()//
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//
                .into(imageView);
    }
}
