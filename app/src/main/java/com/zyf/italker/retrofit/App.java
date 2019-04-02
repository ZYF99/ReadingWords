package com.zyf.italker.retrofit;

import com.zyf.italker.common.app.Application;
import com.zyf.italker.factory.SharedPreferencesUtil;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //开启一个伴随app的sharedpreference存储
        new SharedPreferencesUtil(getApplicationContext(), "local");
    }

}
