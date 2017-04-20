package io.rong.imkit.widget.provider;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Message;
import io.rong.message.LocationMessage;
import io.rong.imkit.R;

public class LocationPlugin implements IPluginModule {
    public LocationPlugin() {

    }

    @Override
    public Drawable obtainDrawable(Context context) {
        return context.getResources().getDrawable(R.drawable.rc_ext_plugin_location_selector);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(R.string.rc_plugin_location);
    }

    @Override
    public void onClick(Fragment currentFragment, final RongExtension extension) {
        if (RongContext.getInstance() != null && RongContext.getInstance().getLocationProvider() != null) {
            RongContext.getInstance().getLocationProvider().onStartLocation(currentFragment.getActivity().getApplicationContext(), new RongIM.LocationProvider.LocationCallback() {
                @Override
                public void onSuccess(final LocationMessage locationMessage) {
                    Message message = Message.obtain(extension.getTargetId(), extension.getConversationType(), locationMessage);
                    RongIM.getInstance().sendLocationMessage(message, null, null, null);
                }

                @Override
                public void onFailure(String msg) {

                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
