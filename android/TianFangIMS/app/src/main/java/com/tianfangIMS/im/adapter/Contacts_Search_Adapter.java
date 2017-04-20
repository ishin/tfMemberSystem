package com.tianfangIMS.im.adapter;

import android.content.Context;
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
 * Created by LianMengYu on 2017/2/18.
 */

public class Contacts_Search_Adapter extends BaseAdapter {

    private TopContactsListBean mlist;
    private Context mContext;
    private int mLayoutId;

    public Contacts_Search_Adapter(Context mContext, TopContactsListBean mlist) {
        this.mContext = mContext;
        this.mlist = mlist;
    }

    @Override
    public int getCount() {
        return mlist.getText().size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.getText().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Hodler viewhodler;
        if(convertView == null){
            convertView = View.inflate(mContext,R.layout.contacts_person_item, null);
            viewhodler = new Hodler();
            viewhodler.userphoto = (ImageView)convertView.findViewById(R.id.iv_person_photo);
            viewhodler.username = (TextView)convertView.findViewById(R.id.tv_person_departmentName);
            viewhodler.pos = (TextView)convertView.findViewById(R.id.tv_person_departmentTxt);
            convertView.setTag(viewhodler);
        }else {
            viewhodler = (Hodler)convertView.getTag();
        }
        Picasso.with(mContext)
                .load(ConstantValue.ImageFile+mlist.getText().get(position).getLogo())
                .resize(500, 500)
                .placeholder(R.mipmap.default_photo)
                .error(R.mipmap.default_photo)
                .into(viewhodler.userphoto);
        viewhodler.username.setText(mlist.getText().get(position).getFullname());
        viewhodler.pos.setText(mlist.getText().get(position).getWorkno());
        return convertView;
    }
    class Hodler{
        ImageView userphoto;
        TextView username;
        TextView pos;//职位
    }
}
