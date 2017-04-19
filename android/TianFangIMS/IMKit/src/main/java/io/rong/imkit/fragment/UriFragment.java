package io.rong.imkit.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import io.rong.common.RLog;

public abstract class UriFragment extends BaseFragment {
    public static final String RONG_URI = "RONG_URI";

    private boolean mViewCreated;
    private Uri mUri;
    private IActionBarHandler mBarHandler;

    protected Bundle obtainUriBundle(Uri uri) {
        Bundle args = new Bundle();
        args.putParcelable(RONG_URI, uri);
        return args;
    }


    protected interface IActionBarHandler {
        void onTitleChanged(CharSequence title);

        void onUnreadCountChanged(int count);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mUri == null) {
            if (savedInstanceState == null) {
                mUri = getActivity().getIntent().getData();
            } else {
                mUri = savedInstanceState.getParcelable(RONG_URI);
            }
        }
        mViewCreated = true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getUri() != null) {
            initFragment(getUri());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(RONG_URI, getUri());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreUI() {
        if (getUri() != null) {
            initFragment(getUri());
        }
    }

    public void setActionBarHandler(IActionBarHandler mBarHandler) {
        this.mBarHandler = mBarHandler;
    }

    protected IActionBarHandler getActionBarHandler() {
        return mBarHandler;
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        mUri = uri;
        if (mViewCreated) {
            initFragment(uri);
        }
    }

    protected abstract void initFragment(Uri uri);

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
