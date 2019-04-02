package com.zyf.italker.factory.net;

import com.zyf.italker.factory.model.api.translate.TransRspModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RemoteService {

    @GET("/translate?&doctype=json&type=AUTO")
    Call<TransRspModel> translate(@Query("i") String word);

}
