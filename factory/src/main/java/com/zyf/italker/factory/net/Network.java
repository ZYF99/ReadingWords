package com.zyf.italker.factory.net;

/*
*单例模式，整个APP运行中只有一个
* 构建网络Retrofit的对象
* */

import com.zyf.italker.common.Common;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {

    private static Network instance;
    private Retrofit retrofit;

    static {
        instance = new Network();
    }
    private Network(){

    }
    public static Retrofit getYoudaoRetrofit(){
        if (instance.retrofit!=null){
            return instance.retrofit;
        }else {
            //构建OKHTTP对象
            OkHttpClient client = new OkHttpClient.Builder().build();
            //设置电脑链接
            Retrofit.Builder builder = new Retrofit.Builder();
            return builder.baseUrl(Common.Constance.YOUDAO_API_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

}
