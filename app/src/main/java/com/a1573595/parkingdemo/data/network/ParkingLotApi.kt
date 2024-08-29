package com.a1573595.parkingdemo.data.network

import okhttp3.ResponseBody
import retrofit2.http.GET

fun interface ParkingLotApi {
    @GET("TCMSV_alldesc.gz")
//    suspend fun getParkingLotDataSet(): Response<ResponseBody>
    suspend fun getParkingLotDataSet(): ResponseBody
}