package com.tianfangIMS.im.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.TopContactsListBean;

/**
 * Created by LianMengYu on 2017/2/3.
 */

public class TopContactsAdapter extends BaseAdapter {
    private Context mContext;
    private TopContactsListBean list;

    public TopContactsAdapter(TopContactsListBean list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return list.getText().size();
    }

    @Override
    public Object getItem(int position) {
        return list.getText().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHodler viewHodler;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.contacts_person_item, null);
            viewHodler = new ViewHodler();
            viewHodler.img = (ImageView) convertView.findViewById(R.id.iv_person_photo);
            viewHodler.name = (TextView) convertView.findViewById(R.id.tv_person_departmentName);
            viewHodler.level = (TextView) convertView.findViewById(R.id.tv_person_departmentTxt);
            convertView.setTag(viewHodler);
        } else {
            viewHodler = (ViewHodler) convertView.getTag();
        }
//        CommonUtil.GetImages(mContext,list.getText().get(position).getLogo(),viewHodler.img);
        Picasso.with(mContext)
                .load(ConstantValue.ImageFile + list.getText().get(position).getLogo())
                .resize(80, 80)
                .placeholder(R.mipmap.default_portrait)
                .config(Bitmap.Config.ARGB_8888)
                .error(R.mipmap.default_portrait)
                .into(viewHodler.img);
        viewHodler.name.setText(list.getText().get(position).getFullname());
        viewHodler.level.setText(list.getText().get(position).getPosition());
        return convertView;
    }

    public class ViewHodler {
        ImageView img;
        TextView name;
        TextView level;
    }
}
