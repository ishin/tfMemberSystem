package com.tianfangIMS.im.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.SearchGroupBean;
import com.tianfangIMS.im.view.FrameView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LianMengYu on 2017/2/28.
 */

public class SearchGroupAdapter extends BaseAdapter {
    private List<SearchGroupBean> searchGroupBeen = new ArrayList<>();
    private Context mContext;

    public SearchGroupAdapter(List<SearchGroupBean> searchGroupBeen, Context mContext) {
        this.searchGroupBeen = searchGroupBeen;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return searchGroupBeen.size();
    }

    @Override
    public Object getItem(int position) {
        return searchGroupBeen.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DetailHolder mDetailHolder = null;
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
        Picasso.with(mContext)
                .load(ConstantValue.ImageFile + searchGroupBeen.get(position).getLogo())
                .resize(80, 80)
                .placeholder(R.mipmap.default_portrait)
                .config(Bitmap.Config.ARGB_8888)
                .error(R.mipmap.default_portrait)
                .into(mDetailHolder.groupHeader);
        mDetailHolder.groupName.setText(searchGroupBeen.get(position).getName());
        mDetailHolder.groupIndex.setText(searchGroupBeen.get(position).getName().substring(1, 2));
        return convertView;
    }

    private class DetailHolder {
        ImageView groupHeader;
        FrameView groupIndex;
        TextView groupName;
    }
}
