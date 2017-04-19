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
import com.tianfangIMS.im.bean.SearchAllBean;
import com.tianfangIMS.im.view.FrameView;

import java.util.List;

/**
 * Created by LianMengYu on 2017/3/11.
 */

public class SearchAllContactsAdapter extends BaseAdapter {
    private Context mContext;
    private List<SearchAllBean> mlist;

    public SearchAllContactsAdapter(Context mContext, List<SearchAllBean> mlist) {
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
    public boolean isEnabled(int position) {
        return Integer.valueOf(mlist.get(position).getId()) == -1 ? false : true;
    }

    @Override
    public int getItemViewType(int position) {
        return Integer.valueOf(mlist.get(position).getId()) == -1 ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        typeHodler typeHodler = null;
        DetailHolder detailHolder = null;
        switch (getItemViewType(position)) {
            case 0:
                if (convertView == null) {
                    typeHodler = new typeHodler();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.search_alltype_item, null);
                    typeHodler.typeName = (TextView) convertView.findViewById(R.id.tv_type);
                    convertView.setTag(typeHodler);
                } else {
                    typeHodler = (typeHodler) convertView.getTag();
                }
                typeHodler.typeName.setText(mlist.get(position).getName());
                break;
            case 1:
                if (convertView == null) {
                    detailHolder = new DetailHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.searchchatting_item, null);
                    detailHolder.logo = (ImageView) convertView.findViewById(R.id.iv_seacchchatting_photo);
                    detailHolder.name = (TextView) convertView.findViewById(R.id.tv_seacchchatting_Name);
                    detailHolder.pos = (TextView) convertView.findViewById(R.id.tv_seacchchatting_Txt);
                    detailHolder.index = (FrameView) convertView.findViewById(R.id.fv_searchchat_index);
                    convertView.setTag(detailHolder);
                } else {
                    detailHolder = (DetailHolder) convertView.getTag();
                }
                if(mlist.get(position).isFlag()){
                    detailHolder.index.setVisibility(View.GONE);
                }
                Picasso.with(mContext)
                        .load(ConstantValue.ImageFile + mlist.get(position).getLogo())
                        .resize(80, 80)
                        .placeholder(R.mipmap.default_portrait)
                        .config(Bitmap.Config.ARGB_8888)
                        .error(R.mipmap.default_portrait)
                        .into(detailHolder.logo);
                detailHolder.name.setText(mlist.get(position).getName());
                detailHolder.pos.setText(mlist.get(position).getPosition());
                detailHolder.index.setText(mlist.get(position).getName().substring(1, 2));
                break;
        }
        return convertView;
    }

    private class typeHodler {
        TextView typeName;
    }

    private class DetailHolder {
        ImageView logo;
        TextView name;
        TextView pos;
        FrameView index;
    }
}
