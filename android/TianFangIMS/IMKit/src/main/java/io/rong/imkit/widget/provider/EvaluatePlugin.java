package io.rong.imkit.widget.provider;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import io.rong.imkit.R;
import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.widget.CSEvaluateDialog;


public class EvaluatePlugin implements IPluginModule, CSEvaluateDialog.EvaluateClickListener {
    private CSEvaluateDialog mEvaluateDialog;
    private boolean mResolvedButton;

    public EvaluatePlugin(boolean mResolvedButton) {
        this.mResolvedButton = mResolvedButton;
    }

    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.rc_cs_evaluate_selector);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(R.string.rc_cs_evaluate);
    }

    @Override
    public void onClick(Fragment currentFragment, RongExtension extension) {
        mEvaluateDialog = new CSEvaluateDialog(currentFragment.getActivity(), extension.getTargetId());
        mEvaluateDialog.showStarMessage(mResolvedButton);
        mEvaluateDialog.setClickListener(this);
        extension.collapseExtension();
    }

    @Override
    public void onEvaluateSubmit() {
        mEvaluateDialog.destroy();
        mEvaluateDialog = null;
    }

    @Override
    public void onEvaluateCanceled() {
        mEvaluateDialog.destroy();
        mEvaluateDialog = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
