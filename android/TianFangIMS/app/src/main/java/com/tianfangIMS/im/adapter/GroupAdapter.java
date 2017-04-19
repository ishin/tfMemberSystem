package com.tianfangIMS.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.GroupBean;
import com.tianfangIMS.im.view.FrameView;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * Created by Titan on 2017/2/9.
 */

public class GroupAdapter extends BaseAdapter {

    List<GroupBean> mGroupBeen;

    Context mContext;

    public GroupAdapter(List<GroupBean> groupBeen, Context context) {
        mGroupBeen = groupBeen;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mGroupBeen.size();
    }

    @Override
    public GroupBean getItem(int position) {
        return mGroupBeen.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return Integer.valueOf(getItem(position).getGID()) == -1 ? false : true;
    }

    @Override
    public int getItemViewType(int position) {
        return Integer.valueOf(getItem(position).getGID()) == -1 ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DescHolder mDescHolder = null;
        DetailHolder mDetailHolder = null;
        switch (getItemViewType(position)) {
            case 0:
                if (convertView == null) {
                    mDescHolder = new DescHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_group_item_desc, null);
                    mDescHolder.desc = (TextView) convertView.findViewById(R.id.adapter_group_item_desc_content);
                    convertView.setTag(mDescHolder);
                } else {
                    mDescHolder = (DescHolder) convertView.getTag();

                }
                mDescHolder.desc.setText(getItem(position).getName());
                break;
            case 1:
                if (convertView == null) {
                    mDetailHolder = new DetailHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_group_item_detail, null);
                    mDetailHolder.groupHeader = (ImageView) convertView.findViewById(R.id.adapter_group_item_detail_header);
                    mDetailHolder.groupName = (TextView) convertView.findViewById(R.id.adapter_group_item_detail_name);
                    mDetailHolder.groupIndex = (FrameView) convertView.findViewById(R.id.adapter_group_item_detail_index);
                    convertView.setTag(mDetailHolder);
                } else {
                    mDetailHolder = (DetailHolder) convertView.getTag();
                }

                Glide.with(mContext).load(ConstantValue.ImageFile + getItem(position).getLogo()).bitmapTransform(new CropCircleTransformation(mContext)).into(mDetailHolder.groupHeader);
//                Picasso.with(mContext)
//                        .load(ConstantValue.ImageFile + getItem(position).getLogo())
//                        .into(mDetailHolder.groupHeader);
                mDetailHolder.groupName.setText(getItem(position).getName());
                mDetailHolder.groupIndex.setText(getItem(position).getName().substring(1, 2));
                break;
        }
        return convertView;
    }

    private class DescHolder {
        TextView desc;
    }

    private class DetailHolder {
        ImageView groupHeader;
        FrameView groupIndex;
        TextView groupName;
    }
}
