package com.tianfangIMS.im.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.MineGroupParentBean;
import com.tianfangIMS.im.utils.CommonUtil;

import java.util.List;

/**
 * Created by LianMengYu on 2017/2/4.
 * 我的群组，根据不同数据加载不同布局
 */

public class MineGroupListAdapter extends BaseAdapter {

    private Context mContext;
    private List<MineGroupParentBean> ListGroup;
    private List<MineGroupParentBean> listItem;

    public MineGroupListAdapter(List<MineGroupParentBean> listGroup, List<MineGroupParentBean> listItem, Context mContext) {
        ListGroup = listGroup;
        this.listItem = listItem;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int position) {
        return listItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        if(ListGroup.contains(listItem.get(position))){
            return false;
        }
        return super.isEnabled(position);
    }
    //
//    @Override
//    public int getItemViewType(int position) {
//        int type = Integer.parseInt(mapList.get(position).get("flag"));
//        return type;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return 2;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChildHodler childHodler = null;
        ParentHolder parentHolder = null;
        if(convertView == null){
            childHodler = new ChildHodler();
            parentHolder = new ParentHolder();
            if(ListGroup.contains(listItem.get(position))){
                convertView = View.inflate(mContext,R.layout.mine_group_parent,null);
                childHodler.name = (TextView)convertView.findViewById(R.id.tv_parent_name);
            }else {
                convertView = View.inflate(mContext,R.layout.mine_group_child,null);
                childHodler.img = (ImageView) convertView.findViewById(R.id.iv_group_photo);
                childHodler.name = (TextView) convertView.findViewById(R.id.tv_group_name);
                convertView.setTag(childHodler);
            }
        }else{
            childHodler = (ChildHodler) convertView.getTag();
        }
//        parentHolder.name.setText("我的群组");
        CommonUtil.GetImages(mContext, listItem.get(position).getText().getICreate().get(position).getLogo(), childHodler.img);
        childHodler.name.setText(listItem.get(position).getText().getICreate().get(position).getName());
        return convertView;
    }
    class ParentHolder{
        TextView name;
    }
    class ChildHodler {
        ImageView img;
        TextView name;
    }
}
