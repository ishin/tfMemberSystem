package io.rong.imkit.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v4.os.EnvironmentCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.rong.imkit.R;
import io.rong.imkit.RongContext;
import io.rong.imkit.model.FileInfo;

/**
 * Created by tiankui on 16/7/27.
 */
public class FileTypeUtils {

    public static int fileTypeImageId(String fileName) {
        int id;
        if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_image_file_suffix)))
            id = R.drawable.rc_file_icon_picture;
        else if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_file_file_suffix)))
            id = R.drawable.rc_file_icon_file;
        else if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_video_file_suffix)))
            id = R.drawable.rc_file_icon_video;
        else if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_audio_file_suffix)))
            id = R.drawable.rc_file_icon_audio;
        else if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_word_file_suffix)))
            id = R.drawable.rc_file_icon_word;
        else if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_excel_file_suffix)))
            id = R.drawable.rc_file_icon_excel;
        else
            id = R.drawable.rc_file_icon_else;
        return id;
    }

    private static boolean checkSuffix(String fileName,
                                       String[] fileSuffix) {
        for (String suffix : fileSuffix) {
            if (fileName != null) {
                if (fileName.toLowerCase().endsWith(suffix)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Intent getOpenFileIntent(String fileName, String fileSavePath) {
        Intent intent = new Intent("android.intent.action.VIEW");
        String type = null;
        if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_image_file_suffix))) {
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            type = "image/*";
        }
        if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_file_file_suffix))) {
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            type = "text/plain";
        }
        if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_video_file_suffix))) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("oneshot", 0);
            intent.putExtra("configchange", 0);
            type = "video/*";
        }
        if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_audio_file_suffix))) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("oneshot", 0);
            intent.putExtra("configchange", 0);
            type = "audio/*";
        }
        if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_word_file_suffix))) {
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            type = "application/msword";
        }
        if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_excel_file_suffix))) {
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            type = "application/vnd.ms-excel";
        }
        Uri uri = FileProvider.getUriForFile(RongContext.getInstance(), RongContext.getInstance().getApplicationContext().getPackageName() + ".FileProvider", new File(fileSavePath));
        intent.setDataAndType(uri, type);
        if (type != null && isIntentHandlerAvailable(RongContext.getInstance(), intent)) {
            return intent;
        } else {
            return null;
        }
    }

    private static boolean isIntentHandlerAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> infoList = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return infoList.size() > 0;
    }

    /**
     * 文件过滤,将手机中隐藏的文件给过滤掉
     */

    public static final FileFilter ALL_FOLDER_AND_FILES_FILTER = new FileFilter() {

        @Override
        public boolean accept(File pathname) {
            return !pathname.isHidden();
        }
    };

    public static final class FileTypeFilter implements FileFilter {

        private String[] filesSuffix;

        public FileTypeFilter(String[] fileSuffix) {
            this.filesSuffix = fileSuffix;
        }

        @Override
        public boolean accept(File pathname) {
            if (!pathname.isHidden() && (pathname.isDirectory() || checkSuffix(pathname.getName(), filesSuffix))) {
                return true;
            }
            return false;
        }
    }

    public static List<FileInfo> getTextFilesInfo(File fileDir) {
        List<FileInfo> textFilesInfo = new ArrayList<>();
        FileFilter fileFilter = new FileTypeFilter(RongContext.getInstance().getResources().getStringArray(R.array.rc_file_file_suffix));
        getFileInfos(fileDir, fileFilter, textFilesInfo);
        return textFilesInfo;
    }

    private static void getFileInfos(File fileDir, FileFilter fileFilter, List<FileInfo> fileInfos) {
        File[] listFiles = fileDir.listFiles(fileFilter);
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.isDirectory()) {
                    getFileInfos(file, fileFilter, fileInfos);
                } else {
                    FileInfo fileInfo = getFileInfoFromFile(file);
                    fileInfos.add(fileInfo);
                }
            }
        }

    }

    public static List<FileInfo> getVideoFilesInfo(File fileDir) {
        List<FileInfo> videoFilesInfo = new ArrayList<>();
        FileFilter fileFilter = new FileTypeFilter(RongContext.getInstance().getResources().getStringArray(R.array.rc_video_file_suffix));
        getFileInfos(fileDir, fileFilter, videoFilesInfo);
        return videoFilesInfo;
    }

    public static List<FileInfo> getAudioFilesInfo(File fileDir) {
        List<FileInfo> audioFilesInfo = new ArrayList<>();
        FileFilter fileFilter = new FileTypeFilter(RongContext.getInstance().getResources().getStringArray(R.array.rc_audio_file_suffix));
        getFileInfos(fileDir, fileFilter, audioFilesInfo);
        return audioFilesInfo;
    }

    public static List<FileInfo> getOtherFilesInfo(File fileDir) {
        List<FileInfo> otherFilesInfo = new ArrayList<>();
        FileFilter fileFilter = new FileTypeFilter(RongContext.getInstance().getResources().getStringArray(R.array.rc_other_file_suffix));
        getFileInfos(fileDir, fileFilter, otherFilesInfo);
        return otherFilesInfo;
    }

    public static List<FileInfo> getFileInfosFromFileArray(File[] files) {
        List<FileInfo> fileInfos = new ArrayList<>();
        for (File file : files) {
            FileInfo fileInfo = getFileInfoFromFile(file);
            fileInfos.add(fileInfo);
        }
        return fileInfos;
    }

    private static FileInfo getFileInfoFromFile(File file) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(file.getName());
        fileInfo.setFilePath(file.getPath());
        fileInfo.setFileSize(file.length());
        fileInfo.setDirectory(file.isDirectory());
        int lastDotIndex = file.getName().lastIndexOf(".");
        if (lastDotIndex > 0) {
            String fileSuffix = file.getName().substring(lastDotIndex + 1);
            fileInfo.setSuffix(fileSuffix);
        }
        return fileInfo;
    }

    /**
     * 根据文件名进行比较排序
     */
    public static class FileNameComparator implements Comparator<FileInfo> {
        protected final static int
                FIRST = -1,
                SECOND = 1;

        @Override
        public int compare(FileInfo lhs, FileInfo rhs) {
            if (lhs.isDirectory() || rhs.isDirectory()) {
                if (lhs.isDirectory() == rhs.isDirectory())
                    return lhs.getFileName().compareToIgnoreCase(rhs.getFileName());
                else if (lhs.isDirectory()) return FIRST;
                else return SECOND;
            }
            return lhs.getFileName().compareToIgnoreCase(rhs.getFileName());
        }
    }

    /**
     * 获取文件夹中文件的个数
     *
     * @param fileInfo
     * @return
     */
    public static int getNumFilesInFolder(FileInfo fileInfo) {
        if (fileInfo.isDirectory() == false) return 0;
        File[] files = new File(fileInfo.getFilePath()).listFiles(ALL_FOLDER_AND_FILES_FILTER);
        if (files == null) return 0;
        return files.length;
    }

    /**
     * 文件管理器中文件列表图标显示:根据文件的类型来获取文件的图标
     */
    public static int getFileIconResource(FileInfo file) {
        if (file.isDirectory()) {
            return R.drawable.rc_ad_list_folder_icon;
        } else {
            return getFileTypeImageId(file.getFileName());
        }
    }

    private static int getFileTypeImageId(String fileName) {
        int id;
        if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_file_file_suffix)))
            id = R.drawable.rc_ad_list_file_icon;
        else if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_video_file_suffix)))
            id = R.drawable.rc_ad_list_video_icon;
        else if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_audio_file_suffix)))
            id = R.drawable.rc_ad_list_audio_icon;
        else
            id = R.drawable.rc_ad_list_other_icon;
        return id;
    }

    public static final int
            KILOBYTE = 1024,
            MEGABYTE = KILOBYTE * 1024,
            GIGABYTE = MEGABYTE * 1024,
            MAX_BYTE_SIZE = KILOBYTE / 2,
            MAX_KILOBYTE_SIZE = MEGABYTE / 2,
            MAX_MEGABYTE_SIZE = GIGABYTE / 2;

    /**
     * 将文件的大小转换成便于认识的字符串
     */
    public static String formatFileSize(long size) {
        if (size < MAX_KILOBYTE_SIZE)
            return String.format("%.2f K", (float) size / KILOBYTE);
        else if (size < MAX_MEGABYTE_SIZE)
            return String.format("%.2f M", (float) size / MEGABYTE);
        else
            return String.format("%.2f G", (float) size / GIGABYTE);
    }

    public String getSDCardPath() {
        String SDCardPath = null;
        String SDCardDefaultPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        if (SDCardDefaultPath.endsWith("/")) {
            SDCardDefaultPath = SDCardDefaultPath.substring(0, SDCardDefaultPath.length() - 1);
        }
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("mount");
            InputStream inputStream = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            String line;
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while ((line = bufferedReader.readLine()) != null) {
                if (line.toLowerCase().contains("sdcard") && line.contains(".android_secure")) {
                    String[] array = line.split(" ");
                    if (array != null && array.length > 1) {
                        String temp = array[1].replace("/.android_secure", "");
                        if (!SDCardDefaultPath.equals(temp)) {
                            SDCardPath = temp;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SDCardPath;
    }

    /* returns external storage paths (directory of external memory card) as array of Strings */
    public static String[] getExternalStorageDirectories(Context context) {

        List<String> results = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Method 1 for KitKat & above
            File[] externalDirs = context.getExternalFilesDirs(null);

            for (File file : externalDirs) {
                if (file != null) {
                    String path = file.getPath().split("/Android")[0];

                    boolean addPath;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        addPath = Environment.isExternalStorageRemovable(file);
                    } else {
                        addPath = Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(file));
                    }

                    if (addPath) {
                        results.add(path);
                    }
                }
            }
        }

        if (results.isEmpty()) { //Method 2 for all versions
            // better variation of: http://stackoverflow.com/a/40123073/5002496
            String output = "";
            try {
                final Process process = new ProcessBuilder().command("mount | grep /dev/block/vold")
                        .redirectErrorStream(true).start();
                process.waitFor();
                final InputStream is = process.getInputStream();
                final byte[] buffer = new byte[1024];
                while (is.read(buffer) != -1) {
                    output = output + new String(buffer);
                }
                is.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            if (!output.trim().isEmpty()) {
                String devicePoints[] = output.split("\n");
                for (String voldPoint : devicePoints) {
                    results.add(voldPoint.split(" ")[2]);
                }
            }
        }

        //Below few lines is to remove paths which may not be external memory card, like OTG (feel free to comment them out)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < results.size(); i++) {
                if (!results.get(i).toLowerCase().matches(".*[0-9a-f]{4}[-][0-9a-f]{4}")) {
                    results.remove(i--);
                }
            }
        } else {
            for (int i = 0; i < results.size(); i++) {
                if (!results.get(i).toLowerCase().contains("ext") && !results.get(i).toLowerCase().contains("sdcard")) {
                    results.remove(i--);
                }
            }
        }

        String[] storageDirectories = new String[results.size()];
        for (int i = 0; i < results.size(); ++i) storageDirectories[i] = results.get(i);

        return storageDirectories;
    }
}