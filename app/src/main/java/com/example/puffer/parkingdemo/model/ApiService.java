package com.example.puffer.parkingdemo.model;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiService {
    // 會返回一個 call 類別
    @GET
    //@Headers("Content-Type:application/octet-stream")
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);
}
