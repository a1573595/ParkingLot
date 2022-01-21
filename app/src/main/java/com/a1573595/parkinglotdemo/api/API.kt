package com.a1573595.parkinglotdemo.api

import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface API {
    @GET
    fun downloadFileWithDynamicUrlSync(@Url fileUrl: String): Single<ResponseBody>
}