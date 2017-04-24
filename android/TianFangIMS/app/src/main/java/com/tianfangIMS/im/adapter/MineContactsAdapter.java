package com.tianfangIMS.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.tianfangIMS.im.R;

import java.util.List;
import java.util.Map;

/**
 * Created by LianMengYu on 2017/1/11.
 */

public class MineContactsAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private Map<String, List<String>> dataTest;
    private String[] parentList;

    public  MineContactsAdapter(Context mContext, Map<String, List<String>> dataTest, String[] parentList) {
        this.mContext = mContext;
        this.dataTest = dataTest;
        this.parentList = parentList;
    }

    //  获得某个父项的某个子项
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return dataTest.get(parentList[groupPosition]).get(childPosition);
    }

    //  获得父项的数量
    @Override
    public int getGroupCount() {
        return dataTest.size();
    }

    //  获得某个父项的子项数目
    @Override
    public int getChildrenCount(int groupPosition) {
        return dataTest.get(parentList[groupPosition]).size();
    }

    //  获得某个父项
    @Override
    public Object getGroup(int groupPosition) {
        return dataTest.get(parentList[groupPosition]);
    }

    //  获得某个父项的id
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //  获得某个父项的某个子项的id
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //  按函数的名字来理解应该是是否具有稳定的id，这个方法目前一直都是返回false，没有去改动过
    @Override
    public boolean hasStableIds() {
        return false;
    }

    //  获得父项显示的view
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentHolder parentHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.minecontacts_listview_parentitem, null);
            parentHolder = new ParentHolder();
            parentHolder.parentText = (TextView) convertView.findViewById(R.id.tv_contactsList_item);
            convertView.setTag(parentHolder);
        } else {
            parentHolder = (ParentHolder) convertView.getTag();
        }
        parentHolder.parentText.setText(parentList[groupPosition]);
        return convertView;
    }

    //  获得子项显示的view
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    //  子项是否可选中，如果需要设置子项的点击事件，需要返回true
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class ParentHolder {
        TextView parentText;
    }

}
