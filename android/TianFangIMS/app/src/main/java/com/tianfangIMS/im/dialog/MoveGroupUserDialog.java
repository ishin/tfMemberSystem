package com.tianfangIMS.im.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.GroupBean;
import com.tianfangIMS.im.utils.NToast;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/3/12.
 */

public class MoveGroupUserDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private String GroupID;
    private ArrayList<GroupBean> list;
    private int position;

    private Button btn_quxiao_move, btn_submit_move;
    private ImageView iv_movegroupuser_photo;
    private TextView tv_movegroupuser_departmentName;
    private TextView tv_person_departmentTxt;

    public MoveGroupUserDialog(Context context, String groupID, ArrayList<GroupBean> list, int position) {
        super(context);
        this.mContext = context;
        GroupID = groupID;
        this.list = list;
        this.position = position;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.movegroupuser_dialog, null);
        setContentView(view);
        init(view);
    }

    private void init(View view) {
        btn_quxiao_move = (Button) view.findViewById(R.id.btn_quxiao_move);
        btn_submit_move = (Button) view.findViewById(R.id.btn_submit_move);
        iv_movegroupuser_photo = (ImageView) view.findViewById(R.id.iv_movegroupuser_photo);
        tv_movegroupuser_departmentName = (TextView) view.findViewById(R.id.tv_movegroupuser_departmentName);
        tv_person_departmentTxt = (TextView) view.findViewById(R.id.tv_person_departmentTxt);


        btn_quxiao_move.setOnClickListener(this);
        btn_submit_move.setOnClickListener(this);
        tv_movegroupuser_departmentName.setText("确定选择 " + list.get(position).getFullname() + " 为新的群主");
        tv_person_departmentTxt.setText("您将自动放弃群主身份");
        Picasso.with(mContext)
                .load(ConstantValue.ImageFile + list.get(position).getLogo())
                .resize(80, 80)
                .into(iv_movegroupuser_photo);
    }

    private void initTransferGroup(final int position) {
        OkGo.post(ConstantValue.TRANSFERGROUP)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .params("userid", list.get(position).getId())
                .params("groupid", GroupID)
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        LoadDialog.show(mContext);
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LoadDialog.dismiss(mContext);
                        if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                            Gson gson = new Gson();
                            Map<String, Object> map = gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                            }.getType());
                            if ((Double) map.get("code") == 1.0) {
                                NToast.shortToast(mContext, "转让成功");
                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
//                                        RongIM.getInstance().startGroupChat(mContext, GroupID, list.get(position).getName());
                                        ((Activity) mContext).finish();
                                    }
                                }, 1000);//延时1s

                            } else {
                                NToast.shortToast(mContext, "转让失败");
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_quxiao_move:
                this.dismiss();
                break;
            case R.id.btn_submit_move:
                initTransferGroup(position);
                this.dismiss();
                break;
        }
    }
}
