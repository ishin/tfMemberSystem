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

public class PictureFragment extends BaseFragment {
    public static PictureFragment Instance = null;

    public static PictureFragment getInstance() {
        if (Instance == null) {
            Instance = new PictureFragment();
        }
        return Instance;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picture_fragment, container, false);
        TextView view1 = new TextView(getActivity());
        view1.setText("图片");
        return view;

    }

}
