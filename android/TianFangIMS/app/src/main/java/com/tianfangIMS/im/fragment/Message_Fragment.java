package com.tianfangIMS.im.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tianfangIMS.im.R;

/**
 * Created by LianMengYu on 2017/1/4.
 */

public class Message_Fragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.message_fragment,container,false);

        return view;
    }
}
