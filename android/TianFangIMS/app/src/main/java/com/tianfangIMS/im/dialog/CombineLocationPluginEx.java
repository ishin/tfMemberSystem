package com.tianfangIMS.im.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;

/**
 * Created by LianMengYu on 2017/2/13.
 */

public class CombineLocationPluginEx  implements IPluginModule {

    @Override
    public Drawable obtainDrawable(Context context) {
        return null;
    }

    @Override
    public String obtainTitle(Context context) {
        return null;
    }

    @Override
    public void onClick(final Fragment currentFragment,final RongExtension extension) {
//        String[] permissions = new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_NETWORK_STATE"};
//        if(PermissionCheckUtil.requestPermissions(currentFragment, permissions)) {
//            String[] items = new String[]{currentFragment.getString(io.rong.imkit.R.string.rc_plugin_location_message), currentFragment.getString(io.rong.imkit.R.string.rc_plugin_location_sharing)};
//            OptionsPopupDialog.newInstance(currentFragment.getActivity(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
//                public void onOptionsItemClicked(int which) {
//                    Intent intent;
//                    if(which == 0) {
//                        intent = new Intent(currentFragment.getActivity(), AMapLocationActivity.class);
//                        extension.startActivityForPluginResult(intent, 1, CombineLocationPluginEx.this);
//                    } else if(which == 1) {
//                        if(LocationManager.getInstance().joinLocationSharing()) {
//                            intent = new Intent(currentFragment.getActivity(), AMapRealTimeActivity.class);
//                            currentFragment.getActivity().startActivity(intent);
//                        } else {
//                            Toast.makeText(currentFragment.getActivity(), io.rong.imkit.R.string.rc_network_exception, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                }
//            }).show();
//        }
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {

    }
}
