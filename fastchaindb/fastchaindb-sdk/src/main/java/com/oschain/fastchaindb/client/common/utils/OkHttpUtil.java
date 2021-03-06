package com.oschain.fastchaindb.client.common.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.*;

import static com.oschain.fastchaindb.client.common.utils.AbstractApi.JSON;

/**
 * author: kevin
 * date:   2019/5/30 16:38
 */

public class OkHttpUtil {

    private static OkHttpClient okHttpClient;
    private static OkHttpClient getInstance() {

        if(okHttpClient == null){

            // 设置缓存大小
            int cacheSize = 100 * 1024 * 1024;
            File cacheDirectory = null;
            try {
                cacheDirectory = Files.createTempDirectory("cache").toFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Cache cache = new Cache(cacheDirectory, cacheSize);

            synchronized (OkHttpUtil.class){
                okHttpClient = new OkHttpClient.Builder() // 构建者
                        .connectTimeout(15, TimeUnit.SECONDS) // 连接超时
                        .readTimeout(30,TimeUnit.SECONDS)     // 读取超时
                        .writeTimeout(30,TimeUnit.SECONDS)    // 写入超时
                        .cache(cache) // 设置缓存
                        .build(); // 闭环
            }
        }
        return okHttpClient;
    }

    /**
     * get请求
     */

    public static Response doGet(String url) throws IOException {
        // 获取OkHttpClient对象
        OkHttpClient mHttpClient = getInstance();
        // 获取Request对象
        Request request = new Request.Builder().url(url).get().build();
        // 获取Call对象
        return mHttpClient.newCall(request).execute();
    }

    /**
     * get请求
     */

    public static  void doGet(String url, Callback callback){
        // 获取OkHttpClient对象
        OkHttpClient mHttpClient = getInstance();
        // 获取Request对象
        Request request = new Request.Builder().url(url).get().build();
        // 获取Call对象
        Call call = mHttpClient.newCall(request);
        // 执行异步请求
        call.enqueue(callback);
    }


    /**
     * post请求
     */
    public static  void doPost(String url, Map<String,String> parameters,final HttpCallback callback){
        // 获取OkHttpClient对象
        OkHttpClient mHttpClient = getInstance();
        // 获取构建者
        FormBody.Builder builder = new FormBody.Builder();
        // 遍历集合
        for (String key: parameters.keySet()) {
            // 添加上传的参数
            builder.add(key,parameters.get(key));
        }
        // 获取Request对象
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        // 获取Call
        mHttpClient.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() >= 300) {
                    callback.error(response.body().string());
                } else {
                    callback.success(response.body().string());
                }
                response.close();
            }
        });
    }

    /**
     * post请求
     */
    public static void doPost(String url, String jsonBody,final HttpCallback callback){
        // 获取OkHttpClient对象
        OkHttpClient mHttpClient = getInstance();
        RequestBody body = RequestBody.create(JSON, jsonBody);
        // 获取Request对象
        Request request = new Request.Builder().url(url).post(body).build();

        // 获取Call
         mHttpClient.newCall(request).enqueue(new Callback() {
             public void onFailure(Call call, IOException e) {
                 e.printStackTrace();
             }

             public void onResponse(Call call, Response response) throws IOException {
                 if (response.code() >= 300) {
                     callback.error(response.body().string());
                 } else {
                     callback.success(response.body().string());
                 }
                 response.close();
             }
         });
    }


    /**
     * post请求
     */
    public static  Response doPost(String url, Map<String,String> parameters) throws IOException{
        // 获取OkHttpClient对象
        OkHttpClient mHttpClient = getInstance();
        // 获取构建者
        FormBody.Builder builder = new FormBody.Builder();
        // 遍历集合
        for (String key: parameters.keySet()) {
            // 添加上传的参数
            builder.add(key,parameters.get(key));
        }
        // 获取Request对象
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        // 获取Call
        Call call = mHttpClient.newCall(request);
        // 执行请求
        return call.execute();
    }

    /**
     * post请求
     */
    public static Response doPost(String url, String jsonBody) throws IOException{
        // 获取OkHttpClient对象
        OkHttpClient mHttpClient = getInstance();
        RequestBody body = RequestBody.create(JSON, jsonBody);
        // 获取Request对象
        Request request = new Request.Builder().url(url).post(body).build();

        // 获取Call
        Call call = mHttpClient.newCall(request);
        // 执行请求
        return call.execute();
    }

}