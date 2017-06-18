package com.tianfangIMS.im.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private StringUtils() {
    }

    private static class Inner {
        private static final StringUtils SU = new StringUtils();
    }

    public static StringUtils getInstance() {
        return Inner.SU;
    }

    public boolean isBlank(String str) {
        return str == null || "".equals(str);
    }

    public boolean isArrayBlank(String[] strs) {
        return strs == null || strs.length == 0;
    }

    public boolean isNull(Object o) {
        return o == null;
    }

    public String careNull(String str) {
        return str == null ? "" : str;
    }

    public int strToInt(String str) {

        str = str.replace("\"", "");

        if (isNumeric(str)) {
            return Integer.parseInt(str);
        } else {
            return -1;
        }
    }

    public long strToLong(String str) {
        str = str.replace("\"", "");

        if (isNumeric(str)) {
            return Long.parseLong(str);
        } else {
            return -1;
        }
    }

    public String replaceChar(String str, String srcChars, String distChars) {
        if (!isBlank(str) && !isBlank(srcChars) && !isNull(distChars)) {
            return str.replace(srcChars, distChars);
        }
        return str;
    }

    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public boolean isStartChar(String str, String seper) {
        return str.startsWith(seper);
    }

    public boolean isEndChar(String str, String seper) {
        return str.endsWith(seper);
    }

    public String subString(String str, int start, int end) {
        return str.substring(start, end);
    }

    public String clearNumPoint(String str) {
        if (str != null) {
            int pos = str.lastIndexOf(".");

            if (pos == -1) {
                return str;
            }
            return str.substring(0, pos);
        }
        return str;
    }

    public String[] stringSplit(String str, String seper) {
        if (!isBlank(str)) {
            return str.split(seper);
        }

        return null;
    }

    public Integer[] stringArrToIntArr(String[] str) {

        if (!isArrayBlank(str)) {
            Integer[] tempIds = new Integer[str.length];

            for (int i = 0; i < str.length; i++) {
                tempIds[i] = strToInt(str[i]);
            }

            return tempIds;
        }

        return null;
    }

    public List<Integer> stringArrToListInt(String[] str) {

        if (!isArrayBlank(str)) {
            List<Integer> tempIds = new ArrayList<Integer>();

            for (int i = 0; i < str.length; i++) {
                tempIds.add(strToInt(str[i]));
            }

            return tempIds;
        }

        return null;

    }

    /**
     * 去除重复数据元素
     *
     * @param strArr
     * @return
     */
    public String[] clearRepeat(String[] strArr) {
        ArrayList<String> list = new ArrayList<String>();

        int j = 0;

        for (int i = 0; i < strArr.length; i++) {
            if (!list.contains(strArr[i])) {
                list.add(strArr[i]);
            }
        }

        int len = list.size();
        String[] temp = new String[len];

        for (int i = 0; i < len; i++) {
            temp[i] = list.get(i);
        }

        return temp;
    }

    public String getRandomString(String str, int len) {
        StringBuilder sb = new StringBuilder();

        char[] ch = str.toCharArray();
        Random r = new Random();

        for (int i = 0; i < len; i++) {
            int t = r.nextInt(len);
            sb.append(ch[t]);
        }

        return sb.toString();
    }

    /**
     * 数组字符串转数组
     *
     * @param str "[x,x,x]"
     * @return
     */
    public String[] strToArray(String str) {
        str = str.replace("[", "");
        str = str.replace("]", "");
        str = str.replace("\"", "");
        String[] arr = str.split(",");

        for (int i = 0; i < arr.length; i++) {
            arr[i].trim();
        }
        return arr;
    }

    // 判断一个字符串是否都为数字
    public static boolean isDigit(String strNum) {
        return strNum.matches("[0-9]{1,}");
    }

    /**
     * 按字母表顺序排序
     *
     * @param pStr
     * @return
     */
    public String sortByChars(String pStr) {
        if (pStr != null) {
            char[] c = pStr.toCharArray();
            Arrays.sort(c);
            return new String(c);
        }
        return null;
    }

    public String getMD5Str(String pwdContext) {
        String result = null;

        char hexs[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
                'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] source = pwdContext.getBytes("UTF-8");
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(source);
            byte md5Digest[] = md5.digest();
            char str[] = new char[16 * 2];
            int c = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = md5Digest[i];
                str[c++] = hexs[byte0 >>> 4 & 0xf];
                str[c++] = hexs[byte0 & 0xf];
            }
            result = new String(str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

}
