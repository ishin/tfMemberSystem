package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.ParentModel;
import com.tianfangIMS.im.bean.SonModel;
import com.tianfangIMS.im.fragment.Contacts_Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzheng.
 * Date 17/2/5.
 */
public class SecondActivity extends BaseActivity implements OnItemClickListener {
    private static final String TAG = "SecondActivity";

    private TextView tv_title;
    private ListView listView;
    private List<ParentModel> parentModels;
    private List<String> bumenList;
    private ArrayAdapter<String> adapter;

    private String companyName;
    private int pid;

    private boolean isFromSon = false;
    private String title;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.departmentperson_activity);
        setTitle(" ");
        mContext = this;
        tv_title = (TextView) findViewById(R.id.tv_contacts_text);
        tv_title.setVisibility(View.VISIBLE);
        listView = (ListView) findViewById(R.id.lv_departmentperson_info);
        listView.setOnItemClickListener(this);

        Intent intent = getIntent();
        companyName = intent.getStringExtra("name");
        pid = intent.getIntExtra("pid", -1);

        tv_title.setText(companyName);

        getBumenNames(Contacts_Fragment.getInstance().jsonUtils.parentModelList);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bumenList);
        listView.setAdapter(adapter);
    }

    /**
     * 获取部门name
     *
     * @param parentModelList
     */
    public void getBumenNames(List<ParentModel> parentModelList) {
        parentModels = new ArrayList<>();
        bumenList = new ArrayList<>();
        Log.d(TAG, "getBumenNames: " + parentModels.size());
        for (int i = 0; i < parentModelList.size(); i++) {
            if (parentModelList.get(i).getPid() == 0) {
                bumenList.add(parentModelList.get(i).getName());
                parentModels.add(parentModelList.get(i));
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (parentModels != null && parentModels.size() != 0) {
            //获取下一级name
            List<String> data = getNames(parentModels.get(position).getId(), Contacts_Fragment.getInstance().jsonUtils.parentModelList
                    , Contacts_Fragment.getInstance().jsonUtils.sonModelList, position);
            Log.d(TAG, "onItemClick: " + data);

            if (data != null && data.size() != 0) {
                adapter = new ArrayAdapter<>(SecondActivity.this, android.R.layout.simple_list_item_1, data);
                listView.setAdapter(adapter);
            }
        }


    }

    /**
     * 获取点击的部门的下一级
     *
     * @param id
     * @param parentModelList
     * @param sonModelList
     */
    private List<String> getNames(int id, List<ParentModel> parentModelList, List<SonModel> sonModelList, int position) {
        if (isFromSon) {
            Toast.makeText(this, "这是最底层了", Toast.LENGTH_SHORT).show();
        } else {
            if (parentModels != null && parentModels.size() != 0) {
                title = tv_title.getText().toString().trim() + ">" + parentModels.get(position).getName();
                //设置标题
                if (!TextUtils.isEmpty(title))
                    tv_title.setText(title);
            }

            List<String> nameList = new ArrayList<>();
            isFromSon = false;
            parentModels.clear();

            //先比较最低级的list
            for (int i = 0; i < sonModelList.size(); i++) {
                if (sonModelList.get(i).getPid() == id) {
                    nameList.add(sonModelList.get(i).getName());
                    parentModels.add(sonModelList.get(i));
                    isFromSon = true;
                }
            }

            if (nameList != null && nameList.size() != 0)
                return nameList;

            else
                for (int i = 0; i < parentModelList.size(); i++) {

                    if (parentModelList.get(i).getPid() == id) {
                        nameList.add(parentModelList.get(i).getName());
                        parentModels.add(parentModelList.get(i));
                    }
                }
            return nameList;
        }
        return null;
    }


}
