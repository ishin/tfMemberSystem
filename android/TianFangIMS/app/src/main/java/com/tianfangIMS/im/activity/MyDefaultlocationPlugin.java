package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.tianfangIMS.im.utils.NToast;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.plugin.location.AMapLocationActivity;
import io.rong.imkit.plugin.location.LocationManager;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.utilities.PermissionCheckUtil;

/**
 * Created by LianMengYu on 2017/5/7.
 */

public class MyDefaultlocationPlugin implements IPluginModule {

    public MyDefaultlocationPlugin() {

    }

    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, io.rong.imkit.R.drawable.rc_ext_plugin_location_selector);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(io.rong.imkit.R.string.rc_plugin_location);
    }

    @Override
    public void onClick(final Fragment currentFragment, final RongExtension extension) {
        String[] permissions = new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_NETWORK_STATE"};
        if (PermissionCheckUtil.requestPermissions(currentFragment, permissions)) {
            String[] items = new String[]{currentFragment.getString(io.rong.imkit.R.string.rc_plugin_location_message), currentFragment.getString(io.rong.imkit.R.string.rc_plugin_location_sharing)};
            OptionsPopupDialog.newInstance(currentFragment.getActivity(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
                public void onOptionsItemClicked(int which) {
                    if (which == 0) {
                        Intent result = new Intent(currentFragment.getActivity(), AMapLocationActivity.class);
                        extension.startActivityForPluginResult(result, 1, MyDefaultlocationPlugin.this);
                    } else if (which == 1) {
                        int result1 = LocationManager.getInstance().joinLocationSharing();
                        if (result1 == 0) {
                            Intent intent = new Intent(currentFragment.getActivity(), AMapShareLocationActivity.class);
                            currentFragment.getActivity().startActivity(intent);
                        } else if (result1 == 1) {
                            NToast.shortToast(currentFragment.getActivity(), io.rong.imkit.R.string.rc_network_exception);
                        } else if (result1 == 2) {
                            NToast.shortToast(currentFragment.getActivity(), io.rong.imkit.R.string.rc_location_sharing_exceed_max);
                        }
                    }

                }
            }).show();
        }
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {

    }
}
