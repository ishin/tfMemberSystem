package com.tianfangIMS.im.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.dialog.LoadDialog;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/1/4.
 */

public class Jobs_Fragment extends BaseFragment {
    private Button brn;
    String sessionId;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.jobs_fragment, container, false);
        sessionId = getActivity().getSharedPreferences("CompanyCode", Activity.MODE_PRIVATE).getString("CompanyCode", "");
        brn = (Button)view.findViewById(R.id.brn);
        brn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkGo.post(ConstantValue.LOGOUT)
                        .tag(this)
                        .connTimeOut(10000)
                        .readTimeOut(10000)
                        .writeTimeOut(10000)
                        .headers("cookie",sessionId)
                        .execute(new StringCallback() {
                            @Override
                            public void onBefore(BaseRequest request) {
                                super.onBefore(request);
                                LoadDialog.show(getActivity());
                            }
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                LoadDialog.dismiss(getActivity());
                            }
                        });
            }
        });
        return view;
    }
}
