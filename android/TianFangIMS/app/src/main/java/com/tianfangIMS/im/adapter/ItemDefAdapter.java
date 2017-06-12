package com.tianfangIMS.im.adapter;


import android.app.Activity;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.utils.JsonUtil;
import com.tianfangIMS.im.view.CircleTransform;

import java.util.List;
import java.util.Map;

/**
 * Created by Rainking on 2017/4/25.
 */

public class ItemDefAdapter extends BaseQuickAdapter<Map<String, Object>, BaseViewHolder> {

    //服务器路径
    private final String HTTP_URL = "http://120.26.42.225:8080/im/";
    //服务器图片路径
    private final String URL_IMAGE = HTTP_URL + "upload/images/";
    Activity activity;
    List<Map<String, Object>> mData;
    private ImageView iv_def;

    public ItemDefAdapter(Activity activity, List<Map<String, Object>> mData) {
        super(R.layout.item_def, mData);
        this.activity = activity;
        this.mData = mData;
    }

    @Override
    protected void convert(BaseViewHolder helper, Map<String, Object> item) {
        String json = JsonUtil.toJson(item);
        iv_def = helper.getView(R.id.iv_def);
        Picasso.with(activity)
                .load(URL_IMAGE + JsonUtil.getMsg(json, "logo"))
                .error(R.mipmap.default_portrait)
                .placeholder(R.mipmap.default_portrait)
                .transform(new CircleTransform())
                .into(iv_def);
    }
}
