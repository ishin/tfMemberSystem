package com.tianfangIMS.im.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.ContactsPersonBean;

import java.util.List;

/**
 * Created by LiChong on 2017/1/15.
 * 部门人员Adapter
 */

public class Contacts_PersonAdapter extends BaseAdapter{
    private Context mContext;
    private List<ContactsPersonBean> mList;

    public Contacts_PersonAdapter(Context mContext, List<ContactsPersonBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
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
        ViewHodlerForPerson holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.contacts_department_item, null);
            holder = new ViewHodlerForPerson();
            holder.tv_person_departmentName = (TextView) convertView.findViewById(R.id.tv_department_name_item);
            holder.tv_person_departmentTxt = (TextView) convertView.findViewById(R.id.tv_dapartment_number_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHodlerForPerson) convertView.getTag();
        }
//        holder.tv_person_departmentName.setText(mList.get(position).getName());
//        holder.tv_person_departmentTxt.setText(mList.get(position).getId());
        return convertView;
    }
    class ViewHodlerForPerson {
        ImageView iv_person_photo;
        TextView tv_person_departmentName;
        TextView tv_person_departmentTxt;
    }
}
