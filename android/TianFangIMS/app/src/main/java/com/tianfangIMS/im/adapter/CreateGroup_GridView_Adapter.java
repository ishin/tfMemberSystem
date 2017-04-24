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
import com.tianfangIMS.im.bean.TreeInfo;

import java.util.List;

/**
 * Created by LianMengYu on 2017/2/20.
 */

public class CreateGroup_GridView_Adapter extends BaseAdapter {
    private Context mContext;
    private List<TreeInfo> mList;

    public CreateGroup_GridView_Adapter(Context mContext, List<TreeInfo> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler hodler;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.addcontacts_gridview, null);
            hodler = new ViewHodler();
            hodler.img = (ImageView) convertView.findViewById(R.id.iv_addfriend_photo);
            hodler.text = (TextView) convertView.findViewById(R.id.item_text);
            convertView.setTag(hodler);
        } else {
            hodler = (ViewHodler) convertView.getTag();
        }
        Picasso.with(mContext)
                .load(ConstantValue.ImageFile + mList.get(position).getLogo())
                .resize(500, 500)
                .placeholder(R.mipmap.default_photo)
                .error(R.mipmap.default_photo)
                .into(hodler.img);
        hodler.text.setText(mList.get(position).getName());
        return convertView;
    }

    class ViewHodler {
        ImageView img;
        TextView text;
    }
}
