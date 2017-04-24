package com.tianfangIMS.im.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tianfangIMS.im.R;
import com.tianfangIMS.im.adapter.MoveGroupUserAdapter;
import com.tianfangIMS.im.bean.GroupBean;
import com.tianfangIMS.im.dialog.MoveGroupUserDialog;

import java.util.ArrayList;

/**
 * Created by LianMengYu on 2017/3/12.
 */
public class MoveGroupUserActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private Context mContext;
    private ListView lv_moveuser;
    private String GroupID;
    private ArrayList<GroupBean> list;
    private boolean flag = false;

    @Override  
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movegroupuser_layout);
        setTitle("转移群组");
        mContext = this;
        GroupID = getIntent().getStringExtra("fromConversationId");
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        Object object = bundle.get("GroupBeanList");
        list = (ArrayList<GroupBean>) object;
        init();
        MoveGroupUserAdapter adapter = new MoveGroupUserAdapter(this, list, flag);
        lv_moveuser.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void init() {
        lv_moveuser = (ListView) this.findViewById(R.id.lv_moveuser);
        lv_moveuser.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MoveGroupUserDialog dialog = new MoveGroupUserDialog(mContext, GroupID, list, position);
        dialog.show();
    }
}
