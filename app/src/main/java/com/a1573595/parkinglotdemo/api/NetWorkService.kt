package com.a1573595.parkinglotdemo.api

import com.a1573595.parkinglotdemo.BuildConfig
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import java.util.concurrent.TimeUnit

class NetWorkService private constructor() {
    companion object {
        val instance: NetWorkService by lazy { NetWorkService() }
    }

    var api: API

    init {
        val logger = HttpLoggingInterceptor()
        logger.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(logger)
            .build()

        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .baseUrl("https://tcgbusfs.blob.core.windows.net/blobtcmsv/")
            .client(client)
            .build()

        api = retrofit.create(API::class.java)
    }
}