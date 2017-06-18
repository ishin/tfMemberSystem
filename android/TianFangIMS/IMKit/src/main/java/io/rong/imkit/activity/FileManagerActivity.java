package io.rong.imkit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;

import io.rong.imkit.R;
import io.rong.imkit.RongBaseActivity;
import io.rong.imkit.model.FileInfo;
import io.rong.imkit.utils.FileTypeUtils;

/**
 * Created by tiankui on 16/7/30.
 */
public class FileManagerActivity extends RongBaseActivity implements View.OnClickListener {

    private static final int REQUEST_FOR_SELECTED_FILES = 730;
    private static final int RESULT_SELECTED_FILES_TO_SEND = 731;

    private final static int ALL_FILE_FILES = 1;
    private final static int ALL_VIDEO_FILES = 2;
    private final static int ALL_AUDIO_FILES = 3;
    private final static int ALL_OTHER_FILES = 4;
    private final static int ALL_RAM_FILES = 5;
    private final static int ALL_SD_FILES = 6;

    private final static int ROOT_DIR = 100;
    private final static int SD_CARD_ROOT_DIR = 101;

    private final static int FILE_TRAVERSE_TYPE_ONE = 200;
    private final static int FILE_TRAVERSE_TYPE_TWO = 201;

    private TextView mFileTextView;
    private TextView mVideoTextView;
    private TextView mAudioTextView;
    private TextView mOtherTextView;
    private TextView mMobileMemoryTextView;
    private TextView mSDCardTextView;
    private LinearLayout mSDCardLinearLayout;
    private LinearLayout mSDCardOneLinearLayout;
    private LinearLayout mSDcardTwoLinearLayout;

    private String[] mPath;
    private String mSDCardPath;
    private String mSDCardPathOne;
    private String mSDCardPathTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rc_ac_file_manager);

        mFileTextView = (TextView) findViewById(R.id.rc_ac_tv_file_manager_file);
        mVideoTextView = (TextView) findViewById(R.id.rc_ac_tv_file_manager_video);
        mAudioTextView = (TextView) findViewById(R.id.rc_ac_tv_file_manager_audio);
        mOtherTextView = (TextView) findViewById(R.id.rc_ac_tv_file_manager_picture);
        mMobileMemoryTextView = (TextView) findViewById(R.id.rc_ac_tv_file_manager_mobile_memory);
        mSDCardTextView = (TextView) findViewById(R.id.rc_ac_tv_file_manager_SD_card);
        mSDCardLinearLayout = (LinearLayout) findViewById(R.id.rc_ac_ll_sd_card);
        mSDCardOneLinearLayout = (LinearLayout) findViewById(R.id.rc_ac_ll_sd_card_one);
        mSDcardTwoLinearLayout = (LinearLayout) findViewById(R.id.rc_ac_ll_sd_card_two);

        mFileTextView.setOnClickListener(this);
        mVideoTextView.setOnClickListener(this);
        mAudioTextView.setOnClickListener(this);
        mOtherTextView.setOnClickListener(this);
        mMobileMemoryTextView.setOnClickListener(this);
        mSDCardTextView.setOnClickListener(this);
        mSDCardOneLinearLayout.setOnClickListener(this);
        mSDcardTwoLinearLayout.setOnClickListener(this);

        TextView title = (TextView) findViewById(R.id.rc_action_bar_title);
        title.setText(R.string.rc_ac_file_send_preview);

        mPath = FileTypeUtils.getExternalStorageDirectories(this);
        if (mPath.length == 1) {
            mSDCardLinearLayout.setVisibility(View.VISIBLE);
            mSDCardPath = mPath[0];
        }
        if (mPath.length == 2) {
            mSDCardPathOne = mPath[0];
            mSDCardPathTwo = mPath[1];
            mSDCardOneLinearLayout.setVisibility(View.VISIBLE);
            mSDcardTwoLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, FileListActivity.class);
        if (v == mFileTextView) {
            intent.putExtra("rootDirType", ROOT_DIR);
            intent.putExtra("fileFilterType", ALL_FILE_FILES);
            intent.putExtra("fileTraverseType", FILE_TRAVERSE_TYPE_ONE);
        }
        if (v == mVideoTextView) {
            intent.putExtra("rootDirType", ROOT_DIR);
            intent.putExtra("fileFilterType", ALL_VIDEO_FILES);
            intent.putExtra("fileTraverseType", FILE_TRAVERSE_TYPE_ONE);
        }
        if (v == mAudioTextView) {
            intent.putExtra("rootDirType", ROOT_DIR);
            intent.putExtra("fileFilterType", ALL_AUDIO_FILES);
            intent.putExtra("fileTraverseType", FILE_TRAVERSE_TYPE_ONE);
        }
        if (v == mOtherTextView) {
            intent.putExtra("rootDirType", ROOT_DIR);
            intent.putExtra("fileFilterType", ALL_OTHER_FILES);
            intent.putExtra("fileTraverseType", FILE_TRAVERSE_TYPE_ONE);
        }

        if (v == mMobileMemoryTextView) {
            intent.putExtra("rootDirType", ROOT_DIR);
            intent.putExtra("fileFilterType", ALL_RAM_FILES);
            intent.putExtra("fileTraverseType", FILE_TRAVERSE_TYPE_TWO);
        }
        if (v == mSDCardTextView) {
            intent.putExtra("rootDirType", SD_CARD_ROOT_DIR);
            intent.putExtra("fileFilterType", ALL_SD_FILES);
            intent.putExtra("fileTraverseType", FILE_TRAVERSE_TYPE_TWO);
            intent.putExtra("rootDir", mSDCardPath);
        }
        if (v == mSDCardOneLinearLayout) {
            intent.putExtra("rootDirType", SD_CARD_ROOT_DIR);
            intent.putExtra("fileFilterType", ALL_SD_FILES);
            intent.putExtra("fileTraverseType", FILE_TRAVERSE_TYPE_TWO);
            intent.putExtra("rootDir", mSDCardPathOne);

        }
        if (v == mSDcardTwoLinearLayout) {
            intent.putExtra("rootDirType", SD_CARD_ROOT_DIR);
            intent.putExtra("fileFilterType", ALL_SD_FILES);
            intent.putExtra("fileTraverseType", FILE_TRAVERSE_TYPE_TWO);
            intent.putExtra("rootDir", mSDCardPathTwo);

        }
        startActivityForResult(intent, REQUEST_FOR_SELECTED_FILES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FOR_SELECTED_FILES) {
            if (data != null) {
                HashSet<FileInfo> selectedFileInfos = (HashSet<FileInfo>) data.getSerializableExtra("selectedFiles");
                Intent intent = new Intent();
                intent.putExtra("sendSelectedFiles", selectedFileInfos);
                setResult(RESULT_SELECTED_FILES_TO_SEND, intent);
                finish();
            }
        }
    }
}
