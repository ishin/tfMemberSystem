package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.tianfangIMS.im.R;
import com.tianfangIMS.im.adapter.SelectPhoto_GridView_Adapter;
import com.tianfangIMS.im.bean.ViewMode;
import com.tianfangIMS.im.dialog.BigImagedialog;
import com.tianfangIMS.im.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by LianMengYu on 2017/2/14.
 */
public class SelectPhoteActivity extends BaseActivity implements AdapterView.OnItemClickListener, SelectPhoto_GridView_Adapter.OnDepartmentCheckedChangeListener,
        View.OnClickListener {
    private static final String TAG = "SelectPhoteActivity";
    private Context mContext;
    private GridView gridView;
    SelectPhoto_GridView_Adapter adapter;
    private TextView tv_select;
    List<Uri> list;
    private ArrayList<Uri> allChecked;
    private Map<Integer, Boolean> checkedMap;
    ArrayList<String> ImgMesList;//发送图片消息的传递值
    private SelectPhoto_GridView_Adapter.ViewHodler hodler;
    ViewMode mode;
    Boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectphont_layout);
        setTitle("聊天文件");
        mode = ViewMode.NORMAL;
        setTv_completeVisibiliy(View.VISIBLE);
        mContext = this;
        gridView = (GridView) this.findViewById(R.id.grid);
        gridView.setOnItemClickListener(this);
        tv_select = getTv_title();
        tv_select.setText("选择");
        List alist = (List<Object>) getIntent().getSerializableExtra("photouri");
        list = new ArrayList<>();
        if (alist.size() > 0 && alist != null) {
            list = alist;
            SetInfo();
        } else {
            return;
        }
        tv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "TextView：" + tv_select.getText());
                if (flag) {
                    mode = ViewMode.CHECK;
                    SetInfo();
                } else {
                    SendImageMessage();
                }
            }
        });
    }

    private void SetInfo() {
        adapter = new SelectPhoto_GridView_Adapter(list, mContext, mode);
        gridView.setAdapter(adapter);
        adapter.setOnDepartmentCheckedChangeListener(this);
        gridView.deferNotifyDataSetChanged();
    }

    private void getCount() {
        checkedMap = adapter.getCheckedMap();//获取选中的人，true是选中的，false是没选中的
        Log.e(TAG, "checkedMap：" + checkedMap);
        allChecked = new ArrayList<Uri>();//创建一个存储选中的人的集合
        for (int i = 0; i < checkedMap.size(); i++) {//循环获取选中人的集合
            if (checkedMap.get(i) == null) {    //防止出现空指针,如果为空,证明没有被选中
                continue;
            } else if (checkedMap.get(i)) {//判断是否有值，如果为空证明没有被选中
                Uri uri = list.get(i);
                allChecked.add(uri);
                Log.e(TAG, "allChecked：" + allChecked);
            }
        }
    }

    private void SendImageMessage() {
        ImgMesList = new ArrayList<>();
        for (int i = 0; i < allChecked.size(); i++) {
            ImgMesList.add(allChecked.get(i).toString());
        }
        Log.e(TAG, "allChecked2：" + allChecked);
        Intent intent = new Intent(mContext, SendMessageActivity.class);
        intent.putStringArrayListExtra("ListUri", ImgMesList);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        hodler = (SelectPhoto_GridView_Adapter.ViewHodler) view.getTag();
        BigImagedialog bigImagedialog = new BigImagedialog(mContext, list.get(position).toString(), R.style.Dialog_Fullscreen);
        bigImagedialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        bigImagedialog.show();
        CommonUtil.SetDialogStyle(bigImagedialog);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onCheckedChange(View v) {
        tv_select.setText("发送");
        flag = false;
        getCount();
    }
}
