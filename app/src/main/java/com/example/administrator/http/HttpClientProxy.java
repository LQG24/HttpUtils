package com.example.administrator.http;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/11/16.
 */

public class HttpClientProxy {
    private OkHttpClient mOkHttpClient;
    private static  HttpClientProxy mInstance;
    private Handler mHandler;
    private Gson mGson;

    private HttpClientProxy(){
        mOkHttpClient = new OkHttpClient();
        //设置连接的超时时间
        mOkHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        // 设置响应的超时时间
        mOkHttpClient.setWriteTimeout(30,TimeUnit.SECONDS);
        //请求的超时时间
        mOkHttpClient.setReadTimeout(10,TimeUnit.SECONDS);
        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
        mHandler = new Handler(Looper.getMainLooper());
        mGson = new Gson();
    }
    public static  HttpClientProxy getInstance(){
        if(mInstance == null){
            synchronized (HttpClientProxy.class){
                if(mInstance == null){
                    mInstance = new HttpClientProxy();
                }
            }
        }
        return  mInstance;
    }


    /**
     * Get 请求
    * */

    public void requestGet(String url,HttpCallback callback){
        Request request = new Request.Builder().url(url).build();
        deliveryResult(callback,request);
    }


    /**
     * Post请求
    * */
    public  void requestPost(String url,HttpCallback callback,List<Param> params){
        Request request = bulidRequest(url,params);
        deliveryResult(callback,request);
    }


    /**
     * 该方法用来对Post请求的request添加请求参数
     */
    private Request bulidRequest(String url, List<Param> params)
    {
// FormBody.Builder builder = new FormBody.Builder();
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Param param : params)
        {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).patch(requestBody).build();
    }



    private void deliveryResult(final HttpCallback callback, Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                   callback.onFailed(e);
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(response.body().toString());
                    }
                });

            }
        });
    }


    public interface HttpCallback<T> {
        void onSuccess(T t);

        void onFailed(Exception errorMsg);
    }

    public static class Param {
        String key;
        String value;

        public Param(String key,String value){
            this.key = key;
            this.value = value;
        }
    }
}
