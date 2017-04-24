package io.rong.imkit.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.R;
import io.rong.imkit.RongBaseActivity;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.Event;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imkit.utils.FileTypeUtils;
import io.rong.imlib.model.Message;
import io.rong.message.FileMessage;

import static android.widget.Toast.makeText;

/**
 * Created by tiankui on 16/8/16.
 */
public class FilePreviewActivity extends RongBaseActivity implements View.OnClickListener {
    private static final int NOT_DOWNLOAD = 0;
    private static final int DOWNLOADED = 1;
    private static final int DOWNLOADING = 2;
    private static final int DELETED = 3;
    private static final int DOWNLOAD_ERROR = 4;
    private static final int DOWNLOAD_CANCEL = 5;
    private static final int DOWNLOAD_SUCCESS = 6;

    private static final int ON_SUCCESS_CALLBACK = 100;
    private static final int ON_PROGRESS_CALLBACK = 101;
    private static final int ON_CANCEL_CALLBACK = 102;
    private static final int ON_ERROR_CALLBACK = 103;

    static public final int REQUEST_CODE_ASK_PERMISSION_WRITE = 1000;
    static public final int REQUEST_CODE_ASK_PERMISSION_READ = 1001;



    private FileDownloadInfo mFileDownloadInfo;
    private ImageView mFileTypeImage;
    private TextView mFileNameView;
    private TextView mFileSizeView;
    private Button mFileButton;
    private ProgressBar mFileDownloadProgressBar;
    private LinearLayout mDownloadProgressView;
    private TextView mDownloadProgressTextView;
    private View mCancel;

    private FileMessage mFileMessage;
    private Message mMessage;
    private int mProgress;

