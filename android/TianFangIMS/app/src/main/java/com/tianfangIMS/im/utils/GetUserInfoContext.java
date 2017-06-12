package com.tianfangIMS.im.utils;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;

import io.rong.imlib.model.UserInfo;

/**
 * Created by LianMengYu on 2017/2/9.
 */

public class GetUserInfoContext {

    private static GetUserInfoContext mGetUserInfoContext;
    private Context mContext;
    private ArrayList<UserInfo> mUserInfo;

    public static void init(Context context) {
        mGetUserInfoContext = new GetUserInfoContext(context);
    }
    public static GetUserInfoContext getInstance(){
        if(mGetUserInfoContext ==null){
            mGetUserInfoContext = new GetUserInfoContext();
        }
        return mGetUserInfoContext;
    }
    private GetUserInfoContext(){

    }

    public GetUserInfoContext(Context mContext) {
        this.mContext = mContext;
        mGetUserInfoContext = this;
    }
    public ArrayList<UserInfo> getmUserInfos() {

        return mUserInfo;
    }
    public UserInfo getUserInfoByUserId(String userId){
        UserInfo userInfoReturn = null;
        if (!TextUtils.isEmpty(userId) && mUserInfo != null) {
            for (UserInfo userInfo : mUserInfo) {

                if (userId.equals(userInfo.getUserId())) {
                    userInfoReturn = userInfo;
                    break;
                }
            }
        }
        return userInfoReturn;
    }
}
