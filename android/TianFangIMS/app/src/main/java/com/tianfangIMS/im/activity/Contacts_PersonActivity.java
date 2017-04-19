package com.tianfangIMS.im.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

import com.tianfangIMS.im.R;

/**
 * Created by LiChong on 2017/1/15.
 */

public class Contacts_PersonActivity extends BaseActivity {
    private ListView lv_person_info;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_person_activity);
        mContext= this;
        setTitle("联系人");
    }

    public void initView() {
        lv_person_info = (ListView)this.findViewById(R.id.lv_person_info);
    }
}
