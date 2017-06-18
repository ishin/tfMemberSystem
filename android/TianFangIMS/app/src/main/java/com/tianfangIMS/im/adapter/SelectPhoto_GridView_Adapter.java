package com.tianfangIMS.im.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.ViewMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LianMengYu on 2017/2/15.
 */

public class SelectPhoto_GridView_Adapter extends BaseAdapter {
    private Context mContext;
    private List<Uri> list;
    //存储CheckBox状态的集合
    private Map<Integer, Boolean> checkedMap;
    OnDepartmentCheckedChangeListener mListener;
    ViewMode mMode;

    public SelectPhoto_GridView_Adapter(List<Uri> list, Context mContext, ViewMode mMode) {
        this.list = list;
        this.mContext = mContext;
        this.mMode = mMode;
        checkedMap = new HashMap<>();
        initCheckBox(false);
    }

    /**
     * 初始化Map集合
     *
     * @param isChecked CheckBox状态
     */
    public void initCheckBox(boolean isChecked) {
        for (int i = 0; i < list.size(); i++) {
            checkedMap.put(i, isChecked);
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
            convertView = View.inflate(mContext, R.layout.item_select_gridview_photo, null);
            hodler.img = (ImageView) convertView.findViewById(R.id.select_imageView_GridView);
            hodler.checkBox = (CheckBox) convertView.findViewById(R.id.cb_selectphoto);
            hodler.checkBox.bringToFront();
            convertView.setTag(hodler);
        } else {
            hodler = (ViewHodler) convertView.getTag();
        }
        Log.e("选择照片的Adapter：", "---:" + list.get(position));
        Picasso.with(mContext)
                .load(list.get(position))
                .resize(500, 500)
                .into(hodler.img);
        if (mMode == ViewMode.CHECK) {
            hodler.checkBox.setVisibility(View.VISIBLE);
            hodler.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checkedMap.put(position, isChecked);
                }
            });
            hodler.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onCheckedChange(v);
                }
            });
        }
        hodler.checkBox.setChecked(checkedMap.get(position));
        return convertView;
    }

    public class ViewHodler {
        public CheckBox checkBox;
        ImageView img;
    }

    /**
     * 得到勾选状态的集合
     */
    public Map<Integer, Boolean> getCheckedMap() {
        return checkedMap;
    }

    public void setOnDepartmentCheckedChangeListener(OnDepartmentCheckedChangeListener mListener) {
        this.mListener = mListener;
    }

    public interface OnDepartmentCheckedChangeListener {
//        /**
//         * 被选中
//         *
//         * @param pid          所选项的PID
//         * @param id           所选项ID
//         * @param isDepartment 是否为部门类型
//         */
//        void onChecked(int pid, int id, int position, boolean isDepartment);
//
//        /**
//         * 被取消选中
//         *
//         * @param pid
//         * @param id
//         * @param isDepartment
//         */
//        void onCancel(int pid, int id, int position, boolean isDepartment);

        void onCheckedChange(View v);
    }

}
