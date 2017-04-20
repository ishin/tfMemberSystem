package com.tianfangIMS.im.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.DepartmentBean;

import java.util.List;

/**
 * Created by LianMengYu on 2017/1/12.
 */

public class Contacts_DepartmentAdapter extends BaseAdapter {
    private Context mContext;

    private List<DepartmentBean> mList;


    public Contacts_DepartmentAdapter(Context context, List<DepartmentBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.contacts_department_item, null);
            holder = new Holder();
            holder.tv_department_name_item = (TextView) convertView.findViewById(R.id.tv_department_name_item);
            holder.tv_dapartment_number_item = (TextView) convertView.findViewById(R.id.tv_dapartment_number_item);
            convertView.setTag(holder);
        } else {

            holder = (Holder) convertView.getTag();
        }
        holder.tv_department_name_item.setText(mList.get(position).getName());
        holder.tv_dapartment_number_item.setText(mList.get(position).getId());
        return convertView;
    }

    class Holder {
        TextView tv_department_name_item;
        TextView tv_dapartment_number_item;
    }
}
