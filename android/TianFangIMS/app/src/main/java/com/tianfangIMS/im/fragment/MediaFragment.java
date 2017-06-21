package com.tianfangIMS.im.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tianfangIMS.im.R;

/**
 * Created by LianMengYu on 2017/4/29.
 */

public class MediaFragment extends BaseFragment{


    public static MediaFragment Instance = null;

    public static MediaFragment getInstance() {
        if (Instance == null) {
            Instance = new MediaFragment();
        }
        return Instance;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.media_fragment, container, false);
        TextView view1 = new TextView(getActivity());
        view1.setText("视频");
        return view;

    }
}
