package com.tianfangIMS.im.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by LianMengYu on 2017/3/7.
 */

public class NewList {
    private List<String> data = new ArrayList<String>(5);
    private int sum = 5;
    ReentrantLock lock = new ReentrantLock();
    public void add(String da) {
        lock.lock();
        try {
            //执行某些操作
            if (data.size() < sum) {
                data.add(da);
            }
            if (data.size() >= sum) {
                data.remove(0);
                data.add(da);
            }
        }finally {
            lock.unlock();
        }
    }

    public void getNew5Data() {
        int size = data.size();
        for (int i = size - 1; i >= 0; i--) {
            Log.e("APP", "--老秦-:" + data.get(i));
        }
    }

//    public static void main(Message message) {
//        NewList list = new NewList();
//        for (int i = 0; i < 10; i++) {
//            list.add(message.getTargetId()+ "");
//        }
//        list.getNew5Data();
//    }
}
