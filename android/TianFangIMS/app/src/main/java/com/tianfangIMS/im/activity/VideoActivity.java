package com.tianfangIMS.im.activity;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tianfangIMS.im.utils.CircleImage;
import com.tianfangIMS.im.R;

import io.rong.ptt.PTTClient;

/**
 * Created by Titan on 2017/2/5.
 */

public class VideoActivity extends BaseActivity implements View.OnClickListener {

    ImageView main_call_blur;
    ImageView main_call_header;

    ImageView main_call_free, main_call_flash, main_call_talk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intercom_layout);
        setTitle("对讲");
        main_call_blur = (ImageView) findViewById(R.id.main_call_blur);
        main_call_header = (ImageView) findViewById(R.id.main_call_header);
        main_call_free = (ImageView) findViewById(R.id.main_call_free);
        main_call_flash = (ImageView) findViewById(R.id.main_call_flash);
        main_call_talk = (ImageView) findViewById(R.id.main_call_talk);
        setListener();

    }



    private void setListener() {
        main_call_free.setOnClickListener(this);
        main_call_flash.setOnClickListener(this);
        main_call_talk.setOnClickListener(this);
    }

//    private Bitmap blur(Bitmap bitmap, float radius) {
//        Bitmap output = Bitmap.createBitmap(bitmap); // 创建输出图片
//        RenderScript rs = RenderScript.create(this); // 构建一个RenderScript对象
//        ScriptIntrinsicBlur gaussianBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs)); // 创建高斯模糊脚本
//        Allocation allIn = Allocation.createFromBitmap(rs, bitmap); // 创建用于输入的脚本类型
//        Allocation allOut = Allocation.createFromBitmap(rs, output); // 创建用于输出的脚本类型
//        gaussianBlur.setRadius(radius); // 设置模糊半径，范围0f<radius<=25f
//        gaussianBlur.setInput(allIn); // 设置输入脚本类型
//        gaussianBlur.forEach(allOut); // 执行高斯模糊算法，并将结果填入输出脚本类型中
//        allOut.copyTo(output); // 将输出内存编码为Bitmap，图片大小必须注意
//        rs.finish();
//        rs.destroy(); // 关闭RenderScript对象，API>=23则使用rs.releaseAllContexts()
//        return output;
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_call_free:
                Toast.makeText(this, "点击了免提", Toast.LENGTH_SHORT).show();
                break;
            case R.id.main_call_flash:
                Toast.makeText(this, "点击了Flash", Toast.LENGTH_SHORT).show();
                break;
            case R.id.main_call_talk:
                Toast.makeText(this, "点击了对讲", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
