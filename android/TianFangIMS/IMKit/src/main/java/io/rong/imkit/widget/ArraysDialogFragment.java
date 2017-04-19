package io.rong.imkit.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

import io.rong.imkit.utilities.OptionsPopupDialog;

/**
 * Created by zhjchen on 4/8/15.
 */
@Deprecated
public class ArraysDialogFragment extends BaseDialogFragment {

    private static final String ARGS_ARRAYS = "args_arrays";

    private OnArraysDialogItemListener mItemListener;
    private int count;

    public static ArraysDialogFragment newInstance(String title, String[] arrays) {
        ArraysDialogFragment dialogFragment = new ArraysDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray(ARGS_ARRAYS, arrays);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArraysDialogFragment setArraysDialogItemListener(OnArraysDialogItemListener mItemListener) {
        this.mItemListener = mItemListener;
        return this;
    }

    public interface OnArraysDialogItemListener {
        public void OnArraysDialogItemClick(DialogInterface dialog, int which);
    }

    public void show(FragmentManager manager) {
        String[] arrays = getArguments().getStringArray(ARGS_ARRAYS);
        setCount(arrays.length);
        List<Fragment> fragmentList = manager.getFragments();
        if (fragmentList != null) {
            Fragment fragment = fragmentList.get(0);
            if (fragment != null) {
                Context context = fragment.getActivity();
                if (context != null) {
                    OptionsPopupDialog.newInstance(context, arrays).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
                        @Override
                        public void onOptionsItemClicked(int which) {
                            mItemListener.OnArraysDialogItemClick(null, which);
                        }
                    }).show();
                }
            }
        }
    }
}
