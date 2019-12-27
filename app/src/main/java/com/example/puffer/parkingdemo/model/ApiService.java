package com.example.puffer.parkingdemo.model;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiService {
    // 會返回一個 call 類別
    @GET
    //@Headers("Content-Type:application/octet-stream")
    Single<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);
}
