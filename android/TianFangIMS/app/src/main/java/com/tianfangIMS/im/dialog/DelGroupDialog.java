package com.tianfangIMS.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/4/13.
 */

public class DelGroupDialog extends Dialog implements View.OnClickListener{
    private Context mContext;
    private String GroupID;
    private List<GroupBean> allChecked;
    private ArrayList<GroupBean> mlist;
    private int position;

    private Button btn_quxiao_move, btn_submit_move;
    private ImageView iv_movegroupuser_photo;
    private TextView tv_movegroupuser_departmentName;
    private TextView tv_person_departmentTxt;

    public DelGroupDialog(Context context,String groupID, List<GroupBean> allChecked, ArrayList<GroupBean> mlist,int position) {
        super(context);
        this.mContext = context;
        GroupID = groupID;
        this.allChecked = allChecked;
        this.mlist = mlist;
        this.position = position;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.delgroupuser_dialog,null);
        setContentView(view);
        init(view);
    }
    private void init(View view){
        btn_quxiao_move = (Button) view.findViewById(R.id.btn_quxiao_del);
        btn_submit_move = (Button) view.findViewById(R.id.btn_submit_del);
        iv_movegroupuser_photo = (ImageView) view.findViewById(R.id.iv_delgroupuser_photo);
        tv_movegroupuser_departmentName = (TextView) view.findViewById(R.id.tv_delgroupuser_departmentName);
        tv_person_departmentTxt = (TextView)view.findViewById(R.id.tv_person_department_del);


        btn_quxiao_move.setOnClickListener(this);
        btn_submit_move.setOnClickListener(this);
        tv_movegroupuser_departmentName.setText("确定将 " + mlist.get(position).getFullname() + "移除本群");
//        tv_person_departmentTxt.setText("您将自动放弃群主身份");
        Picasso.with(mContext)
                .load(ConstantValue.ImageFile + mlist.get(position).getLogo())
                .resize(80, 80)
                .into(iv_movegroupuser_photo);
    }
    private void DelGroupUser() {
        String str = "";
        List<String> list = new ArrayList<String>();
        if (allChecked != null) {
            for (int i = 0; i < allChecked.size(); i++) {
                list.add(allChecked.get(i).getId());
            }
            str = list.toString();
        }
        Log.e("打印数据----：", "--------:" + str);
        OkGo.post(ConstantValue.SINGOUTGROUP)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .params("groupids", str)
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
                        Log.e("打印数据----：", "--------:" + s);
                        if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                            Gson gson = new Gson();
                            Map<String, Object> map = gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                            }.getType());
                            if((Double)map.get("code") == 1.0){
                                NToast.shortToast(mContext,"移除成员成功");
                                RongIM.getInstance().startGroupChat(mContext,GroupID,mlist.get(0).getName());
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
                DelGroupUser();
                this.dismiss();
                break;
        }
    }
}
