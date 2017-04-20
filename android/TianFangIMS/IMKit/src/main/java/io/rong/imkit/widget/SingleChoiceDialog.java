package io.rong.imkit.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import io.rong.imkit.R;

public class SingleChoiceDialog extends Dialog {

    protected Context mContext;
    protected View mRootView;

    protected TextView mTVTitle;
    protected TextView mButtonOK;
    protected TextView mButtonCancel;
    protected ListView mListView;

    protected List<String> mList;
    protected OnClickListener mOkClickListener;
    protected OnClickListener mCancelClickListener;

    private SingleChoiceAdapter<String> mSingleChoiceAdapter;


    public SingleChoiceDialog(Context context, List<String> list) {
        super(context);
        // TODO Auto-generated constructor stub

        mContext = context;
        mList = list;

        initView(mContext);
        initData();
    }

    protected void initView(Context context) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rc_cs_single_choice_layout);
        mRootView = findViewById(R.id.rc_cs_rootView);
        mRootView.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mTVTitle = (TextView) findViewById(R.id.rc_cs_tv_title);
        mButtonOK = (Button) findViewById(R.id.rc_cs_btn_ok);
        mButtonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonOK();
            }
        });
        mButtonCancel = (Button) findViewById(R.id.rc_cs_btn_cancel);
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonCancel();
            }
        });

        mListView = (ListView) findViewById(R.id.rc_cs_group_dialog_listView);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        ColorDrawable dw = new ColorDrawable(0x00000000);
        dialogWindow.setBackgroundDrawable(dw);
    }

    public void setTitle(String title) {
        mTVTitle.setText(title);
    }

    public void setOnOKButtonListener(OnClickListener onClickListener) {
        mOkClickListener = onClickListener;
    }

    public void setOnCancelButtonListener(OnClickListener onClickListener) {
        mCancelClickListener = onClickListener;
    }

    protected void onButtonOK() {
        dismiss();
        if (mOkClickListener != null) {
            mOkClickListener.onClick(this, 0);
        }
    }

    protected void onButtonCancel() {
        dismiss();
        if (mCancelClickListener != null) {
            mCancelClickListener.onClick(this, 0);
        }
    }

    protected void initData() {
        mSingleChoiceAdapter = new SingleChoiceAdapter<String>(mContext, mList,
                R.drawable.rc_cs_group_checkbox_selector);

        mListView.setAdapter(mSingleChoiceAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != mSingleChoiceAdapter.getSelectItem()) {
                    if (!mButtonOK.isEnabled()) {
                        mButtonOK.setEnabled(true);
                    }
                    mSingleChoiceAdapter.setSelectItem(position);
                    mSingleChoiceAdapter.notifyDataSetChanged();
                }
            }
        });

        setListViewHeightBasedOnChildren(mListView);

    }

    public int getSelectItem() {
        return mSingleChoiceAdapter.getSelectItem();
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        totalHeight += 10;
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
