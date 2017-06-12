package com.tianfangIMS.im.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.tianfangIMS.im.R;

/**
 * Created by LianMengYu on 2017/4/26.
 */

public class ScanActivity extends CaptureActivity {

    private LinearLayout parentLinearLayout; //把父类的activity和子类的activity的view都add到这里来
    private ImageButton scan_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.scan_layout);
        initContentView(R.layout.scan_layout);
        scan_back = (ImageButton) this.findViewById(R.id.scan_back);
        scan_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initContentView(int layoutResID) {
        // TODO Auto-generated method stub
        ViewGroup group = (ViewGroup) findViewById(android.R.id.content);  //得到窗口的根布局
//        group.removeAllViews(); //首先先移除在根布局上的组件
        //创建自定义父布局
        parentLinearLayout  = new LinearLayout(this);
        parentLinearLayout.setOrientation(LinearLayout.VERTICAL);
        group.addView(parentLinearLayout); //将自定义的父布局，加载到窗口的根布局上
        LayoutInflater.from(this).inflate(layoutResID, parentLinearLayout, true);//这句话的意思就是将自定义的子布局加到parentLinearLayout上，true的意思表示添加上去
    }
}
