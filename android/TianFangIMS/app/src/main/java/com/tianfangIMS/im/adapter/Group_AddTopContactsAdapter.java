package com.tianfangIMS.im.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.TopContactsListBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LianMengYu on 2017/2/5.
 */

public class Group_AddTopContactsAdapter extends BaseAdapter {
    private Context mContext;
        private TopContactsListBean mList;
//    private List<AddFriendBean> mList;
    //    private TopContactsListBean mList;
    //存储CheckBox状态的集合
    private Map<String, Boolean> checkedMap;

    public Group_AddTopContactsAdapter(Context mContext, TopContactsListBean mList) {
        this.mContext = mContext;
        this.mList = mList;
        checkedMap = new HashMap<>();
        initCheckBox(false);
    }

    /**
     * 初始化Map集合
     *
     * @param isChecked CheckBox状态
     */
    public void initCheckBox(boolean isChecked) {
        for (int i = 0; i < mList.getText().size(); i++) {
            checkedMap.put(mList.getText().get(i).getId(), isChecked);
        }
    }

    @Override
    public int getCount() {
        return mList.getText().size();
    }

    @Override
    public Object getItem(int position) {
        return mList.getText().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
       final ViewHodler viewHodler;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.contacts_group_item, null);
            viewHodler = new ViewHodler();
            viewHodler.cb_addfrien = (CheckBox) convertView.findViewById(R.id.cb_addfrien);
            viewHodler.img = (ImageView) convertView.findViewById(R.id.iv_person_photo);
            viewHodler.name = (TextView) convertView.findViewById(R.id.tv_person_departmentName);
            viewHodler.level = (TextView) convertView.findViewById(R.id.tv_person_departmentTxt);
            viewHodler.iv_checkbox_grouptop = (ImageView)convertView.findViewById(R.id.iv_checkbox_grouptop);
            convertView.setTag(viewHodler);
        } else {
            viewHodler = (ViewHodler) convertView.getTag();
        }
        viewHodler.cb_addfrien.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 当勾选框状态发生改变时,重新存入map集合
                checkedMap.put(mList.getText().get(position).getId(), isChecked);

                if(buttonView.isChecked()){
                    viewHodler.iv_checkbox_grouptop.setImageResource(R.mipmap.checkbox_selected);
                }else{
                    viewHodler.iv_checkbox_grouptop.setImageResource(R.mipmap.checkbox_selectring);
                }

            }
        });
        viewHodler.cb_addfrien.setChecked(checkedMap.get(mList.getText().get(position).getId()));
//        CommonUtil.GetImages(mContext,mList.getText().get(position).getLogo(),viewHodler.img);
        Picasso.with(mContext)
                .load(ConstantValue.ImageFile + mList.getText().get(position).getLogo())
                .resize(50, 50)
                .placeholder(R.mipmap.default_portrait)
                .error(R.mipmap.default_portrait)
                .into(viewHodler.img);
        viewHodler.name.setText(mList.getText().get(position).getFullname());
//        viewHodler.level.setText(mList.get(position).getText();
        return convertView;
    }

    public class ViewHodler {
        public CheckBox cb_addfrien;//选择
        ImageView img;
        TextView name;
        TextView level;
        ImageView iv_checkbox_grouptop;
    }

    /**
     * 得到勾选状态的集合
     * @return
     */
    public Map<String, Boolean> getCheckedMap() {
        return checkedMap;
    }
}
