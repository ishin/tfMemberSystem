package com.tianfangIMS.im.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.adapter.MoveGroupUserAdapter;
import com.tianfangIMS.im.bean.GroupBean;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.utils.NToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/3/13.
 */
public class DeleteGropUserActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView lv_delete_GroupUser;
    private String GroupID;
    private ArrayList<GroupBean> mlist;
    private List<GroupBean> allChecked;
    private Map<Integer, Boolean> checkedMap;
    private MoveGroupUserAdapter adapter;
    private TextView tv_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_groupuser_layout);
        setTitle("删除群组成员");
        setTv_completeVisibiliy(View.VISIBLE);
        GroupID = getIntent().getStringExtra("fromConversationId");
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        Object object = bundle.get("GroupBeanList");
        mlist = (ArrayList<GroupBean>) object;
        tv_delete = getTv_title();
        tv_delete.setText("删除");
        init();
        adapter = new MoveGroupUserAdapter(this, mlist, true);
        lv_delete_GroupUser.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DelGroupUser();
//                DelGroupDialog delGroupDialog = new DelGroupDialog(mContext,GroupID,allChecked,mlist,0);
//                delGroupDialog.show();
                dialog();
            }
        });
    }

    private void init() {
        lv_delete_GroupUser = (ListView) this.findViewById(R.id.lv_delete_GroupUser);
        lv_delete_GroupUser.setOnItemClickListener(this);
    }

    private void getCount() {
        checkedMap = adapter.getCheckedMap();//获取选中的人，true是选中的，false是没选中的
        Log.e("DeleteGropUserActivity", "checkedMap：" + checkedMap);
        allChecked = new ArrayList<GroupBean>();//创建一个存储选中的人的集合
        for (int i = 0; i < checkedMap.size(); i++) {//循环获取选中人的集合
            if (checkedMap.get(i) == null) {    //防止出现空指针,如果为空,证明没有被选中
                continue;
            } else if (checkedMap.get(i)) {//判断是否有值，如果为空证明没有被选中
                GroupBean testCheckBean = mlist.get(i);
                allChecked.add(testCheckBean);
                Log.e("DeleteGropUserActivity", "checkedMap：" + allChecked.size());
            }
        }

    }

    private void dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("提示");
        builder.setMessage("确认删除群组成员吗？");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DelGroupUser();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MoveGroupUserAdapter.ViewHodler hodler = (MoveGroupUserAdapter.ViewHodler) view.getTag();
        hodler.cb_adddel.toggle();
        getCount();
    }
}
