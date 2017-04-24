package com.tianfangIMS.im.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.TreeInfo;
import com.tianfangIMS.im.bean.ViewMode;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Titan on 2017/2/7.
 */

public class InfoAdapter extends BaseAdapter {

    Context mContext;
    List<TreeInfo> mInfos;
    List<Integer> childCount;
    TreeInfo mTreeInfo;
    HashMap<Integer, Boolean> prepare;
    OnDepartmentCheckedChangeListener mListener;
    ViewMode mMode;

    /**
     * @param context
     * @param treeInfos
     * @param childCount
     * @param mode
     * @param prepare    mode值为ViewMode.NORMAL时 prepare可传空
     */

    public InfoAdapter(Context context, List<TreeInfo> treeInfos, List<Integer> childCount, ViewMode mode, HashMap<Integer, Boolean> prepare) {
        mContext = context;
        this.mInfos = treeInfos;
        this.childCount = childCount;
        this.mMode = mode;
        this.prepare = prepare;
    }

    @Override
    public int getCount() {
        return mInfos.size();
    }

    @Override
    public TreeInfo getItem(int position) {
        return mInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return mInfos.get(position).getFlag();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        mTreeInfo = getItem(position);
        BranchHolder mBranchHolder = null;
        WorkerHolder mWorkerHolder = null;
        switch (getItemViewType(position)) {
            case -1:
            case 0:
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_info_item_banch, null);
                    mBranchHolder = new BranchHolder();
                    mBranchHolder.adapter_info_item_branch_name = (TextView) convertView.findViewById(R.id.adapter_info_item_branch_name);
                    mBranchHolder.adapter_info_item_branch_count = (TextView) convertView.findViewById(R.id.adapter_info_item_branch_count);
                    mBranchHolder.adapter_info_item_branch_iv = (ImageView) convertView.findViewById(R.id.adapter_info_item_branch_iv);
                    convertView.setTag(mBranchHolder);
                } else {
                    mBranchHolder = (BranchHolder) convertView.getTag();
                }
                mBranchHolder.adapter_info_item_branch_name.setText(mTreeInfo.getName());
                if (mMode == ViewMode.CHECK) {
                    mBranchHolder.adapter_info_item_branch_iv.setVisibility(View.VISIBLE);
                    mBranchHolder.adapter_info_item_branch_iv.setImageResource(mTreeInfo.isChecked() ? R.drawable.checkbox_selected : R.drawable.checkbox_normal);
                    mBranchHolder.adapter_info_item_branch_iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            mTreeInfo = getItem(position);
                            mTreeInfo.setChecked(mTreeInfo.isChecked() ? false : true);
                            ((ImageView) v).setImageResource(mTreeInfo.isChecked() ? R.drawable.checkbox_selected : R.drawable.checkbox_normal);
                            mListener.onCheckedChange(mTreeInfo.getPid(), mTreeInfo.getId(), mTreeInfo);
                        }
                    });
                } else {
                    mBranchHolder.adapter_info_item_branch_iv.setVisibility(View.GONE);
                }
                //部门类型才显示子部门及人员数量
                if (mInfos.get(position).getFlag() == 0) {
                    mBranchHolder.adapter_info_item_branch_count.setVisibility(View.VISIBLE);
                    mBranchHolder.adapter_info_item_branch_count.setText(String.valueOf(childCount.get(position)));
                } else {
                    mBranchHolder.adapter_info_item_branch_count.setVisibility(View.GONE);
                }
                break;
            case 1:
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_info_item_worker, null);
                    mWorkerHolder = new WorkerHolder();
                    mWorkerHolder.adapter_info_item_worker_header = (ImageView) convertView.findViewById(R.id.adapter_info_item_worker_header);
                    mWorkerHolder.adapter_info_item_worker_name = (TextView) convertView.findViewById(R.id.adapter_info_item_worker_name);
                    mWorkerHolder.adapter_info_item_worker_job = (TextView) convertView.findViewById(R.id.adapter_info_item_worker_job);
                    mWorkerHolder.adapter_info_item_worker_iv = (ImageView) convertView.findViewById(R.id.adapter_info_item_worker_iv);
                    convertView.setTag(mWorkerHolder);
                } else {
                    mWorkerHolder = (WorkerHolder) convertView.getTag();
                }
                if (mMode == ViewMode.CHECK) {
                    mWorkerHolder.adapter_info_item_worker_iv.setVisibility(View.VISIBLE);
                    mWorkerHolder.adapter_info_item_worker_iv.setImageResource(mTreeInfo.isChecked() ? R.drawable.checkbox_selected : R.drawable.checkbox_normal);
                    mWorkerHolder.adapter_info_item_worker_iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTreeInfo = getItem(position);
                            mTreeInfo.setChecked(mTreeInfo.isChecked() ? false : true);
                            ((ImageView) v).setImageResource(mTreeInfo.isChecked() ? R.drawable.checkbox_selected : R.drawable.checkbox_normal);
                            mListener.onCheckedChange(mTreeInfo.getPid(), mTreeInfo.getId(), mTreeInfo);
                        }
                    });
                } else {
                    mWorkerHolder.adapter_info_item_worker_iv.setVisibility(View.GONE);
                }
//                Glide.with(mContext).load("http://35.164.107.27:8080/im/upload/images/" + mInfos.get(position).getLogo()).bitmapTransform(new CropCircleTransformation(mContext)).into(mWorkerHolder.adapter_info_item_worker_header);
                Picasso.with(mContext)
                        .load(ConstantValue.ImageFile + mInfos.get(position).getLogo())
                        .resize(80, 80)
                        .centerCrop()
                        .placeholder(R.mipmap.default_portrait)
                        .config(Bitmap.Config.ARGB_8888)
                        .error(R.mipmap.default_portrait)
                        .into(mWorkerHolder.adapter_info_item_worker_header);
                mWorkerHolder.adapter_info_item_worker_name.setText(mTreeInfo.getName());
                mWorkerHolder.adapter_info_item_worker_job.setText(mTreeInfo.getPostitionname());
                break;
        }
        return convertView;
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

        void onCheckedChange(int pid, int id, TreeInfo mInfo);
    }

    private class BranchHolder {
        TextView adapter_info_item_branch_name, adapter_info_item_branch_count;
        ImageView adapter_info_item_branch_iv;
    }

    private class WorkerHolder {
        ImageView adapter_info_item_worker_header;
        TextView adapter_info_item_worker_name, adapter_info_item_worker_job;
        ImageView adapter_info_item_worker_iv;
    }
}
