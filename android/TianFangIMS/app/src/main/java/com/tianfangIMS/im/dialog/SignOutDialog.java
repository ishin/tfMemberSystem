package com.tianfangIMS.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.activity.LoginActivity;
import com.tianfangIMS.im.service.FloatService;

import okhttp3.Call;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by LianMengYu on 2017/3/10.
 */

public class SignOutDialog extends Dialog implements View.OnClickListener{
    private Context mContext;
    private RelativeLayout rl_dialog_signout,rl_dialog_signoutcancel;
    private String sessionId;
    public SignOutDialog(Context context,String sessionId) {
        super(context);
        this.mContext = context;
        this.sessionId = sessionId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.signout_dialog,null);
        setContentView(view);
        init(view);
    }
    private void init(View view){
        rl_dialog_signout = (RelativeLayout)view.findViewById(R.id.rl_dialog_signout);
        rl_dialog_signoutcancel = (RelativeLayout)view.findViewById(R.id.rl_dialog_signoutcancel);

        rl_dialog_signout.setOnClickListener(this);
        rl_dialog_signoutcancel.setOnClickListener(this);
    }

    private void LogOut(){
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
                        LoadDialog.show(mContext);
                    }
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LoadDialog.dismiss(mContext);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_dialog_signout:
                LogOut();
                SharedPreferences sp = mContext.getSharedPreferences("config", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();
                mContext.startActivity(new Intent(mContext, LoginActivity.class));
                Intent mIntent = new Intent(mContext, FloatService.class);
                mContext.stopService(mIntent);
                this.dismiss();
                break;
            case R.id.rl_dialog_signoutcancel:
                this.dismiss();
                break;
        }
    }
}
