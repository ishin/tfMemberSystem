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
import com.tianfangIMS.im.bean.AddFriendTwoBean;

import java.util.List;

/**
 * Created by LianMengYu on 2017/2/5.
 */

public class GroupTopContacts_GridView_Adapter extends BaseAdapter {
    private Context mContext;
    private List<AddFriendTwoBean> mList;

    public GroupTopContacts_GridView_Adapter(Context mContext, List<AddFriendTwoBean> mList) {
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
//        LayoutInflater mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder viewHolder;
        if (convertView == null) {
//            convertView = mInflater.inflate(R.layout.addcontacts_gridview, null);
            convertView = View.inflate(mContext, R.layout.addcontacts_gridview, null);
            viewHolder = new ViewHolder();
            viewHolder.img = (ImageView) convertView.findViewById(R.id.iv_addfriend_photo);
            viewHolder.text = (TextView) convertView.findViewById(R.id.item_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        CommonUtil.GetImages(mContext, mList.get(position).getText().get(position).getLogo(), viewHolder.img);
        Picasso.with(mContext)
                .load(ConstantValue.ImageFile + mList.get(position).getLogo())
                .resize(50, 50)
                .placeholder(R.mipmap.default_portrait)
                .error(R.mipmap.default_portrait)
                .into(viewHolder.img);
        viewHolder.text.setText(mList.get(position).getFullname());
        return convertView;
    }

    class ViewHolder {
        ImageView img;
        TextView text;
    }
}
