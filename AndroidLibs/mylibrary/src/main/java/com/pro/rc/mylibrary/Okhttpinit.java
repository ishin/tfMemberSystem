package com.pro.rc.mylibrary;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;

import java.util.logging.Level;




public class Okhttpinit {

    public static void init_http(Application application) {
        OkGo.init(application);
        if(EncryptUtil.IsHttpUtils()) {
            return;

        }else{
			try {
                okhttpUtil.gethttpUtils();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
		}
    }
}
