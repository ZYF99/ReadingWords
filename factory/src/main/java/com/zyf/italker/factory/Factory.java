package com.zyf.italker.factory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Factory {

    //全局单例
    private static Factory instance;
    //全局线程池
    private static Executor executor;

    //全局gson
    private final Gson gson;

    static {
        instance = new Factory();
    }


    public Factory(){
        //新建一个4个线程的线程池
        executor = Executors.newFixedThreadPool(4);
        //构建gson
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS")
                .create();
    }

    /**
    * 异步执行的方法
    * */
    public static void runOnAsync(Runnable runnable){
        //拿到单例，拿到线程池，然后异步执行
        instance.executor.execute(runnable);
    }

    public static Gson getGson(){

            return instance.gson;


    }
}
