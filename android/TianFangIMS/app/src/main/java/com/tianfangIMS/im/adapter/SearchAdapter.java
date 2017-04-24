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
import com.tianfangIMS.im.bean.SearchUserBean;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Titan on 2017/2/9.
 */

public class SearchAdapter extends BaseAdapter {

    List<SearchUserBean> SearchUserBean = new ArrayList<>();

    Context mContext;


    public SearchAdapter(Context mContext, List<SearchUserBean> SearchUserBean) {
        this.mContext = mContext;
        this.SearchUserBean = SearchUserBean;
    }

    @Override
    public int getCount() {
        return SearchUserBean.size();
    }

    @Override
    public Object getItem(int position) {
        return SearchUserBean.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DetailHolder mDetailHolder = null;
        if (convertView == null) {
            mDetailHolder = new DetailHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.contacts_person_item, null);
            mDetailHolder.groupHeader = (ImageView) convertView.findViewById(R.id.iv_person_photo);
            mDetailHolder.groupName = (TextView) convertView.findViewById(R.id.tv_person_departmentName);
            mDetailHolder.pos = (TextView) convertView.findViewById(R.id.tv_person_departmentTxt);
//            mDetailHolder.groupIndex = (FrameView) convertView.findViewById(R.id.adapter_group_item_detail_index);
            convertView.setTag(mDetailHolder);
        } else {
            mDetailHolder = (DetailHolder) convertView.getTag();
        }

        Picasso.with(mContext)
                .load(ConstantValue.ImageFile + SearchUserBean.get(position).getLogo())
                .resize(80, 80)
                .placeholder(R.mipmap.default_portrait)
                .config(Bitmap.Config.ARGB_8888)
                .error(R.mipmap.default_portrait)
                .into(mDetailHolder.groupHeader);
        mDetailHolder.groupName.setText(SearchUserBean.get(position).getName());
        mDetailHolder.pos.setText(SearchUserBean.get(position).getPos());
//        mDetailHolder.groupIndex.setText(SearchUserBean.get(position).getName().substring(1, 2));
        return convertView;
    }

    private class DescHolder {
        TextView desc;
    }

    private class DetailHolder {
        ImageView groupHeader;
        //        FrameView groupIndex;
        TextView groupName;
        TextView pos;
    }
}
