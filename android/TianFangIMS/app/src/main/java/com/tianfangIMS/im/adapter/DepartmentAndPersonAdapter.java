package com.tianfangIMS.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianfangIMS.im.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.tianfangIMS.im.R.id.iv_person_photo;

/**
 * Created by LianMengYu on 2017/1/13.
 */

public class DepartmentAndPersonAdapter extends BaseAdapter {
    private Context mContext;

    private List<Map<String, String>> mList = new ArrayList<Map<String, String>>();

    private final int Department = 0;
    private final int person = 1;

    public DepartmentAndPersonAdapter(Context context, List<Map<String, String>> list) {
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
    public int getItemViewType(int position) {
        int type = Integer.parseInt(mList.get(position).get("flag"));
        return type;
    }

    @Override
    public int getViewTypeCount() {

        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodlerForDepartment viewHodlerForDepartment = null;
        ViewHodlerForPerson viewHodlerForPerson = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            switch (type) {
                case 0:
                    convertView = inflater.inflate(R.layout.contacts_department_item, parent, false);
                    viewHodlerForDepartment = new ViewHodlerForDepartment();
                    viewHodlerForDepartment.tv_department_name_item = (TextView) convertView.findViewById(R.id.tv_department_name_item);
                    viewHodlerForDepartment.tv_dapartment_number_item = (TextView) convertView.findViewById(R.id.tv_dapartment_number_item);
                    convertView.setTag(viewHodlerForDepartment);
                    break;
                case 1:
                    convertView = inflater.inflate(R.layout.contacts_person_item, parent, false);
                    viewHodlerForPerson = new ViewHodlerForPerson();
                    viewHodlerForPerson.iv_person_photo = (ImageView) convertView.findViewById(iv_person_photo);
                    viewHodlerForPerson.tv_person_departmentName = (TextView) convertView.findViewById(R.id.tv_person_departmentName);
                    viewHodlerForPerson.tv_person_departmentTxt = (TextView) convertView.findViewById(R.id.tv_person_departmentTxt);
                    convertView.setTag(viewHodlerForPerson);
                    break;
            }

        } else {
            switch (type) {
                case 0:
                    viewHodlerForDepartment = (ViewHodlerForDepartment) convertView.getTag();
                    break;
                case 1:
                    viewHodlerForPerson = (ViewHodlerForPerson) convertView.getTag();
                    break;
            }
        }
        switch (type) {
            case 0:
                viewHodlerForDepartment.tv_department_name_item.setText(mList.get(position).get("name"));
                viewHodlerForDepartment.tv_dapartment_number_item.setText(mList.get(position).get("id"));
                break;
            case 1:
                viewHodlerForPerson.tv_person_departmentName.setText(mList.get(position).get("name"));
                viewHodlerForPerson.tv_person_departmentTxt.setText(mList.get(position).get("mobile"));
                break;
        }
        return convertView;
    }

    class ViewHodlerForDepartment {
        TextView tv_department_name_item;
        TextView tv_dapartment_number_item;
    }

    class ViewHodlerForPerson {
        ImageView iv_person_photo;
        TextView tv_person_departmentName;
        TextView tv_person_departmentTxt;
    }
}
