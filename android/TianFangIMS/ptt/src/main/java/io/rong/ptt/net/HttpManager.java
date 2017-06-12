package io.rong.ptt.net;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by jiangecho on 2017/1/4.
 */

class HttpManager {
    private static final String TAG = "NetManager";

    private Executor executor;

    public HttpManager() {
        executor = Executors.newSingleThreadExecutor();
    }

    public void get(final String url, final ResponseCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = get(url);
                    if (callback != null) {
                        callback.onSuccess(response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFail(-1, e.getMessage());
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFail(e.getStatus(), e.getErrorMsg());
                    }
                }
            }
        });
    }

    public void post(final String url, final Map<String, String> params, final ResponseCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = post(url, params);
                    if (callback != null) {
                        callback.onSuccess(response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFail(-1, e.getMessage());
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFail(e.getStatus(), e.getErrorMsg());
                    }
                }

            }
        });
    }

    private String get(String url) throws IOException, HttpException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setReadTimeout(30 * 1000);
        connection.setDoInput(true);
        int responseCode;
        InputStream inputStream = null;
        String response = null;
        try {
            connection.connect();
            responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                inputStream = connection.getErrorStream();
                String errorMsg = readStream(inputStream);
                throw new HttpException(responseCode, errorMsg);
            } else {
                inputStream = connection.getInputStream();
                response = readStream(inputStream);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            connection.disconnect();
        }
        return response;
    }

    private String post(String url, Map<String, String> params) throws IOException, HttpException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setReadTimeout(30 * 1000);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        JSONObject jsonObject = new JSONObject(params);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", "" + jsonObject.toString().length());
        int responseCode;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String response = null;

        try {
            outputStream = connection.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(jsonObject.toString());
            outputStreamWriter.flush();
            outputStreamWriter.close();

            responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                inputStream = connection.getInputStream();
                response = readStream(inputStream);
            } else {
                inputStream = connection.getErrorStream();
                response = readStream(inputStream);
                throw new HttpException(responseCode, response);
            }

        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            connection.disconnect();
        }
        return response;
    }


    private String readStream(InputStream stream) throws IOException {
        final ByteArrayOutputStream responseData = new ByteArrayOutputStream(512);
        int c;
        while ((c = stream.read()) != -1) {
            responseData.write(c);
        }
        return responseData.toString();
    }

}
