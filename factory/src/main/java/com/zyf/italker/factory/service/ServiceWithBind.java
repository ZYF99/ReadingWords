package com.zyf.italker.factory.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zyf.italker.factory.service.main.MainService;

/**
 * 带与主界面绑定的service
 */

public abstract class ServiceWithBind extends Service {

    String TAG = "ServiceWithBind";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public abstract IBinder onBind(Intent intent);

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }




}

