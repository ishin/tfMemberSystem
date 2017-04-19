package com.tianfangIMS.im.adapter;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.tianfangIMS.im.bean.GroupBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LianMengYu on 2017/3/13.
 */

public class MoveGroupUserAdapter extends BaseAdapter {
    private Context mContext;
    private List<GroupBean> list;
    private boolean flag;
    private Map<Integer, Boolean> chackedMap;

    public MoveGroupUserAdapter(Context mContext, List<GroupBean> list, boolean flag) {
        this.mContext = mContext;
        this.list = list;
        this.flag = flag;
        chackedMap = new HashMap<>();
        initCheckBox(false);
    }

    public void initCheckBox(boolean isChecked) {
        for (int i = 0; i < list.size(); i++) {
            chackedMap.put(i, isChecked);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHodler hodler = null;
        if (convertView == null) {
            hodler = new ViewHodler();
            convertView = View.inflate(mContext, R.layout.movegroupuser_item, null);
            hodler.img = (ImageView) convertView.findViewById(R.id.iv_movegroupuser_photo);
            hodler.name = (TextView) convertView.findViewById(R.id.tv_movegroupuser_departmentName);
            hodler.cb_adddel = (CheckBox) convertView.findViewById(R.id.cb_adddel);
            if (flag) {
                hodler.cb_adddel.setVisibility(View.VISIBLE);
            }
            convertView.setTag(hodler);
        } else {
            hodler = (ViewHodler) convertView.getTag();
        }
        hodler.cb_adddel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                chackedMap.put(position,isChecked);
            }
        });
        hodler.cb_adddel.setChecked(chackedMap.get(position));
        Picasso.with(mContext)
                .load(ConstantValue.ImageFile + list.get(position).getLogo())
                .resize(80, 80)
                .placeholder(R.mipmap.default_portrait)
                .config(Bitmap.Config.ARGB_8888)
                .error(R.mipmap.default_portrait)
                .into(hodler.img);
        hodler.name.setText(list.get(position).getFullname());
        return convertView;
    }

    public class ViewHodler {
        public CheckBox cb_adddel;
        ImageView img;
        TextView name;
    }
    /**
     * 得到勾选状态的集合
     * @return
     */
    public Map<Integer,Boolean> getCheckedMap() {
        return chackedMap;
    }
}
