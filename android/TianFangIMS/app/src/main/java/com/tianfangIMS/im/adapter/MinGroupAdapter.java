package com.tianfangIMS.im.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.MineGroupParentBean;
import com.tianfangIMS.im.utils.CommonUtil;

import java.util.List;

/**
 * Created by LianMengYu on 2017/2/3.
 * 我的群组可折叠适配器
 */

public class MinGroupAdapter extends BaseExpandableListAdapter {

    private Context mContext;
//    private List<Map<String,String>> parentBeen;
//    private List<ArrayList<Map<String,String>>> childBeen;
    private List<MineGroupParentBean> parentBeen;
    private List<List<MineGroupParentBean>> childBeen;

    public MinGroupAdapter(List<List<MineGroupParentBean>> childBeen, Context mContext, List<MineGroupParentBean> parentBeen) {
        this.childBeen = childBeen;
        this.mContext = mContext;
        this.parentBeen = parentBeen;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childBeen.get(groupPosition).get(childPosition);
    }

    @Override
    public int getGroupCount() {
        return parentBeen.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childBeen.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentBeen.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentHodler parentHodler = null;
        if(convertView == null){
            convertView = View.inflate(mContext,R.layout.mine_group_parent,null);
            parentHodler = new ParentHodler();
            parentHodler.name = (TextView)convertView.findViewById(R.id.tv_parent_name);
            convertView.setTag(parentHodler);
        }else {
            parentHodler = (ParentHodler)convertView.getTag();
        }
        parentHodler.name.setText(parentBeen.get(groupPosition).getText().getICreate().get(groupPosition).getName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHodler childhodler = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.mine_group_child, null);
            childhodler = new ChildHodler();
            childhodler.img = (ImageView) convertView.findViewById(R.id.iv_group_photo);
            childhodler.name = (TextView) convertView.findViewById(R.id.tv_group_name);
            convertView.setTag(childhodler);
        }else {
            childhodler = (ChildHodler)convertView.getTag();
        }
        CommonUtil.GetImages(mContext,parentBeen.get(groupPosition).getText().getICreate().get(childPosition).getLogo(),childhodler.img);
        childhodler.name.setText(childBeen.get(groupPosition).get(childPosition).getText().getICreate().get(childPosition).getName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    class ParentHodler{
        TextView name;
    }
    class ChildHodler{
        ImageView img;
        TextView name;
    }
}
