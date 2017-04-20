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
import com.tianfangIMS.im.bean.GroupBean;

import java.util.ArrayList;

/**
 * Created by LianMengYu on 2017/2/17.
 */

public class GroupDetailInfo_GridView_Adapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<GroupBean> mList;
    private AddClickListener addListener;
    private DelClickListener delListener;
    private boolean flag;//true是群主，false不是群主

    public GroupDetailInfo_GridView_Adapter(Context mContext, ArrayList<GroupBean> mList, AddClickListener addListener, DelClickListener delListener, boolean flag) {
        this.mContext = mContext;
        this.mList = mList;
        this.addListener = addListener;
        this.delListener = delListener;
        this.flag = flag;
    }

    @Override
    public int getCount() {
        return mList.size() + 2;
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
        Hodler viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.addcontacts_gridview, null);
            viewHolder = new Hodler();
            viewHolder.iv_photo = (ImageView) convertView.findViewById(R.id.iv_addfriend_photo);
            viewHolder.tv_userName = (TextView) convertView.findViewById(R.id.item_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Hodler) convertView.getTag();
        }
        if (position < mList.size()) {
            Picasso.with(mContext)
                    .load(ConstantValue.ImageFile + mList.get(position).getLogo())
                    .resize(80, 80)
                    .placeholder(R.mipmap.default_portrait)
                    .config(Bitmap.Config.ARGB_8888)
                    .error(R.mipmap.default_portrait)
                    .into(viewHolder.iv_photo);
            viewHolder.tv_userName.setText(mList.get(position).getFullname());
        } else if (position == mList.size()) {
            viewHolder.iv_photo.setBackgroundResource(R.mipmap.add);
            viewHolder.iv_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addListener.AddclickListener(v);
                }
            });


        } else if (position == mList.size() + 1 && flag == true) {
            viewHolder.iv_photo.setBackgroundResource(R.mipmap.del);
            viewHolder.iv_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delListener.DelclickListener(v);
                }
            });
        }
        return convertView;
    }

    class Hodler {
        ImageView iv_photo;
        TextView tv_userName;
    }

    //自定义接口，用于回调按钮点击事件到Activity
    public static interface AddClickListener {
        public void AddclickListener(View v);
    }

    public interface DelClickListener {
        public void DelclickListener(View v);
    }


    public void setaddClickListener(AddClickListener e) {
        addListener = e;
    }

    public void setdelClickListener(DelClickListener e) {
        delListener = e;
    }
}