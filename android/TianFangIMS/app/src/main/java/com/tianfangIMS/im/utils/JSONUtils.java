package com.tianfangIMS.im.utils;


import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.bean.ParentModel;
import com.tianfangIMS.im.bean.SonModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu.
 * Date 17/2/5.
 * 这个类负责解析json串
 */

public class JSONUtils {

    private void GetData(String str) {

    }

    public static List<ParentModel> parentModelList;
    public static List<SonModel> sonModelList;

    private static final String TAG = "JSONUtils";


    public JSONUtils() {
        OkGo.post(ConstantValue.DEPARTMENTPERSON)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (!TextUtils.isEmpty(s)) {
                            Gson gson = new Gson();
                            parentModelList = new ArrayList<>();
                            sonModelList = new ArrayList<>();

                            try {
                                JSONArray jsonArray = new JSONArray(s);
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        int flag = jsonObject.optInt("flag");
                                        ParentModel parentModel;
                                        SonModel sonModel;
                                        if (flag == 0) {
                                            parentModel = gson.fromJson(jsonObject.toString(), ParentModel.class);
                                            parentModelList.add(parentModel);
                                        } else if (flag == 1) {
                                            sonModel = gson.fromJson(jsonObject.toString(), SonModel.class);
                                            sonModelList.add(sonModel);
                                        }
                                    }

                                    Log.d(TAG, "JSONUtils: " + parentModelList.toString());
                                    Log.d(TAG, "JSONUtils: " + sonModelList.toString());
                                    Log.d(TAG, "JSONUtils: " + parentModelList.size());
                                    Log.d(TAG, "JSONUtils: " + sonModelList.size());
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "JSONUtils:  错误信息" + e.toString());
                                e.printStackTrace();
                            }
                        }
                    }
                });

    }

    private final String JSON1 = "[\n" +
            "    {\n" +
            "        \"id\": \"1\",\n" +
            "        \"pid\": -1,\n" +
            "        \"name\": \"天坊信息\",\n" +
            "        \"flag\": 0\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"1\",\n" +
            "        \"pid\": \"0\",\n" +
            "        \"name\": \"销售部\",\n" +
            "        \"flag\": 0\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"2\",\n" +
            "        \"pid\": \"0\",\n" +
            "        \"name\": \"产品部\",\n" +
            "        \"flag\": 0\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"3\",\n" +
            "        \"pid\": \"1\",\n" +
            "        \"name\": \"销售一部\",\n" +
            "        \"flag\": 0\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"4\",\n" +
            "        \"pid\": \"2\",\n" +
            "        \"name\": \"产品一部\",\n" +
            "        \"flag\": 0\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"5\",\n" +
            "        \"pid\": \"1\",\n" +
            "        \"name\": \"销售二部\",\n" +
            "        \"flag\": 0\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"6\",\n" +
            "        \"pid\": \"2\",\n" +
            "        \"name\": \"产品二部\",\n" +
            "        \"flag\": 0\n" +
            "    },\n" +
            "    {\n" +
            "        \"flag\": 0,\n" +
            "        \"id\": \"7\",\n" +
            "        \"pid\": \"3\",\n" +
            "        \"name\": \"销售一部一组\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"flag\": 1,\n" +
            "        \"pid\": \"7\",\n" +
            "        \"id\": \"1\",\n" +
            "        \"account\": \"12345678901\",\n" +
            "        \"name\": \"TestAccount\",\n" +
            "        \"logo\": \"timg.jpg\",\n" +
            "        \"telephone\": \"13838383835\",\n" +
            "        \"email\": \"test@163.com\",\n" +
            "        \"address\": \"地球\",\n" +
            "        \"token\": \"HvEmG0PGwWgVcTdlV/XpLo1i9uq7VKB93TSM2Us1wbj7+hgwkCfSOmJigHoDWrYXoW/jqtydpP8=\",\n" +
            "        \"sex\": \"1\",\n" +
            "        \"birthday\": \"20010101\",\n" +
            "        \"workno\": \"N0001\",\n" +
            "        \"mobile\": \"13838383838\",\n" +
            "        \"groupmax\": \"10\",\n" +
            "        \"groupuse\": \"0\",\n" +
            "        \"intro\": \"\",\n" +
            "        \"postitionid\": \"2\",\n" +
            "        \"postitionname\": \"职员\",\n" +
            "        \"sexid\": \"1\",\n" +
            "        \"sexname\": \"男\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"flag\": 0,\n" +
            "        \"id\": \"8\",\n" +
            "        \"pid\": \"4\",\n" +
            "        \"name\": \"产品一部一组\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"flag\": 1,\n" +
            "        \"pid\": \"8\",\n" +
            "        \"id\": \"2\",\n" +
            "        \"account\": \"15910832272\",\n" +
            "        \"name\": \"N0001\",\n" +
            "        \"logo\": \"2-1485065814_80_80.jpg\",\n" +
            "        \"telephone\": \"10\",\n" +
            "        \"email\": \"test4@163.com\",\n" +
            "        \"address\": \"0\",\n" +
            "        \"token\": \"NY2wh6G8q0+rkpsUaSdsmjf+Kg4+7HNZg9Vo04FRnx06kxnCGSosnFK0TflKbXKIpiGPzyGmcmTqvC9QyFSSLg==\",\n" +
            "        \"sex\": \"1\",\n" +
            "        \"birthday\": \"20010203\",\n" +
            "        \"workno\": \"N0002\",\n" +
            "        \"mobile\": \"地球\",\n" +
            "        \"groupmax\": \"0\",\n" +
            "        \"groupuse\": \"0\",\n" +
            "        \"intro\": \"1484198731\",\n" +
            "        \"postitionid\": \"2\",\n" +
            "        \"postitionname\": \"职员\",\n" +
            "        \"sexid\": \"1\",\n" +
            "        \"sexname\": \"男\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"flag\": 0,\n" +
            "        \"id\": \"9\",\n" +
            "        \"pid\": \"0\",\n" +
            "        \"name\": \"技术部\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"flag\": 1,\n" +
            "        \"pid\": \"9\",\n" +
            "        \"id\": \"5\",\n" +
            "        \"account\": \"15210162827\",\n" +
            "        \"name\": \"童译信\",\n" +
            "        \"logo\": \"5-1485003424_80_80.jpg\",\n" +
            "        \"telephone\": \"15210162827\",\n" +
            "        \"email\": \"15210162827@qq.com\",\n" +
            "        \"address\": \"北京\",\n" +
            "        \"token\": \"3LlnHDqyp66HOBASuKIKjzf+Kg4+7HNZg9Vo04FRnx06kxnCGSosnJta2zZOV/kXijbUNXesV0rqvC9QyFSSLg==\",\n" +
            "        \"sex\": \"1\",\n" +
            "        \"birthday\": \"20000101\",\n" +
            "        \"workno\": \"NO15210162827\",\n" +
            "        \"mobile\": \"15210162827\",\n" +
            "        \"groupmax\": \"100\",\n" +
            "        \"groupuse\": \"0\",\n" +
            "        \"intro\": \"\",\n" +
            "        \"postitionid\": \"1\",\n" +
            "        \"postitionname\": \"总监\",\n" +
            "        \"sexid\": \"1\",\n" +
            "        \"sexname\": \"男\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"flag\": 1,\n" +
            "        \"pid\": \"9\",\n" +
            "        \"id\": \"6\",\n" +
            "        \"account\": \"15542315736\",\n" +
            "        \"name\": \"朱金倩\",\n" +
            "        \"logo\": \"timg.jpg\",\n" +
            "        \"telephone\": \"15542315736\",\n" +
            "        \"email\": \"15542315736@qq.com\",\n" +
            "        \"address\": \"北京\",\n" +
            "        \"token\": \"QCuD43JZz79v3W6QsMBNHl7uxSksQ/iP8MdAvKI0ax/NpYeN8IWpG9A2iERl/EhwcdSsBMmVDjB/oYaH97taoQ==\",\n" +
            "        \"sex\": \"2\",\n" +
            "        \"birthday\": \"20000101\",\n" +
            "        \"workno\": \"NO15542315736\",\n" +
            "        \"mobile\": \"15542315736\",\n" +
            "        \"groupmax\": \"100\",\n" +
            "        \"groupuse\": \"0\",\n" +
            "        \"intro\": \"\",\n" +
            "        \"postitionid\": \"2\",\n" +
            "        \"postitionname\": \"职员\",\n" +
            "        \"sexid\": \"2\",\n" +
            "        \"sexname\": \"女\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"flag\": 1,\n" +
            "        \"pid\": \"9\",\n" +
            "        \"id\": \"7\",\n" +
            "        \"account\": \"15210548389\",\n" +
            "        \"name\": \"高英娜\",\n" +
            "        \"logo\": \"timg.jpg\",\n" +
            "        \"telephone\": \"15210548389\",\n" +
            "        \"email\": \"15210548389@qq.com\",\n" +
            "        \"address\": \"北京\",\n" +
            "        \"token\": \"a3txs837xWZE6EJS1lymbTf+Kg4+7HNZg9Vo04FRnx06kxnCGSosnL/Tpwf2Nej8k2ubnTOr0hDqvC9QyFSSLg==\",\n" +
            "        \"sex\": \"2\",\n" +
            "        \"birthday\": \"20000101\",\n" +
            "        \"workno\": \"NO15210548389\",\n" +
            "        \"mobile\": \"15210548389\",\n" +
            "        \"groupmax\": \"100\",\n" +
            "        \"groupuse\": \"0\",\n" +
            "        \"intro\": \"\",\n" +
            "        \"postitionid\": \"2\",\n" +
            "        \"postitionname\": \"职员\",\n" +
            "        \"sexid\": \"2\",\n" +
            "        \"sexname\": \"女\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"flag\": 1,\n" +
            "        \"pid\": \"9\",\n" +
            "        \"id\": \"8\",\n" +
            "        \"account\": \"18612755630\",\n" +
            "        \"name\": \"郝冬勇\",\n" +
            "        \"logo\": \"timg.jpg\",\n" +
            "        \"telephone\": \"18612755630\",\n" +
            "        \"email\": \"123456@qq.com\",\n" +
            "        \"address\": \"北京\",\n" +
            "        \"token\": \"+iVU2lxoqONH0LL7c0YAOzf+Kg4+7HNZg9Vo04FRnx06kxnCGSosnDLxfw2T+NLBjqMVbZbE0GXqvC9QyFSSLg==\",\n" +
            "        \"sex\": \"1\",\n" +
            "        \"birthday\": \"20000101\",\n" +
            "        \"workno\": \"NO18612755630\",\n" +
            "        \"mobile\": \"18612755630\",\n" +
            "        \"groupmax\": \"100\",\n" +
            "        \"groupuse\": \"0\",\n" +
            "        \"intro\": \"\",\n" +
            "        \"postitionid\": \"2\",\n" +
            "        \"postitionname\": \"职员\",\n" +
            "        \"sexid\": \"1\",\n" +
            "        \"sexname\": \"男\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"flag\": 1,\n" +
            "        \"pid\": \"9\",\n" +
            "        \"id\": \"10\",\n" +
            "        \"account\": \"18612206582\",\n" +
            "        \"name\": \"jack1\",\n" +
            "        \"logo\": \"timg.jpg\",\n" +
            "        \"telephone\": \"18612206582\",\n" +
            "        \"email\": \"18612206582@qq.com\",\n" +
            "        \"address\": \"北京\",\n" +
            "        \"token\": \"LxeNUBFz4oX7TnaESainV17uxSksQ/iP8MdAvKI0ax/NpYeN8IWpG0vdo8/FgFrA4+Mgh6WnE9p/oYaH97taoQ==\",\n" +
            "        \"sex\": \"1\",\n" +
            "        \"birthday\": \"20010101\",\n" +
            "        \"workno\": \"NO18612206582\",\n" +
            "        \"mobile\": \"18612206582\",\n" +
            "        \"groupmax\": \"100\",\n" +
            "        \"groupuse\": \"0\",\n" +
            "        \"intro\": \"\",\n" +
            "        \"postitionid\": \"2\",\n" +
            "        \"postitionname\": \"职员\",\n" +
            "        \"sexid\": \"1\",\n" +
            "        \"sexname\": \"男\"\n" +
            "    }\n" +
            "]";
}
