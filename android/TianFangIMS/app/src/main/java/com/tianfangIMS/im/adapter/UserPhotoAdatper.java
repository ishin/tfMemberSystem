package com.tianfangIMS.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.tianfangIMS.im.R;

import java.util.List;

/**
 * Created by LianMengYu on 2017/3/1.
 */

public class UserPhotoAdatper implements AMap.InfoWindowAdapter {
    private Context mContext;
    private LatLng latLng;
//    private List<String> mlsit = new ArrayList<>();
    private String snippet;
    private String photoUrl;
    private String agentName;
    private TextView name,title;
    private List<BitmapDescriptor> list;
    public UserPhotoAdatper(Context mContext, LatLng latLng, String photoUrl) {
        this.mContext = mContext;
        this.latLng = latLng;
        this.photoUrl = photoUrl;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        initData(marker);
        View infowindow = LayoutInflater.from(mContext).inflate(R.layout.view_infowindow, null);
        ImageView userphoto = (ImageView)infowindow.findViewById(R.id.iv_amap_photo);
        name.setText(snippet);
        title.setText(agentName);
        userphoto.setImageBitmap(list.get(0).getBitmap());
//            Picasso.with(mContext)
//                    .load(photoUrl)
//                    .into(userphoto);
//            CommonUtil.GetImages(mContext,photoUrl,userphoto);
        return infowindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void initData(Marker marker) {
        latLng = marker.getPosition();
        snippet = marker.getSnippet();
        agentName = marker.getTitle();
        list = marker.getIcons();
    }
}
