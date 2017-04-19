package com.tianfangIMS.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.amap.api.maps2d.model.Marker;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.adapter.AllUserPhotoForShareLocation_GridView_Adapter;
import com.tianfangIMS.im.bean.LocationBean;

import java.util.List;

/**
 * Created by LianMengYu on 2017/3/3.
 */

public class ShareLoactionDialog extends Dialog{
    private Context mContext;
    private GridView gv_sharelocation_dialog;
    private List<LocationBean> mlist;
    private Marker marker;
    private DialogItemClickListener DialogItemClickListener;
    public ShareLoactionDialog(Context context, List<LocationBean> mlist, int theme, Marker marker,DialogItemClickListener listener) {
        super(context);
        this.mContext = context;
        this.mlist = mlist;
        this.marker = marker;
        this.DialogItemClickListener = listener;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.sharelocation_dialog, null);
        setContentView(view);
        init(view);
        AllUserPhotoForShareLocation_GridView_Adapter adapter = new AllUserPhotoForShareLocation_GridView_Adapter(mlist, mContext);
        gv_sharelocation_dialog.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        gv_sharelocation_dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogItemClickListener.onItemClick(parent,view,position,id,marker);
            }
        });
    }
    public interface DialogItemClickListener{
        public void onItemClick(AdapterView<?> parent,View view, int position, long id,Marker marker);
    }
    private void init(View view) {
        gv_sharelocation_dialog = (GridView) view.findViewById(R.id.gv_sharelocation_dialog);
    }
}
