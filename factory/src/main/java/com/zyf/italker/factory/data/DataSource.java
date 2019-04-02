package com.zyf.italker.factory.data;

public interface DataSource {
    interface Callback<T>extends succeedCallback<T>,failedCallback{

    }
    interface succeedCallback<T>{
        void onDataLoaded(T t);
    }
    interface failedCallback{
        void onDataAvailable();
    }
}
