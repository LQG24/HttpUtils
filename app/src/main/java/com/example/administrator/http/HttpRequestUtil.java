package com.example.administrator.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/11/20.
 */

public class HttpRequestUtil {

    public static void requestData(final String urlStr, final HttpCallback listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(urlStr);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    InputStream in = connection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    if (listener != null) {
                        //回调onFinish方法
                        listener.onSuccess(sb.toString());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        //回调onError方法
                        listener.onFailed(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * 发送POST请求
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws Exception
     */

    public static void sendPostRequest(final String url,
                                       final Map<String, String> params, final Map<String, String> headers, final HttpCallback listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder buf = new StringBuilder();
                Set<Map.Entry<String, String>> entrys = null;
                // 如果存在参数，则放在HTTP请求体，形如name=aaa&age=10
                if (params != null && !params.isEmpty()) {
                    entrys = params.entrySet();
                    for (Map.Entry<String, String> entry : entrys) {
                        try {
                            buf.append(entry.getKey()).append("=")
                                    .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                                    .append("&");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    buf.deleteCharAt(buf.length() - 1);
                }
                HttpURLConnection connection = null;

                try {
                    URL url1 = new URL(url);
                    connection = (HttpURLConnection) url1.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoOutput(true);
                    OutputStream out = connection.getOutputStream();
                    out.write(buf.toString().getBytes("UTF-8"));
                    if (headers != null && !headers.isEmpty()) {
                        entrys = headers.entrySet();
                        for (Map.Entry<String, String> entry : entrys) {
                            connection.setRequestProperty(entry.getKey(), entry.getValue());
                        }
                    }
                    InputStream in = connection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    if (listener != null) {
                        //回调onFinish方法
                        listener.onSuccess(sb.toString());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        //回调onError方法
                        listener.onFailed(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }

            }
        }).start();
    }


    public interface HttpCallback<T> {
        void onSuccess(T t);

        void onFailed(Exception errorMsg);
    }
}
