package com.tianfangIMS.im.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    private static Gson gson = null;


    static {
        if (gson == null) {
            gson = new Gson();
        }
    }

    /**
     * 在json字符串中 找一个值
     *
     * @param
     * @return
     * @serialData
     */
    public static String getMsg(String jsonString, String name) {

        String Msg = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            Msg = jsonObject.getString(name);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Msg;
    }


    public static int getMsgInt(String jsonString, String name) {

        int Msg = 0;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            Msg = jsonObject.getInt(name);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Msg;
    }


    public static double getMsgDouble(String jsonString, String name) {

        double Msg = 0;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            Msg = jsonObject.getDouble(name);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Msg;
    }


    public static boolean getMsgBoolean(String jsonString, String name) {

        boolean Msg = false;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            Msg = jsonObject.getBoolean(name);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Msg;
    }

    /**
     * 将对象转换成json格式
     */
    public static String toJson(Object obj) {
        String jsonStr = null;
        if (gson != null) {
            jsonStr = gson.toJson(obj);
        }
        return jsonStr;
    }


    public static Object toObject(String jsonString, Object obj) {
        if (gson == null) {
            gson = new Gson();

        }
        return gson.fromJson(jsonString, obj.getClass());
    }

    /**
     * @param <T>        不要管客户端传递的数据类型，只要保证服务器和客户端所用的类型一致即可
     * @param jsonString
     * @param cls
     * @return 网上Down
     */

    public static <T> T getPerson(String jsonString, Class<T> cls) {
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, cls);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return t;
    }

    /**
     * 使用Gson进行解析 List<Person>
     *
     * @param <T>
     * @param jsonString
     * @param cls
     * @return
     */
    public static <T> List<T> getPersons(String jsonString, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            Gson gson = new Gson();
            // 这里的TypeToken是google提供的反射机制，避免像纯粹的JSON解析时复杂的迭代
            list = gson.fromJson(jsonString, new TypeToken<List<T>>() {
            }.getType());
        } catch (Exception e) {
        }
        return list;
    }

    public static <T> List<T> getObjectList(String jsonString, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            Gson gson = new Gson();
            JsonArray arry = new JsonParser().parse(jsonString).getAsJsonArray();
            for (JsonElement jsonElement : arry) {
                list.add(gson.fromJson(jsonElement, cls));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static <T> T getPersons(String jsonString, Type type) {
        if (gson == null) {
            gson = new Gson();
        }
        return gson.fromJson(jsonString, type);
    }

    /**
     * @param jsonString
     * @return
     */
    public static List<String> getList(String jsonString) {
        List<String> list = new ArrayList<String>();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(jsonString, new TypeToken<List<String>>() {
            }.getType());
        } catch (Exception e) {
            // TODO: handle exception
        }
        return list;
    }

    public static List<Map<String, Object>> listKeyMaps(String jsonString) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(jsonString,
                    new TypeToken<List<Map<String, Object>>>() {
                    }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}