package com.zyf.italker.factory.data;

public interface MainDataSource {
    interface Callback<T>extends MainDataSource.transSucceedCallback<T>, MainDataSource.transFailedCallback {

    }
    interface transSucceedCallback<T>{
        void onTransLoaded(T t);
        void onAddWindowTransLoaded(T t);
    }
    interface transFailedCallback{
        void onTransAvailable();
        void onAddWindowTransAvailable();
    }
}
