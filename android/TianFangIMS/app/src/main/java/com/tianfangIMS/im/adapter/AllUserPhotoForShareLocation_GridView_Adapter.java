package com.tianfangIMS.im.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.LocationBean;

import java.util.List;

/**
 * Created by LianMengYu on 2017/3/3.
 */

public class AllUserPhotoForShareLocation_GridView_Adapter extends BaseAdapter{
    private List<LocationBean> mlist;
    private Context mContext;

    public AllUserPhotoForShareLocation_GridView_Adapter(List<LocationBean> mlist, Context mContext) {
        this.mlist = mlist;
        this.mContext = mContext;
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
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.amapshare_item,null);
            viewHolder.img =(ImageView)convertView.findViewById(R.id.iv_amapshare_photo);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Picasso.with(mContext)
                .load(ConstantValue.ImageFile+mlist.get(position).getLogo())
                .resize(50,50)
                .centerCrop()
                .error(R.mipmap.default_portrait)
                .into(viewHolder.img);
        return convertView;
    }
    class ViewHolder{
        ImageView img;
    }
}