    private String mFileName;
    private long mFileSize;
    private List<Toast> mToasts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rc_ac_file_download);

        mFileMessage = getIntent().getParcelableExtra("FileMessage");
        mMessage = getIntent().getParcelableExtra("Message");
        mProgress = getIntent().getIntExtra("Progress", 0);
        initView();
        initData();
        getFileDownloadInfo();
    }

    private void initData() {
        mToasts = new ArrayList<>();
        mFileName = mFileMessage.getName();
        mFileTypeImage.setImageResource(FileTypeUtils.fileTypeImageId(mFileName));
        mFileNameView.setText(mFileName);
        mFileSize = mFileMessage.getSize();
        mFileSizeView.setText(FileTypeUtils.formatFileSize(mFileSize));

        mFileDownloadInfo = new FileDownloadInfo();

        mFileButton.setOnClickListener(this);
        mCancel.setOnClickListener(this);

        RongContext.getInstance().getEventBus().register(this);
    }
    private void initView() {
        mFileTypeImage = (ImageView) findViewById(R.id.rc_ac_iv_file_type_image);
        mFileNameView = (TextView) findViewById(R.id.rc_ac_tv_file_name);
        mFileSizeView = (TextView) findViewById(R.id.rc_ac_tv_file_size);
        mFileButton = (Button) findViewById(R.id.rc_ac_btn_download_button);
        mDownloadProgressView = (LinearLayout) findViewById(R.id.rc_ac_ll_progress_view);
        mCancel = findViewById(R.id.rc_btn_cancel);
        mFileDownloadProgressBar = (ProgressBar) findViewById(R.id.rc_ac_pb_download_progress);
        mDownloadProgressTextView = (TextView) findViewById(R.id.rc_ac_tv_download_progress);
        TextView title = (TextView)findViewById(R.id.rc_action_bar_title);
        title.setText(R.string.rc_ac_file_download_preview);
        if (mMessage.getMessageDirection() == Message.MessageDirection.RECEIVE) {
            if (mProgress == 0) {
                mDownloadProgressView.setVisibility(View.GONE);
                mFileButton.setVisibility(View.VISIBLE);
            } else if (mProgress == 100) {
                mDownloadProgressView.setVisibility(View.GONE);
                mFileButton.setVisibility(View.VISIBLE);
            } else {
                mFileButton.setVisibility(View.GONE);
                mDownloadProgressView.setVisibility(View.VISIBLE);
                mFileDownloadProgressBar.setProgress(mProgress);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mFileButton) {
            switch (mFileDownloadInfo.state) {
                case NOT_DOWNLOAD:
                case DOWNLOAD_CANCEL:
                case DOWNLOAD_ERROR:
                case DELETED:
                    downloadFile();
                    break;
                case DOWNLOAD_SUCCESS:
                case DOWNLOADED:
                    openFile(mFileName, mFileMessage.getLocalPath().getPath());
                    break;
            }
        } else if (v == mCancel) {
            RongIM.getInstance().cancelDownloadMediaMessage(mMessage, null);
        }
    }

    private void openFile(String fileName, String fileSavePath) {
        Intent intent = FileTypeUtils.getOpenFileIntent(fileName, fileSavePath);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } else {
            makeText(FilePreviewActivity.this, getString(R.string.rc_ac_file_preview_can_not_open_file), Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadFile() {
        String[] permission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(!PermissionCheckUtil.checkPermissions(this, permission)) {
            PermissionCheckUtil.requestPermissions(this, permission);
            return;
        }
        mFileButton.setVisibility(View.GONE);
        mDownloadProgressView.setVisibility(View.VISIBLE);
        mDownloadProgressTextView.setText(getString(R.string.rc_ac_file_download_progress_tv, FileTypeUtils.formatFileSize(0), FileTypeUtils.formatFileSize(mFileSize)));
        RongIM.getInstance().downloadMediaMessage(mMessage, null);
    }
    private void getFileDownloadInfo() {
        if (mFileMessage.getLocalPath() != null) {
            File file = new File(mFileMessage.getLocalPath().getPath());
            if (file.exists()) {
                mFileDownloadInfo.state = DOWNLOADED;
            } else {
                mFileDownloadInfo.state = DELETED;
            }
        } else {
            if(mProgress > 0 && mProgress < 100){
                mFileDownloadInfo.state = DOWNLOADING;
                mFileDownloadInfo.progress = mProgress;
            } else {
                mFileDownloadInfo.state = NOT_DOWNLOAD;
            }
        }
        refreshDownloadState();
    }
    private void refreshDownloadState() {
        switch (mFileDownloadInfo.state) {
            case NOT_DOWNLOAD:
                mFileButton.setText(getString(R.string.rc_ac_file_preview_begin_download));
                break;
            case DOWNLOADING:
                mFileButton.setVisibility(View.GONE);
                mDownloadProgressView.setVisibility(View.VISIBLE);
                mFileDownloadProgressBar.setProgress(mFileDownloadInfo.progress);
                long downloadedFileLength = (long) (mFileMessage.getSize() * (mFileDownloadInfo.progress / 100.0) + 0.5f);
                mDownloadProgressTextView.setText(getString(R.string.rc_ac_file_download_progress_tv, FileTypeUtils.formatFileSize(downloadedFileLength), FileTypeUtils.formatFileSize(mFileSize)));
                break;
            case DOWNLOADED:
                mFileButton.setText(getString(R.string.rc_ac_file_download_open_file_btn));
                break;
            case DOWNLOAD_SUCCESS:
                mDownloadProgressView.setVisibility(View.GONE);
                mFileButton.setVisibility(View.VISIBLE);
                mFileButton.setText(getString(R.string.rc_ac_file_download_open_file_btn));
                makeText(FilePreviewActivity.this, getString(R.string.rc_ac_file_preview_downloaded) + mFileDownloadInfo.path, Toast.LENGTH_SHORT).show();
                break;
            case DOWNLOAD_ERROR:
                mDownloadProgressView.setVisibility(View.GONE);
                mFileButton.setVisibility(View.VISIBLE);
                mFileButton.setText(getString(R.string.rc_ac_file_preview_begin_download));
                Toast toast = Toast.makeText(FilePreviewActivity.this, getString(R.string.rc_ac_file_preview_download_error), Toast.LENGTH_SHORT);
                toast.show();
                mToasts.add(toast);
                break;
            case DOWNLOAD_CANCEL:
                mDownloadProgressView.setVisibility(View.GONE);
                mFileDownloadProgressBar.setProgress(0);
                mFileButton.setVisibility(View.VISIBLE);
                makeText(FilePreviewActivity.this, getString(R.string.rc_ac_file_preview_download_cancel), Toast.LENGTH_SHORT).show();
                break;
            case DELETED:
                mFileButton.setText(getString(R.string.rc_ac_file_preview_begin_download));
                break;
        }

    }
    public void onEventMainThread(Event.FileMessageEvent event) {
        if (mMessage.getMessageId() == event.getMessage().getMessageId()) {
            switch (event.getCallBackType()) {
                case ON_SUCCESS_CALLBACK:
                    if (event.getMessage() == null || event.getMessage().getContent() == null)
                        return;
                    FileMessage fileMessage = (FileMessage) event.getMessage().getContent();
                    mFileMessage.setLocalPath(Uri.parse(fileMessage.getLocalPath().toString()));
                    mFileDownloadInfo.state = DOWNLOAD_SUCCESS;
                    mFileDownloadInfo.path = fileMessage.getLocalPath().toString();
                    refreshDownloadState();
                    break;
                case ON_PROGRESS_CALLBACK:
                    mFileDownloadInfo.state = DOWNLOADING;
                    mFileDownloadInfo.progress = event.getProgress();
                    refreshDownloadState();
                    break;
                case ON_ERROR_CALLBACK:
                    mFileDownloadInfo.state = DOWNLOAD_ERROR;
                    refreshDownloadState();
                    break;
                case ON_CANCEL_CALLBACK:
                    mFileDownloadInfo.state = DOWNLOAD_CANCEL;
                    refreshDownloadState();
                    break;
            }
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        getFileDownloadInfo();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        RongContext.getInstance().getEventBus().unregister(this);
        try {
            for (Toast toast : mToasts) {
                toast.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    class FileDownloadInfo {
        int state;
        int progress;
        String path;
    }
}
