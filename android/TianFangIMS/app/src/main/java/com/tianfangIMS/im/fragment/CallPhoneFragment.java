package com.tianfangIMS.im.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tianfangIMS.im.R;

/**
 * Created by LianMengYu on 2017/2/9.
 */

public class CallPhoneFragment extends BaseFragment {
    public static CallPhoneFragment Instance = null;

    public static CallPhoneFragment getInstance() {
        if (Instance == null) {
            Instance = new CallPhoneFragment();
        }
        return Instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.callphone_fragment, container, false);
        TextView textView = new TextView(getActivity());
        textView.setText("语音通话Fragment");
        return view;
    }
}
