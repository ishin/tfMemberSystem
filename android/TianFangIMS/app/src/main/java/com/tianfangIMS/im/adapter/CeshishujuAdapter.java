package com.tianfangIMS.im.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.MineGroupParentBean;
import com.tianfangIMS.im.utils.CommonUtil;

import java.util.List;

/**
 * Created by LianMengYu on 2017/2/4.
 */

public class CeshishujuAdapter extends BaseAdapter {

    private Context mContext;
    private List<MineGroupParentBean> mlist;

    public CeshishujuAdapter(Context mContext, List<MineGroupParentBean> mlist) {
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
        ChildHodler childHodler = null;
        if(convertView == null){
            convertView = View.inflate(mContext, R.layout.mine_group_child,null);
            childHodler.img = (ImageView) convertView.findViewById(R.id.iv_group_photo);
            childHodler.name = (TextView) convertView.findViewById(R.id.tv_group_name);
            convertView.setTag(childHodler);
        }else {
            childHodler = (ChildHodler) convertView.getTag();
        }
        CommonUtil.GetImages(mContext, mlist.get(position).getText().getICreate().get(position).getLogo(), childHodler.img);
        childHodler.name.setText(mlist.get(position).getText().getICreate().get(position).getName());
        return convertView;
    }

    class ChildHodler {
        ImageView img;
        TextView name;
    }
}
