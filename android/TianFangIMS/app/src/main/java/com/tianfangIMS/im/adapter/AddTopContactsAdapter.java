package com.tianfangIMS.im.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.AddFriendBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LianMengYu on 2017/1/23.
 */

public class AddTopContactsAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<AddFriendBean> mList;
    public static HashMap<String, Boolean> isSelectedCheck;
    //存储CheckBox状态的集合
    private static Map<String, Boolean> checkedMap;
    Map<String, AddFriendBean> maps;
    public setOnCheckedData mListener;

    public AddTopContactsAdapter(Context mContext, ArrayList<AddFriendBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
//        checkedMap = new HashMap<>();
//        initCheckBox(false);
    }

    public interface setOnCheckedData {
        void OnCheckedData();
    }

    public void setOnCheckedData(setOnCheckedData mListener) {
        this.mListener = mListener;
    }

    //主要是设置maps
    public void setMaps(Map<String, AddFriendBean> maps) {
        this.maps = maps;
    }

    /**
     * 初始化Map集合
     *
     * @param isChecked CheckBox状态
     */
    public void initCheckBox(boolean isChecked) {
//        for (int i = 0; i < mList.size(); i++) {
//            getCheckedMap().put(mList.get(i).getId(), isChecked);
//        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public AddFriendBean getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.addtopcontacts_item, null);
            holder = new Holder();
            holder.cb_addfrien = (CheckBox) convertView.findViewById(R.id.cb_addfrien);
            holder.AddFriendPhoto = (ImageView) convertView.findViewById(R.id.iv_addfriend_photo);
            holder.AddFriendName = (TextView) convertView.findViewById(R.id.tv_addfriend_Name);
            holder.AddFriendLevel = (TextView) convertView.findViewById(R.id.tv_addfriend_Txt);
            holder.iv_checkbox = (ImageView) convertView.findViewById(R.id.iv_checkbox);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        //如果maps包含该Key值 则表明之前界面曾被选中过
        if (maps.containsKey(mList.get(position).getId())) {
            mList.get(position).setChecked(true);
            holder.iv_checkbox.setImageResource(R.mipmap.checkbox_selected);
        } else {
            holder.iv_checkbox.setImageResource(R.mipmap.checkbox_selectring);
        }
        holder.iv_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList.get(position).isChecked()) {
                    mList.get(position).setChecked(false);
                    maps.remove(mList.get(position).getId());
                    holder.iv_checkbox.setImageResource(R.mipmap.checkbox_selectring);
                } else {
                    mList.get(position).setChecked(true);
                    maps.put(mList.get(position).getId(), mList.get(position));
                    holder.iv_checkbox.setImageResource(R.mipmap.checkbox_selected);
                }
                mListener.OnCheckedData();
            }
        });
//        holder.iv_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                // 当勾选框状态发生改变时,重新存入map集合
////                if (!TextUtils.isEmpty(mList.get(position).getId())) {
////                    checkedMap.put(mList.get(position).getId(), isChecked);
////                }
//                if (isChecked) {
//                    mList.get(position).setChecked(true);
//                    maps.put(mList.get(position).getId(),mList.get(position));
//                    holder.iv_checkbox.setImageResource(R.mipmap.checkbox_selected);
//                } else {
//                    mList.get(position).setChecked(false);
//                    maps.remove(mList.get(position).getId(),mList.get(position));
//                    holder.iv_checkbox.setImageResource(R.mipmap.checkbox_selectring);
//                }
//            }
//        });
//        holder.cb_addfrien.setChecked(getCheckedMap().get(mList.get(position).getId()));
        Picasso.with(mContext)
                .load(ConstantValue.ImageFile + mList.get(position).getLogo())
                .resize(50, 50)
                .placeholder(R.mipmap.default_portrait)
                .error(R.mipmap.default_portrait)
                .into(holder.AddFriendPhoto);
        holder.AddFriendName.setText(mList.get(position).getName());
        holder.AddFriendLevel.setText(mList.get(position).getPositionname());
        return convertView;
    }


    public class Holder {
        public CheckBox cb_addfrien;//选择
        ImageView AddFriendPhoto;//好友头像
        TextView AddFriendName;//好友名字
        TextView AddFriendLevel;//还有级别
        ImageView iv_checkbox;
    }

    public static void isSelected(HashMap<String, Boolean> isSelected) {
        AddTopContactsAdapter.checkedMap = isSelected;
    }

    /**
     * 得到勾选状态的集合
     *
     * @return
     */
    public Map<String, Boolean> getCheckedMaps() {
        return checkedMap;
    }
}