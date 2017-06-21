package com.tianfangIMS.im.utils;

import io.rong.imkit.RongContext;

/**
 * Created by Raink on 2017/4/30.
 */

public class FileMessageTypeUtils {

    public static String fileMessageType(String fileName) {
        String type;
        if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(io.rong.imkit.R.array.rc_image_file_suffix)))
            type = "IMAGE";
        else if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(io.rong.imkit.R.array.rc_file_file_suffix)))
            type = "TXT";
        else if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(io.rong.imkit.R.array.rc_video_file_suffix)))
            type = "VIDEO";
        else if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(io.rong.imkit.R.array.rc_audio_file_suffix)))
            type = "AUDIO";
        else if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(io.rong.imkit.R.array.rc_word_file_suffix)))
            type = "WORD";
        else if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(io.rong.imkit.R.array.rc_excel_file_suffix)))
            type = "EXCEL";
        else
            type = "UN_KNOW";
        return type;
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
}
