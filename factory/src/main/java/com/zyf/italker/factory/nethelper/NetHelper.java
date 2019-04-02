package com.zyf.italker.factory.nethelper;


import android.util.Log;

import com.zyf.italker.factory.data.MainDataSource;
import com.zyf.italker.factory.model.api.translate.TransModel;
import com.zyf.italker.factory.model.api.translate.TransRspModel;
import com.zyf.italker.factory.net.Network;
import com.zyf.italker.factory.net.RemoteService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 网络请求的具体实现
 */

public class NetHelper {

    private static String TAG = "*******NetHelper******";


    public static void translateInToolBar(final TransModel transModel, final MainDataSource.Callback<TransRspModel> callback) {

        RemoteService service = Network.getYoudaoRetrofit().create(RemoteService.class);
        final Call<TransRspModel> transCallback = service.translate(transModel.getWord());

        transCallback.enqueue(new Callback<TransRspModel>() {
            @Override
            public void onResponse(Call<TransRspModel> call, Response<TransRspModel> response) {
                //从返回值中得到MODEL
                TransRspModel transRspModel = response.body();
                if (transRspModel.success()) {

                    callback.onTransLoaded(transRspModel);
                } else {
                    callback.onTransAvailable();

                    Log.d(TAG, "翻译返回错误代码：" + transRspModel.getErrorCode());
                }
            }

            @Override
            public void onFailure(Call<TransRspModel> call, Throwable t) {
                callback.onTransAvailable();
            }
        });


    }

    public static void translateInAdd(final TransModel transModel, final MainDataSource.Callback<TransRspModel> callback) {

        final RemoteService service = Network.getYoudaoRetrofit().create(RemoteService.class);
        final Call<TransRspModel> transCallback = service.translate(transModel.getWord());

        transCallback.enqueue(new Callback<TransRspModel>() {
            @Override
            public void onResponse(Call<TransRspModel> call, Response<TransRspModel> response) {
                //从返回值中得到MODEL
                final TransRspModel transRspModel = response.body();
                if (transRspModel.success()) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Thread.sleep(500);//休眠3秒
                                callback.onAddWindowTransLoaded(transRspModel);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }.start();


                } else {
                    callback.onAddWindowTransAvailable();

                    Log.d(TAG, "翻译返回错误代码：" + transRspModel.getErrorCode());
                }
            }

            @Override
            public void onFailure(Call<TransRspModel> call, Throwable t) {
                callback.onAddWindowTransAvailable();
            }
        });


    }

}
