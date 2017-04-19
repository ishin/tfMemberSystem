package io.rong.imkit.widget.provider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.HashSet;

import io.rong.imkit.R;
import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.activity.FileManagerActivity;
import io.rong.imkit.model.FileInfo;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.FileMessage;

public class FilePlugin implements IPluginModule {

    private static final String TAG = "FileInputProvider";
    private static final int REQUEST_FILE = 100;

    private Conversation.ConversationType conversationType;
    private String targetId;

    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.rc_ic_files_selector);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(R.string.rc_plugins_files);
    }

    @Override
    public void onClick(Fragment currentFragment, RongExtension extension) {
        conversationType = extension.getConversationType();
        targetId = extension.getTargetId();
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!PermissionCheckUtil.requestPermissions(currentFragment, permissions)) {
            return;
        }

        Intent intent = new Intent(currentFragment.getActivity(), FileManagerActivity.class);
        extension.startActivityForPluginResult(intent, REQUEST_FILE, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FILE) {
            if (data != null) {
                HashSet<FileInfo> selectedFileInfos = (HashSet<FileInfo>) data.getSerializableExtra("sendSelectedFiles");
                for (FileInfo fileInfo : selectedFileInfos) {
                    Uri filePath = Uri.parse("file://" + fileInfo.getFilePath());
                    FileMessage fileMessage = FileMessage.obtain(filePath);
                    if (fileMessage != null) {
                        fileMessage.setType(fileInfo.getSuffix());
                        final Message message = Message.obtain(targetId, conversationType, fileMessage);
                        RongIM.getInstance().sendMediaMessage(message, null, null, (IRongCallback.ISendMediaMessageCallback) null);
                    }
                }
            }
        }
    }
}
