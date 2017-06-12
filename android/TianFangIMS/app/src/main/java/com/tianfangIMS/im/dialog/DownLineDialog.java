package com.tianfangIMS.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by LianMengYu on 2017/3/12.
 */

public class DownLineDialog extends Dialog{
    private Context mContext;
    public DownLineDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}

