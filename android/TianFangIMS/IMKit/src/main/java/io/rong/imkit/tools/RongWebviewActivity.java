package io.rong.imkit.tools;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.rong.common.RLog;
import io.rong.common.RongWebView;
import io.rong.imkit.R;
import io.rong.imkit.RongBaseActivity;

public class RongWebviewActivity extends RongBaseActivity {
    private final static String TAG = "RongWebviewActivity";

    private String mPrevUrl;
    private RongWebView mWebView;
    private ProgressBar mProgressBar;
    protected TextView mWebViewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rc_ac_webview);
        Intent intent = getIntent();
        mWebView = (RongWebView) findViewById(R.id.rc_webview);
        mProgressBar = (ProgressBar) findViewById(R.id.rc_web_progressbar);
        mWebViewTitle = (TextView) findViewById(R.id.rc_action_bar_title);
        mWebView.setVerticalScrollbarOverlay(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT > 11) {
            mWebView.getSettings().setDisplayZoomControls(false);
        }
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setWebViewClient(new RongWebviewClient());
        mWebView.setWebChromeClient(new RongWebChromeClient());

        String url = intent.getStringExtra("url");
        Uri data = intent.getData();
        if (url != null) {
            mPrevUrl = url;
            mWebView.loadUrl(url);
        } else if (data != null) {
            mPrevUrl = data.toString();
            mWebView.loadUrl(data.toString());
        }
    }

    private class RongWebviewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (mPrevUrl != null) {
                if (!mPrevUrl.equals(url)) {
                    if (!(url.toLowerCase().startsWith("http://") || url.toLowerCase().startsWith("https://"))) {
                        Intent intent = new Intent("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(url);
                        intent.setData(content_url);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        try {
                            startActivity(intent);
                        } catch (Exception e) {
                            RLog.e(TAG, "not apps install for this intent =" + e.toString());
                            e.printStackTrace();
                        }
                        return true;
                    }
                    mPrevUrl = url;
                    mWebView.loadUrl(url);
                    return true;
                } else {
                    return false;
                }
            } else {
                mPrevUrl = url;
                mWebView.loadUrl(url);
                return true;
            }
        }
    }

    private class RongWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
            } else {
                if (mProgressBar.getVisibility() == View.GONE) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                mProgressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (mWebViewTitle != null && TextUtils.isEmpty(mWebViewTitle.getText())) {
                mWebViewTitle.setText(title);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
