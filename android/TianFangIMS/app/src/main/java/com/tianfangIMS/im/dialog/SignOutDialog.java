package com.tianfangIMS.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.tianfangIMS.im.R;
import com.tianfangIMS.im.activity.LoginActivity;
import com.tianfangIMS.im.service.FloatService;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by LianMengYu on 2017/3/10.
 */

public class SignOutDialog extends Dialog implements View.OnClickListener{
    private Context mContext;
    private RelativeLayout rl_dialog_signout,rl_dialog_signoutcancel;

    public SignOutDialog(Context context) {
        super(context);
        this.mContext = context;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_dialog_signout:
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
