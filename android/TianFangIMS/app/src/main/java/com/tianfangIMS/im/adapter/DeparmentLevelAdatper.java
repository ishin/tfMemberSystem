package com.tianfangIMS.im.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.TreeInfo;

import java.util.ArrayList;

/**
 * Created by LianMengYu on 2017/2/22.
 * 我的模块，部门层级Adapter
 */

public class DeparmentLevelAdatper extends BaseAdapter {

    private ArrayList<TreeInfo> mlist;
    private Context mContext;

    public DeparmentLevelAdatper(Context mContext, ArrayList<TreeInfo> mlist) {
        this.mContext = mContext;
        this.mlist = mlist;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.departmentlevel_item, null);
            TextView textView = (TextView) convertView.findViewById(R.id.tv_position);
            textView.setText(mlist.get(position).getName());
        }
        return convertView;
    }
}
